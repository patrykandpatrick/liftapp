package com.patrykandpatrick.liftapp.plan.creator.model

import com.patrykandpatrick.liftapp.plan.creator.ui.ScreenState

sealed interface Action {
    data object PopBackStack : Action

    data class OnRoutineClick(val routineID: Long) : Action

    data object AddRestDay : Action

    data object AddRoutine : Action

    data class RemoveItem(val index: Int) : Action

    data class DeletePlan(val id: Long) : Action

    data class Save(val state: ScreenState) : Action
}
