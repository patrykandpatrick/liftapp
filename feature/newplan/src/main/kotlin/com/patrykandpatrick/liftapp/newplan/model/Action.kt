package com.patrykandpatrick.liftapp.newplan.model

import com.patrykandpatrick.liftapp.newplan.ui.NewPlanState

sealed interface Action {
    data object PopBackStack : Action

    data class OnPlanElementClick(val planElement: NewPlanState.Item.PlanElement) : Action

    data object AddRestDay : Action

    data object AddRoutine : Action

    data class RemoveItem(val index: Int) : Action

    data class Save(val state: NewPlanState) : Action

    data object ClearError : Action
}
