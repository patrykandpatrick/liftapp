package com.patrykandpatrick.liftapp.plan.creator.model

import com.patrykandpatrick.liftapp.plan.creator.ui.ScreenState

sealed interface Action {
    data object PopBackStack : Action

    data class OnPlanElementClick(val planElement: ScreenState.Item.PlanElement) : Action

    data object AddRestDay : Action

    data object AddRoutine : Action

    data class RemoveItem(val index: Int) : Action

    data class Save(val state: ScreenState) : Action

    data object ClearError : Action
}
