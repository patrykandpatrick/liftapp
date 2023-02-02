package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val exerciseMapper: ExerciseMapper,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao
            .getAllExercises()
            .map(exerciseMapper::toDomain)
            .flowOn(dispatcher)

    override fun getExercise(id: Long): Flow<Exercise?> =
        exerciseDao
            .getExercise(id)
            .map { entity -> entity?.let(exerciseMapper::toDomain) }
            .flowOn(dispatcher)

    override fun getRoutineExerciseItems(exerciseIds: List<Long>): Flow<List<RoutineExerciseItem>> =
        exerciseDao
            .getExercises(exerciseIds)
            .map(exerciseMapper::toRoutineExerciseItem)

    override suspend fun insert(exercise: Exercise.Insert): Long = withContext(dispatcher) {
        exerciseDao.insert(exercise.toEntity())
    }

    override suspend fun insert(exercises: List<Exercise.Insert>): List<Long> = withContext(dispatcher) {
        exerciseDao.insert(exercises.toEntity())
    }

    override suspend fun update(exercise: Exercise.Update) = withContext(dispatcher) {
        exerciseDao.update(exercise.toEntity())
    }

    override suspend fun delete(exerciseId: Long) = withContext(dispatcher) {
        exerciseDao.delete(exerciseId)
    }
}
