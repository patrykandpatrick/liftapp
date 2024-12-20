package com.patrykandpatryk.liftapp.domain.exercise

import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository : GetExerciseNameContract {

    fun getAllExercises(): Flow<List<Exercise>>

    fun getExercise(id: Long): Flow<Exercise?>

    fun getRoutineExerciseItems(
        exerciseIds: List<Long>,
        ordered: Boolean,
    ): Flow<List<RoutineExerciseItem>>

    suspend fun insert(exercise: Exercise.Insert): Long

    suspend fun insert(exercises: List<Exercise.Insert>): List<Long>

    suspend fun update(exercise: Exercise.Update)

    suspend fun delete(exerciseId: Long)
}
