package com.patrykandpatryk.liftapp.functionality.database.workout

import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import com.patrykandpatryk.liftapp.domain.workout.WorkoutRepository
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
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

class RoomWorkoutRepository
@Inject
constructor(
    private val workoutDao: WorkoutDao,
    private val workoutMapper: WorkoutMapper,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    coroutineExceptionHandler: CoroutineExceptionHandler,
) : WorkoutRepository {
    private val coroutineContext = dispatcher + coroutineExceptionHandler

    override fun getWorkout(routineID: Long, workoutID: Long?): Flow<Workout> =
        getWorkoutEntity(routineID, workoutID)
            .flatMapLatest { workoutEntity ->
                workoutDao.getWorkoutExercises(workoutEntity.id).map { exercises ->
                    workoutEntity to exercises
                }
            }
            .map { (workoutEntity, exercises) -> workoutMapper.toDomain(workoutEntity, exercises) }
            .flowOn(coroutineContext)

    private fun getWorkoutEntity(routineID: Long, workoutID: Long?): Flow<WorkoutEntity> =
        flow {
                if (workoutID != null) {
                    emitAll(workoutDao.getWorkout(workoutID))
                } else {
                    emit(null)
                }
            }
            .distinctUntilChanged()
            .transformLatest { workout ->
                if (workout == null) {
                    emitAll(workoutDao.getWorkout(insertEmptyWorkout(routineID)))
                } else {
                    emit(workout)
                }
            }
            .filterNotNull()

    private suspend fun insertEmptyWorkout(routineID: Long): Long = coroutineScope {
        val routineName = async {
            checkNotNull(workoutDao.getRoutineName(routineID).first()) {
                "Routine with ID $routineID not found"
            }
        }

        val bodyWeight = async { workoutDao.getLatestBodyWeight() }

        val workoutID =
            workoutDao.insertWorkout(
                WorkoutEntity(
                    name = routineName.await(),
                    routineID = routineID,
                    date = LocalDateTime.now(),
                    durationMillis = 0L,
                    notes = "",
                    bodyWeight = bodyWeight.await(),
                )
            )

        launch { insertWorkoutWithExercises(workoutID, routineID) }
        launch { workoutDao.copyRoutineGoalsToWorkoutGoals(routineID, workoutID) }
        workoutID
    }

    private suspend fun insertWorkoutWithExercises(workoutID: Long, routineID: Long) {
        val exerciseIDs = workoutDao.getExerciseIDs(routineID).first()
        val workoutWithExercises =
            exerciseIDs.mapIndexed { index, id -> WorkoutWithExerciseEntity(workoutID, id, index) }
        workoutDao.insertWorkoutWithExercises(workoutWithExercises)
    }

    override suspend fun upsertWorkoutGoal(
        workoutID: Long,
        exerciseID: Long,
        minReps: Int,
        maxReps: Int,
        sets: Int,
        restTimeMillis: Long,
        durationMillis: Long,
        distance: Double,
        distanceUnit: LongDistanceUnit,
        calories: Double,
    ) {
        workoutDao.upsertWorkoutGoal(
            workoutID,
            exerciseID,
            minReps,
            maxReps,
            sets,
            restTimeMillis,
            durationMillis,
            distance,
            distanceUnit,
            calories,
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
            setIndex,
        )
    }
}
