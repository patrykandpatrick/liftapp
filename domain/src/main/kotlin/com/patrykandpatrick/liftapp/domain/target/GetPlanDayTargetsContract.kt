package com.patrykandpatrick.liftapp.domain.target

import kotlinx.coroutines.flow.Flow

fun interface GetPlanDayTargetsContract {
    fun getDayTargets(
        planID: Long,
        cycleIndex: Int,
        planItemOrderIndex: Int,
    ): Flow<PlanDayTargets>
}
