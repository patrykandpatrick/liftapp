package com.patrykandpatrick.liftapp.newplan.model

import com.patrykandpatrick.liftapp.newplan.ui.NewPlanState
import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.domain.plan.UpsertPlanContract
import javax.inject.Inject

class UpsertPlanUseCase @Inject constructor(private val upsertPlanContract: UpsertPlanContract) {
    suspend operator fun invoke(state: NewPlanState) {
        upsertPlanContract.upsertPlan(toPlan(state))
    }

    private fun toPlan(state: NewPlanState): Plan {
        return Plan(
            id = state.id,
            name = state.name.value,
            description = state.description.value,
            items =
                state.items.mapNotNull { item ->
                    when (item) {
                        is NewPlanState.Item.RoutineItem -> Plan.Item.RoutineItem(item.routine)
                        is NewPlanState.Item.RestItem -> Plan.Item.RestItem
                        is NewPlanState.Item.PlaceholderItem -> null
                    }
                },
        )
    }
}
