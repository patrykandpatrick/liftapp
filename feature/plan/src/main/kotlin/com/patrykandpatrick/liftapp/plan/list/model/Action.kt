package com.patrykandpatrick.liftapp.plan.list.model

sealed interface Action {
    data object PopBackStack : Action

    data object AddNewPlan : Action

    data class OnPlanClick(val id: Long) : Action

    data object SaveSelection : Action
}
