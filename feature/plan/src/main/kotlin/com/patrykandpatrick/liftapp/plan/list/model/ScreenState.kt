package com.patrykandpatrick.liftapp.plan.list.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.plan.list.model.ScreenState.PlanItem
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.plan.Plan

@Immutable
data class ScreenState(
    val plans: List<PlanItem>,
    val isPickingTrainingPlan: Boolean,
    val isAnyPlanSelected: Boolean,
) : List<PlanItem> by plans {

    data class PlanItem(
        val id: Long,
        val name: String?,
        val routineNames: List<String>,
        val cycleLength: Int,
        val isChecked: Boolean,
    )

    companion object {
        fun create(
            plans: List<Plan>,
            checkedID: Long,
            isPickingTrainingPlan: Boolean,
        ): ScreenState =
            ScreenState(
                plans =
                    plans.map { plan ->
                        PlanItem(
                            id = plan.id,
                            name = plan.name,
                            routineNames = plan.routines.map { it.name },
                            cycleLength = plan.items.size,
                            isChecked = checkedID == plan.id,
                        )
                    },
                isPickingTrainingPlan = isPickingTrainingPlan,
                isAnyPlanSelected = isPickingTrainingPlan && checkedID != ID_NOT_SET,
            )
    }
}
