package com.patrykandpatrick.liftapp.functionality.database.workout

import androidx.room.InvalidationTracker
import androidx.room.RoomRawQuery
import com.patrykandpatrick.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatrick.liftapp.domain.workout.DeleteWorkoutContract
import com.patrykandpatrick.liftapp.domain.workout.EditWorkoutItemsContract
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutPageContract
import com.patrykandpatrick.liftapp.domain.workout.GetPastWorkoutsInRangeContract
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutContract
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsByDateContract
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsContract
import com.patrykandpatrick.liftapp.domain.workout.UpdateExerciseNotesContract
import com.patrykandpatrick.liftapp.domain.workout.UpdateWorkoutContract
import com.patrykandpatrick.liftapp.domain.workout.UpsertExerciseSetContract
import com.patrykandpatrick.liftapp.domain.workout.UpsertWorkoutGoalContract
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.functionality.database.converter.LocalDateTimeConverters
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomWorkoutRepository
@Inject
constructor(
    private val workoutDao: WorkoutDao,
    private val workoutMapper: WorkoutMapper,
    private val invalidationTracker: InvalidationTracker,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    coroutineExceptionHandler: CoroutineExceptionHandler,
) :
    GetWorkoutContract,
    UpsertWorkoutGoalContract,
    UpsertExerciseSetContract,
    UpdateWorkoutContract,
    UpdateExerciseNotesContract,
    GetWorkoutsContract,
    GetPastWorkoutPageContract,
    GetPastWorkoutsInRangeContract,
    DeleteWorkoutContract,
    EditWorkoutItemsContract,
    GetWorkoutsByDateContract {
    private val coroutineContext = dispatcher + coroutineExceptionHandler

    override fun getWorkout(routineID: Long, workoutID: Long?): Flow<Workout> =
        getWorkoutEntity(routineID, workoutID)
            .flatMapLatest { workoutEntity ->
                workoutDao.getWorkoutExercises(workoutEntity.id, workoutEntity.routineID).map {
                    exercises ->
                    workoutEntity to exercises
                }
            }
            .map { (workoutEntity, exercises) -> workoutMapper.toDomain(workoutEntity, exercises) }
            .flowOn(coroutineContext)

    private fun getWorkoutEntity(routineID: Long, workoutID: Long?): Flow<WorkoutEntity> = flow {
        if (workoutID != null) {
            emitAll(workoutDao.getWorkout(workoutID).filterNotNull())
        } else {
            emitAll(workoutDao.getWorkout(insertEmptyWorkout(routineID)).filterNotNull())
        }
    }
        .distinctUntilChanged()

    private suspend fun insertEmptyWorkout(routineID: Long): Long = coroutineScope {
        val routineName = async {
            checkNotNull(workoutDao.getRoutineName(routineID).first()) {
                "Routine with ID $routineID not found"
            }
        }

        val bodyWeight = async { workoutDao.getLatestBodyWeight() }
        val routineItems = async { workoutDao.getRoutineItems(routineID).first() }

        workoutDao.getOrInsertActiveWorkoutWithItemsAndGoals(
            workout =
                WorkoutEntity(
                    name = routineName.await(),
                    routineID = routineID,
                    startDate = LocalDateTime.now(),
                    endDate = null,
                    notes = "",
                    bodyWeight = bodyWeight.await(),
                ),
            routineItems = routineItems.await(),
            routineID = routineID,
        )
    }

    override suspend fun upsertWorkoutGoal(workoutID: Long, exerciseID: Long, goal: Workout.Goal) {
        workoutDao.upsertWorkoutGoal(
            WorkoutGoalEntity(
                goal.id,
                workoutID,
                exerciseID,
                goal.minReps,
                goal.maxReps,
                goal.sets,
                goal.restTime.inWholeMilliseconds,
                goal.duration.inWholeMilliseconds,
                goal.distance,
                goal.distanceUnit,
                goal.calories,
            )
        )
    }

    override suspend fun upsertExerciseSet(
        workoutID: Long,
        exerciseId: Long,
        set: ExerciseSet,
        setIndex: Int,
    ) {
        workoutDao.upsertExerciseSet(
            workoutID,
            exerciseId,
            set.weight,
            set.weightUnit,
            set.reps,
            set.duration?.inWholeMilliseconds,
            set.distance,
            set.distanceUnit,
            set.kcal,
            set.notes,
            setIndex,
        )
    }

    override suspend fun updateExerciseNotes(
        workoutItemID: Long,
        exerciseID: Long,
        notes: String,
    ) {
        workoutDao.updateExerciseNotes(workoutItemID, exerciseID, notes)
    }

    override suspend fun updateWorkout(
        workoutID: Long,
        name: String?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?,
        notes: String?,
    ) {
        val updatedColumns = buildList {
            name?.also { add(WorkoutEntity.NAME to it) }
            startDate?.also { add(WorkoutEntity.START_DATE to it) }
            endDate?.also { add(WorkoutEntity.END_DATE to it) }
            notes?.also { add(WorkoutEntity.NOTES to it) }
        }
        if (updatedColumns.isEmpty()) error("No workout columns to update")
        val query =
            "UPDATE workout SET ${updatedColumns.joinToString { (column, _) -> "$column = ?" }} " +
                "WHERE workout_id = ?"
        workoutDao.query(
            RoomRawQuery(query) { statement ->
                updatedColumns.forEachIndexed { index, (_, value) ->
                    val text =
                        when (value) {
                            is LocalDateTime -> LocalDateTimeConverters.toString(value)
                            else -> value.toString()
                        }
                    statement.bindText(index + 1, text)
                }
                statement.bindLong(updatedColumns.size + 1, workoutID)
            }
        )
        invalidationTracker.refreshAsync()
    }

    override fun getWorkouts(
        type: GetWorkoutsContract.WorkoutType,
        limit: Int?,
    ): Flow<List<Workout>> =
        workoutDao
            .getWorkouts(
                WorkoutDao.getWorkoutsQuery(
                    hasEndDate = type == GetWorkoutsContract.WorkoutType.PAST,
                    limit = limit,
                )
            )
            .map(workoutMapper::toDomain)
            .flowOn(coroutineContext)

    override suspend fun getPastWorkoutPage(limit: Int, offset: Int): List<Workout> =
        withContext(coroutineContext) {
            workoutMapper.toDomain(
                workoutDao.getWorkoutsOnce(
                    WorkoutDao.getWorkoutsQuery(hasEndDate = true, limit = limit, offset = offset)
                )
            )
        }

    override fun getPastWorkouts(
        start: LocalDateTime,
        endExclusive: LocalDateTime,
    ): Flow<List<Workout>> =
        workoutDao
            .getWorkouts(WorkoutDao.getPastWorkoutsQuery(start, endExclusive))
            .map(workoutMapper::toDomain)
            .flowOn(coroutineContext)

    override suspend fun deleteWorkout(workoutID: Long) {
        withContext(coroutineContext) { workoutDao.deleteWorkout(workoutID) }
        invalidationTracker.refreshAsync()
    }

    override suspend fun addExercises(workoutID: Long, exerciseIDs: List<Long>) {
        if (exerciseIDs.isEmpty()) return
        withContext(coroutineContext) { workoutDao.addWorkoutExercises(workoutID, exerciseIDs) }
        invalidationTracker.refreshAsync()
    }

    override suspend fun reorderItems(workoutID: Long, workoutItemIDs: List<Long>) {
        withContext(coroutineContext) { workoutDao.reorderWorkoutItems(workoutID, workoutItemIDs) }
        invalidationTracker.refreshAsync()
    }

    override suspend fun removeItem(workoutID: Long, workoutItemID: Long) {
        withContext(coroutineContext) { workoutDao.removeWorkoutItem(workoutID, workoutItemID) }
        invalidationTracker.refreshAsync()
    }

    override suspend fun updateSetCount(workoutID: Long, workoutItemID: Long, setCount: Int) {
        withContext(coroutineContext) {
            workoutDao.updateWorkoutItemSetCount(workoutID, workoutItemID, setCount)
        }
    }

    override fun getWorkouts(date: LocalDate): Flow<List<Workout>> =
        workoutDao
            .getWorkouts(WorkoutDao.getWorkoutsQuery(date))
            .map(workoutMapper::toDomain)
            .flowOn(coroutineContext)
}
