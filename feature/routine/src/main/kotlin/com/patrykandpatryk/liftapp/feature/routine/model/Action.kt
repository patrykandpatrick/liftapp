package com.patrykandpatryk.liftapp.feature.routine.model

sealed interface Action {

    data object Edit : Action

    data object PopBackStack : Action

    data object StartWorkout : Action

    data class NavigateToExercise(val exerciseID: Long) : Action

    data class NavigateToExerciseGoal(val exerciseID: Long) : Action
}
