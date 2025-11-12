package com.patrykandpatryk.liftapp.functionality.database.plan

import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.exception.PlanNotFoundException
import com.patrykandpatryk.liftapp.domain.plan.AddPlanItemsScheduleContract
import com.patrykandpatryk.liftapp.domain.plan.GetAllPlansContract
import com.patrykandpatryk.liftapp.domain.plan.GetPlanContract
import com.patrykandpatryk.liftapp.domain.plan.GetPlanItemContract
import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.domain.plan.UpsertPlanContract
import java.time.LocalDate
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
) :
    GetAllPlansContract,
    GetPlanContract,
    UpsertPlanContract,
    AddPlanItemsScheduleContract,
    GetPlanItemContract {
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
            name = plan.name.orEmpty(),
            description = plan.description,
            itemCount = plan.items.size,
        )
    }

    private fun toPlanItems(plan: Plan): List<PlanItemEntity> {
        return plan.items.mapIndexedNotNull { index, item ->
            when (item) {
                is Plan.Item.Routine ->
                    PlanItemEntity(
                        planId = plan.id,
                        orderIndex = index,
                        routineId = item.routine.id,
                    )
                else -> null
            }
        }
    }

    override suspend fun addPlanItemsSchedule(plan: Plan, startDate: LocalDate, cycleCount: Int) {
        var currentDate = startDate
        val schedule = buildList {
            repeat(cycleCount) {
                plan.items.forEach { planItem ->
                    if (planItem is Plan.Item.Routine) {
                        add(
                            PlanItemSchedule(
                                planID = plan.id,
                                routineID = planItem.routine.id,
                                date = currentDate,
                            )
                        )
                    } else {
                        add(
                            PlanItemSchedule(planID = plan.id, routineID = null, date = currentDate)
                        )
                    }
                    currentDate = currentDate.plusDays(1)
                }
            }
        }
        dao.insertPlanItemSchedule(schedule)
    }

    override fun getPlanItem(date: LocalDate): Flow<Plan.Item?> =
        dao.getScheduledRoutine(date).map(mapper::toDomain)
}
