package com.patrykandpatryk.liftapp.feature.dashboard.model

sealed class Action {
    data class NewWorkout(val routineID: Long) : Action()

    data class OpenWorkout(val workoutID: Long) : Action()
}
