package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.workout.Workout
import java.time.LocalDate

@Immutable
sealed class PlanState {

    data class ActivePlan(
        val plan: Plan,
        val cycleNumber: Int,
        val cycleCount: Int,
        val currentPlanItemIndex: Int,
        val cycleDates: List<Pair<LocalDate, LocalDate>>,
        val currentWorkout: Workout?,
        /** The block this cycle belongs to says whether it is a week of reduced work. */
        val isDeloadCycle: Boolean = false,
    ) : PlanState()

    data class NoActivePlan(val hasPlans: Boolean) : PlanState()
}
