package com.patrykandpatryk.liftapp.domain.plan

import kotlinx.coroutines.flow.Flow

fun interface GetAllPlansContract {
    fun getPlans(): Flow<List<Plan>>
}
