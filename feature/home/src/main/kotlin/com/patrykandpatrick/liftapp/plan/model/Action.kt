package com.patrykandpatrick.liftapp.plan.model

import com.patrykandpatryk.liftapp.domain.plan.Plan

sealed interface Action {
    data object CreateNewPlan : Action

    data object ChooseExistingPlan : Action

    data class OnPlanItemClick(val item: Plan.Item) : Action

    data class StartWorkout(val item: Plan.Item.Routine) : Action
}
