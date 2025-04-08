package com.patrykandpatrick.liftapp.planlist.model

sealed interface Action {
    data object PopBackStack : Action

    data class CheckPlan(val id: Long) : Action

    data object SaveSelection : Action
}
