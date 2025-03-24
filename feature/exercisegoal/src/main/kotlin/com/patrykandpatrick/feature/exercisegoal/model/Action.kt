package com.patrykandpatrick.feature.exercisegoal.model

sealed interface Action {
    data class SetGoalInfoVisible(val visible: Boolean) : Action

    data class SaveGoal(val state: State) : Action

    data object PopBackStack : Action
}
