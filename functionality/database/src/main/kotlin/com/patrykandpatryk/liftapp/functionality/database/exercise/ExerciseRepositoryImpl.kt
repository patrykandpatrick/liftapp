package com.patrykandpatryk.liftapp.functionality.database.exercise

import androidx.sqlite.db.SimpleSQLiteQuery
import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.functionality.database.appendSQLOrderByCase
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

    override fun getRoutineExerciseItems(exerciseIds: List<Long>, ordered: Boolean): Flow<List<RoutineExerciseItem>> {
        val query = buildString {
            append("SELECT * FROM exercise WHERE exercise_id IN (${exerciseIds.joinToString(separator = ",")})")
            if (ordered) {
                append(" ".appendSQLOrderByCase("exercise_id", exerciseIds))
            }
        }
        return exerciseDao
            .getExercises(SimpleSQLiteQuery(query))
            .map(exerciseMapper::exerciseEntityToRoutineExerciseItem)
    }

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
