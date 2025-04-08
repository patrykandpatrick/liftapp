package com.patrykandpatryk.liftapp.domain.plan

import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

class GetAllPlansUseCase @Inject constructor(private val getAllPlansContract: GetAllPlansContract) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Plan>> = getAllPlansContract.getPlans()
}
