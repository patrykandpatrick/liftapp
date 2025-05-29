package com.patrykandpatrick.liftapp.plan.creator.model

import com.patrykandpatrick.liftapp.plan.creator.ui.ScreenState
import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.domain.plan.UpsertPlanContract
import javax.inject.Inject

class UpsertPlanUseCase @Inject constructor(private val upsertPlanContract: UpsertPlanContract) {
    suspend operator fun invoke(state: ScreenState) {
        upsertPlanContract.upsertPlan(toPlan(state))
    }

    private fun toPlan(state: ScreenState): Plan {
        return Plan(
            id = state.id,
            name = state.name.value,
            description = state.description.value,
            items =
                state.items.mapNotNull { item ->
                    when (item) {
                        is ScreenState.Item.RoutineItem -> Plan.Item.Routine(item.routine)
                        is ScreenState.Item.RestItem -> Plan.Item.Rest
                        is ScreenState.Item.PlaceholderItem -> null
                    }
                },
        )
    }
}
