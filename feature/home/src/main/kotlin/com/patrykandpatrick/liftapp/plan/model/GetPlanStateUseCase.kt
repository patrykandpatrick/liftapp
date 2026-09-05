package com.patrykandpatrick.liftapp.plan.model

import com.patrykandpatrick.liftapp.domain.date.currentDateFlow
import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetAllPlansUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.plan.PlanSchedule
import com.patrykandpatrick.liftapp.domain.plan.invoke
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsByDateContract
import com.patrykandpatrick.liftapp.plan.ui.PlanState
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class GetPlanStateUseCase
@Inject
constructor(
    private val getActivePlanUseCase: GetActivePlanUseCase,
    private val getAllPlansUseCase: GetAllPlansUseCase,
    private val getWorkoutsByDateContract: GetWorkoutsByDateContract,
) {
    operator fun invoke(): Flow<PlanState> =
        currentDateFlow().flatMapLatest { currentDate ->
            combine(
                getActivePlanUseCase(),
                getAllPlansUseCase(),
                getWorkoutsByDateContract.getWorkouts(currentDate),
            ) { pair, plans, workouts ->
                if (pair == null) {
                    PlanState.NoActivePlan(hasPlans = plans.isNotEmpty())
                } else {
                    val (activePlan, plan) = pair
                    val schedule =
                        PlanSchedule(
                            startDate = activePlan.startDate,
                            itemCount = plan.items.size,
                            cycleCount = activePlan.cycleCount,
                        )
                    val position = schedule.positionOf(currentDate)
                    if (position != null) {
                        val currentPlanItemIndex = position.itemOrderIndex
                        PlanState.ActivePlan(
                            plan = plan,
                            cycleNumber = position.cycleIndex,
                            cycleCount = activePlan.cycleCount,
                            currentPlanItemIndex = currentPlanItemIndex,
                            cycleDates = schedule.cycleDates,
                            isDeloadCycle = activePlan.mesocycle.isDeload(position.cycleIndex),
                            currentWorkout =
                                (plan.items[currentPlanItemIndex] as? Plan.Item.Routine)?.let {
                                    currentRoutine ->
                                    workouts.firstOrNull {
                                        it.routineID == currentRoutine.routine.id
                                    }
                                },
                        )
                    } else {
                        PlanState.NoActivePlan(hasPlans = plans.isNotEmpty())
                    }
                }
            }
        }
}
