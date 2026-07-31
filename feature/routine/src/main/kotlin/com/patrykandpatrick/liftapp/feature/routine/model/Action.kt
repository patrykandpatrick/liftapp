package com.patrykandpatrick.liftapp.feature.routine.model

sealed interface Action {

    data object Edit : Action

    data object Delete : Action

    data object Share : Action

    data object PopBackStack : Action

    data object StartWorkout : Action

    data class PickExercises(val disabledExerciseIDs: List<Long>) : Action

    data class RemoveItem(val itemID: Long) : Action

    data class ReorderItems(val itemIDs: List<Long>) : Action

    data class ReorderSupersetExercise(val itemID: Long, val fromIndex: Int, val toIndex: Int) :
        Action

    data class RemoveSupersetExercise(val itemID: Long, val exerciseID: Long) : Action

    data object NewSuperset : Action

    data class EditSuperset(val itemID: Long) : Action

    data class NavigateToExercise(val exerciseID: Long) : Action

    data class NavigateToExerciseGoal(val exerciseID: Long) : Action
}
