package com.patrykandpatrick.liftapp.plan.model

import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetAllPlansUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.plan.invoke
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsByDateContract
import com.patrykandpatrick.liftapp.feature.home.currentDateFlow
import com.patrykandpatrick.liftapp.plan.ui.PlanState
import jakarta.inject.Inject
import java.time.LocalDate
import java.time.temporal.ChronoUnit
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
                    val currentPlanItemIndex = getCurrentItem(activePlan, plan, currentDate)
                    val cycleDates =
                        PlanState.getAllCycleDates(
                            activePlan.startDate,
                            activePlan.cycleCount,
                            plan.items.size.toLong(),
                        )
                    if (currentDate in activePlan.startDate..cycleDates.last().second) {
                        PlanState.ActivePlan(
                            plan = plan,
                            cycleNumber = getCycleNumber(activePlan, plan, currentDate),
                            cycleCount = activePlan.cycleCount,
                            currentPlanItemIndex = currentPlanItemIndex,
                            cycleDates = cycleDates,
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

    private fun getDayCount(activePlan: ActivePlan, currentDate: LocalDate): Int =
        ChronoUnit.DAYS.between(activePlan.startDate, currentDate).toInt()

    private fun getCycleNumber(
        activePlan: ActivePlan,
        plan: Plan,
        currentDate: LocalDate,
    ): Int {
        val dayCount = plan.items.size
        val daysDifference = getDayCount(activePlan, currentDate)
        return daysDifference / dayCount
    }

    private fun getCurrentItem(
        activePlan: ActivePlan,
        plan: Plan,
        currentDate: LocalDate,
    ): Int = getDayCount(activePlan, currentDate) % plan.items.size
}
