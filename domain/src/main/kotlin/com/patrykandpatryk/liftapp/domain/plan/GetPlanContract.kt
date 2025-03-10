package com.patrykandpatryk.liftapp.domain.plan

import kotlinx.coroutines.flow.Flow

fun interface GetPlanContract {
    fun getPlan(id: Long): Flow<Plan>
}
