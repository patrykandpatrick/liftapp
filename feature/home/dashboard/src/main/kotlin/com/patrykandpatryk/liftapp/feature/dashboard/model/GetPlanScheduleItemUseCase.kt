package com.patrykandpatryk.liftapp.feature.dashboard.model

import com.patrykandpatryk.liftapp.domain.plan.GetPlanItemContract
import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.domain.workout.GetWorkoutsByDateContract
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetPlanScheduleItemUseCase
@Inject
constructor(
    private val getPlanItemContract: GetPlanItemContract,
    private val getWorkoutsByDateContract: GetWorkoutsByDateContract,
) {

    operator fun invoke(date: LocalDate): Flow<PlanScheduleItem> =
        combine(
            getPlanItemContract.getPlanItem(date),
            getWorkoutsByDateContract.getWorkouts(date),
        ) { planItem, workouts ->
            when (planItem) {
                Plan.Item.Rest -> PlanScheduleItem.Rest
                is Plan.Item.Routine ->
                    PlanScheduleItem.Routine(
                        routine = planItem.routine,
                        workout = workouts.firstOrNull { it.routineID == planItem.id },
                    )

                null -> PlanScheduleItem.None
            }
        }
}
