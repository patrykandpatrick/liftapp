package com.patrykandpatrick.liftapp.feature.dashboard.model

import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanItemContract
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.workout.GetWorkoutsByDateContract
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetPlanScheduleItemUseCase
@Inject
constructor(
    private val getPlanItemContract: GetPlanItemContract,
    private val getWorkoutsByDateContract: GetWorkoutsByDateContract,
    private val getActivePlanUseCase: GetActivePlanUseCase,
) {

    operator fun invoke(date: LocalDate): Flow<PlanScheduleItem> =
        combine(
            getPlanItemContract.getPlanItem(date),
            getWorkoutsByDateContract.getWorkouts(date),
            getActivePlanUseCase(),
        ) { planItem, workouts, activePlan ->
            when (planItem) {
                Plan.Item.Rest -> PlanScheduleItem.Rest
                is Plan.Item.Routine ->
                    PlanScheduleItem.Routine(
                        routine = planItem.routine,
                        workout = workouts.firstOrNull { it.routineID == planItem.id },
                    )

                null -> PlanScheduleItem.None(hasActivePlan = activePlan != null)
            }
        }
}
