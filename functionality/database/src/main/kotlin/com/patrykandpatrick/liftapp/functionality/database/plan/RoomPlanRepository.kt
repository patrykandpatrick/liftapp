package com.patrykandpatrick.liftapp.functionality.database.plan

import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.domain.di.IODispatcher
import com.patrykandpatrick.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatrick.liftapp.domain.exception.PlanNotFoundException
import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.AddPlanItemsScheduleContract
import com.patrykandpatrick.liftapp.domain.plan.DeletePlanContract
import com.patrykandpatrick.liftapp.domain.plan.GetAllPlansUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanItemContract
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.plan.UpsertPlanContract
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomPlanRepository
@Inject
constructor(
    private val dao: PlanDao,
    private val mapper: PlanMapper,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    @param:PreferenceQualifier.ActivePlan private val activePlan: Preference<ActivePlan?>,
) :
    GetAllPlansUseCase,
    GetPlanUseCase,
    UpsertPlanContract,
    AddPlanItemsScheduleContract,
    GetPlanItemContract,
    DeletePlanContract {
    override fun getPlans(): Flow<List<Plan>> =
        dao.getAllPlans().map { plans -> mapper.toDomain(plans) }.flowOn(dispatcher)

    override fun getPlan(id: Long): Flow<Plan> =
        dao.getPlan(id)
            .map { plans ->
                mapper.toDomain(plans).firstOrNull() ?: throw PlanNotFoundException(id)
            }
            .flowOn(dispatcher)

    // `plan_item` and `plan_item_schedule` both declare `onDelete = CASCADE` against `plan_id`, so
    // removing the plan row takes its items and its schedule with it.
    override suspend fun deletePlan(id: Long) {
        withContext(dispatcher + NonCancellable) { dao.deletePlan(id) }
    }

    override suspend fun upsertPlan(plan: Plan) {
        withContext(dispatcher + NonCancellable) {
            dao.upsertPlanWithItems(toPlanEntity(plan), toPlanItems(plan))
        }
    }

    private fun toPlanEntity(plan: Plan): PlanEntity =
        PlanEntity(
            id = plan.id,
            name = plan.name.orEmpty(),
            description = plan.description,
            itemCount = plan.items.size,
        )

    private fun toPlanItems(plan: Plan): List<PlanItemEntity> =
        plan.items.mapIndexedNotNull { index, item ->
            when (item) {
                is Plan.Item.Routine ->
                    PlanItemEntity(
                        planId = plan.id,
                        orderIndex = index,
                        routineId = item.routine.id,
                    )
                is Plan.Item.Rest -> null
            }
        }

    override suspend fun addPlanItemsSchedule(plan: Plan, startDate: LocalDate, cycleCount: Int) {
        var currentDate = startDate
        val schedule = buildList {
            repeat(cycleCount) { cycleIndex ->
                plan.items.forEachIndexed { itemOrderIndex, planItem ->
                    add(
                        PlanItemSchedule(
                            planID = plan.id,
                            routineID = (planItem as? Plan.Item.Routine)?.routine?.id,
                            date = currentDate,
                            cycleIndex = cycleIndex,
                            itemOrderIndex = itemOrderIndex,
                        )
                    )
                    currentDate = currentDate.plusDays(1)
                }
            }
        }
        dao.replacePlanItemSchedule(schedule)
    }

    /**
     * Scoped to the active plan rather than trusting the table to hold only its rows. Activation
     * clears the rest, but a restored archive arrives without passing through it.
     */
    override fun getPlanItem(date: LocalDate): Flow<Plan.Item?> =
        activePlan
            .get()
            .flatMapLatest { active ->
                if (active == null) flowOf(null)
                else dao.getScheduledRoutine(active.planID, date).map(mapper::toDomain)
            }
            .flowOn(dispatcher)
}
