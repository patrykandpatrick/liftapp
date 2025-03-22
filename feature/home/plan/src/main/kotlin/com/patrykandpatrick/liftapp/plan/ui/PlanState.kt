package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.plan.Plan

@Immutable
sealed class PlanState {

    data class ActivePlan(val plan: Plan) : PlanState()

    data object NoActivePlan : PlanState()
}
