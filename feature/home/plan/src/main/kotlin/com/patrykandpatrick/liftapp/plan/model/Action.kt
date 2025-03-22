package com.patrykandpatrick.liftapp.plan.model

sealed interface Action {
    data object CreateNewPlan : Action

    data object ChooseExistingPlan : Action
}
