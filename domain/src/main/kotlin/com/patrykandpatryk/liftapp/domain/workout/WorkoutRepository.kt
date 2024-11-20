package com.patrykandpatryk.liftapp.domain.workout

import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun getWorkout(routineID: Long, workoutID: Long?): Flow<Workout>

    suspend fun upsertWorkoutGoal(
        workoutID: Long,
        exerciseID: Long,
        minReps: Int,
        maxReps: Int,
        sets: Int,
        breakDurationMillis: Long
    )
}
