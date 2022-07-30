package com.patrykandpatryk.liftapp.functionality.database.exercise

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val entityToDomain: Mapper<ExerciseEntity, Exercise>,
    private val insertToEntity: Mapper<Exercise.Insert, ExerciseEntity>,
    private val updateToEntity: Mapper<Exercise.Update, ExerciseEntity.Update>,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : ExerciseRepository {

    override fun getAllExercises(): Flow<List<Exercise>> =
        exerciseDao
            .getAllExercises()
            .map(entityToDomain::invoke)
            .flowOn(dispatcher)

    override fun getExercise(id: Long): Flow<Exercise?> =
        exerciseDao
            .getExercise(id)
            .map { entity -> entity?.let(entityToDomain::invoke) }
            .flowOn(dispatcher)

    override suspend fun insert(exercise: Exercise.Insert): Long = withContext(dispatcher) {
        exerciseDao.insert(insertToEntity(exercise))
    }

    override suspend fun insert(exercises: List<Exercise.Insert>): List<Long> = withContext(dispatcher) {
        exerciseDao.insert(insertToEntity(exercises))
    }

    override suspend fun update(exercise: Exercise.Update) = withContext(dispatcher) {
        exerciseDao.update(updateToEntity(exercise))
    }

    override suspend fun delete(exerciseId: Long) = withContext(dispatcher) {
        exerciseDao.delete(exerciseId)
    }
}
