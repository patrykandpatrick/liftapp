package com.patrykandpatrick.liftapp.feature.journal.model

sealed interface Action {

    data class GoToWorkout(val workoutID: Long) : Action

    data class DeleteWorkout(val workoutID: Long) : Action

    data object PopBackStack : Action
}
