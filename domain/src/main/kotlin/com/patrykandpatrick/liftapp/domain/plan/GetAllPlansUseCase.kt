package com.patrykandpatrick.liftapp.domain.plan

import kotlinx.coroutines.flow.Flow

fun interface GetAllPlansUseCase {
    fun getPlans(): Flow<List<Plan>>
}

operator fun GetAllPlansUseCase.invoke(): Flow<List<Plan>> = getPlans()
