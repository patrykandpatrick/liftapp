package com.patrykandpatryk.liftapp.functionality.database.plan

import com.patrykandpatryk.liftapp.domain.exception.PlanNotFoundException
import com.patrykandpatryk.liftapp.domain.plan.GetPlanContract
import com.patrykandpatryk.liftapp.domain.plan.Plan
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomPlanRepository
@Inject
constructor(private val dao: PlanDao, private val mapper: PlanMapper) : GetPlanContract {
    override fun getPlan(id: Long): Flow<Plan> =
        dao.getPlan(id).map { plans ->
            mapper.toDomain(plans).firstOrNull() ?: throw PlanNotFoundException(id)
        }
}
