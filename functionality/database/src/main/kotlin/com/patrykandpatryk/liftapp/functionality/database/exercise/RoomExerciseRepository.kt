package com.patrykandpatryk.liftapp.functionality.database.exercise

import androidx.sqlite.db.SimpleSQLiteQuery
import com.patrykandpatryk.liftapp.domain.date.DateInterval
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseNameAndType
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.exercise.GetRoutineExercisesUseCase
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatryk.liftapp.domain.exerciseset.GetExerciseSetsUseCase
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.functionality.database.appendSQLOrderByCase
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomExerciseRepository
@Inject
constructor(
    private val exerciseDao: ExerciseDao,
    private val exerciseMapper: ExerciseMapper,
    private val exerciseSetMapper: ExerciseSetMapper,
    @param:DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : ExerciseRepository, GetRoutineExercisesUseCase, GetExerciseSetsUseCase {

    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao.getAllExercises().map(exerciseMapper::toDomain).flowOn(dispatcher)

    override fun getExercise(id: Long): Flow<Exercise?> =
        exerciseDao
            .getExercise(id)
            .map { entity -> entity?.let(exerciseMapper::toDomain) }
            .flowOn(dispatcher)

    override fun getExerciseNameAndType(id: Long): Flow<ExerciseNameAndType?> =
        exerciseDao
            .getExerciseNameAndType(id)
            .map { dto -> dto?.let(exerciseMapper::toDomain) }
            .flowOn(dispatcher)

    override fun getRoutineExerciseItems(
        exerciseIds: List<Long>,
        ordered: Boolean,
    ): Flow<List<RoutineExerciseItem>> {
        val query = buildString {
            append(
                "SELECT * FROM exercise WHERE exercise_id IN (${exerciseIds.joinToString(separator = ",")})"
            )
            if (ordered) {
                append(" ".appendSQLOrderByCase("exercise_id", exerciseIds))
            }
        }
        return exerciseDao
            .getExercises(SimpleSQLiteQuery(query))
            .map(exerciseMapper::exerciseEntityToRoutineExerciseItem)
            .flowOn(dispatcher)
    }

    override suspend fun insert(exercise: Exercise.Insert): Long =
        withContext(dispatcher) { exerciseDao.insert(exercise.toEntity()) }

    override suspend fun insert(exercises: List<Exercise.Insert>): List<Long> =
        withContext(dispatcher) { exerciseDao.insert(exercises.toEntity()) }

    override suspend fun update(exercise: Exercise.Update) =
        withContext(dispatcher) { exerciseDao.update(exercise.toEntity()) }

    override suspend fun delete(exerciseId: Long) =
        withContext(dispatcher) { exerciseDao.delete(exerciseId) }

    override fun getExerciseSets(
        exerciseID: Long,
        dateInterval: DateInterval,
    ): Flow<List<ExerciseSetGroup>> =
        exerciseDao
            .getExercise(exerciseID)
            .filterNotNull()
            .flatMapLatest { exercise ->
                exerciseDao
                    .getExerciseSets(
                        exercise.id,
                        dateInterval.periodStartTime,
                        dateInterval.periodEndTime,
                    )
                    .map { allSets ->
                        allSets
                            .groupBy { it.exerciseSet.workoutID }
                            .map { (workoutID, dtos) ->
                                val workout = dtos.first()
                                val workoutStartDate = workout.workoutStartDate
                                ExerciseSetGroup(
                                    workoutID = workoutID,
                                    workoutName = workout.workoutName,
                                    exerciseID = exercise.id,
                                    sets =
                                        exerciseSetMapper.mapCompletedExerciseSets(
                                            exercise.exerciseType,
                                            dtos.map { it.exerciseSet },
                                        ),
                                    workoutStartDate = workoutStartDate,
                                )
                            }
                    }
            }
            .flowOn(dispatcher)
}
