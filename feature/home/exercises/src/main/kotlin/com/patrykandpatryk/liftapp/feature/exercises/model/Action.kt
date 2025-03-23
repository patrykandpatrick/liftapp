package com.patrykandpatryk.liftapp.feature.exercises.model

sealed interface Action {

    class SetQuery(val query: String) : Action

    class SetGroupBy(val groupBy: GroupBy) : Action

    class SetExerciseChecked(val exerciseId: Long, val checked: Boolean) : Action

    data class FinishPickingExercises(val resultKey: String) : Action

    data class GoToExerciseDetails(val exerciseID: Long) : Action

    data object PopBackStack : Action

    data object GoToNewExercise : Action
}
