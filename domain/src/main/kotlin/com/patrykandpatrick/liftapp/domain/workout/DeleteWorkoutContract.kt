package com.patrykandpatrick.liftapp.domain.workout

fun interface DeleteWorkoutContract {
    suspend fun deleteWorkout(workoutID: Long)
}
