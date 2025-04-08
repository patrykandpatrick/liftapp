package com.patrykandpatryk.liftapp.functionality.database.plan

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.exception.PlanNotFoundException
import com.patrykandpatryk.liftapp.domain.plan.GetAllPlansContract
import com.patrykandpatryk.liftapp.domain.plan.GetPlanContract
import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.domain.plan.UpsertPlanContract
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomPlanRepository
@Inject
constructor(
    private val dao: PlanDao,
    private val mapper: PlanMapper,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
) : GetAllPlansContract, GetPlanContract, UpsertPlanContract {
    override fun getPlans(): Flow<List<Plan>> =
        dao.getAllPlans().map { plans -> mapper.toDomain(plans) }.flowOn(dispatcher)

    override fun getPlan(id: Long): Flow<Plan> =
        dao.getPlan(id)
            .map { plans ->
                mapper.toDomain(plans).firstOrNull() ?: throw PlanNotFoundException(id)
            }
            .flowOn(dispatcher)

    override suspend fun upsertPlan(plan: Plan) {
        withContext(dispatcher) {
            val planID = dao.upsertPlan(toPlanEntity(plan))
            dao.upsertPlanItems(toPlanItems(plan.copy(id = planID)))
        }
    }

    private fun toPlanEntity(plan: Plan): PlanEntity {
        return PlanEntity(
            id = plan.id,
            name = plan.name,
            description = plan.description,
            itemCount = plan.items.size,
        )
    }

    private fun toPlanItems(plan: Plan): List<PlanItemEntity> {
        return plan.items.mapIndexedNotNull { index, item ->
            when (item) {
                is Plan.Item.RoutineItem ->
                    PlanItemEntity(
                        planId = plan.id,
                        orderIndex = index,
                        routineId = item.routine.id,
                    )
                else -> null
            }
        }
    }
}
