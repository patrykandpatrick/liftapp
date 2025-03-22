package com.patrykandpatrick.liftapp.plan.model

import com.patrykandpatrick.liftapp.plan.ui.PlanState
import com.patrykandpatryk.liftapp.domain.plan.GetActivePlanUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetPlanStateUseCase
@Inject
constructor(private val getActivePlanUseCase: GetActivePlanUseCase) {
    operator fun invoke(): Flow<PlanState> =
        getActivePlanUseCase().map { plan ->
            if (plan == null) {
                PlanState.NoActivePlan
            } else {
                PlanState.ActivePlan(plan)
            }
        }
}
