package com.patrykandpatryk.liftapp.feature.routine.model

import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem

sealed interface Action {

    data object Edit : Action

    data object ShowDeleteDialog : Action

    data object HideDeleteDialog : Action

    data object Delete : Action

    data class DeleteExercise(val exerciseId: Long) : Action

    data class Reorder(val exercises: List<RoutineExerciseItem>, val from: Int, val to: Int) :
        Action

    data object PopBackStack : Action

    data object StartWorkout : Action

    data class NavigateToExercise(val exerciseID: Long) : Action

    data class NavigateToExerciseGoal(val exerciseID: Long) : Action
}
