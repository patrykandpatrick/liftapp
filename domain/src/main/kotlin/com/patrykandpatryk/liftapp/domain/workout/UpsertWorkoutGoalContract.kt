package com.patrykandpatryk.liftapp.domain.workout

fun interface UpsertWorkoutGoalContract {
    suspend fun upsertWorkoutGoal(workoutID: Long, exerciseID: Long, goal: Workout.Goal)
}
