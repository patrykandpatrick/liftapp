package com.patrykandpatryk.liftapp.domain.exercise

import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {

    fun getAllExercises(): Flow<List<Exercise>>

    suspend fun insert(exercise: Exercise.Insert): Long

    suspend fun update(exercise: Exercise.Update)

    suspend fun delete(exerciseId: Long)
}
