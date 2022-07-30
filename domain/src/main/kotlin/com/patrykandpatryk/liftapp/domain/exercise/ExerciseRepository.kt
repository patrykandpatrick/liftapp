package com.patrykandpatryk.liftapp.domain.exercise

import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {

    fun getAllExercises(): Flow<List<Exercise>>

    fun getExercise(id: Long): Flow<Exercise?>

    suspend fun insert(exercise: Exercise.Insert): Long

    suspend fun insert(exercises: List<Exercise.Insert>): List<Long>

    suspend fun update(exercise: Exercise.Update)

    suspend fun delete(exerciseId: Long)
}
