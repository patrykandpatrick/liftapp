package com.patrykandpatrick.liftapp.feature.dashboard.model

sealed interface WorkoutTarget {
    data class ActiveWorkout(val workoutID: Long) : WorkoutTarget

    data class PlannedRoutine(val routineID: Long) : WorkoutTarget
}
