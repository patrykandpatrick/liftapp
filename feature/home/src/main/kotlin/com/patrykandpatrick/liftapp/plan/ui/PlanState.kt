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
    ) : PlanState()

    data class NoActivePlan(val hasPlans: Boolean) : PlanState()

    companion object {
        fun getAllCycleDates(
            startDate: LocalDate,
            cycleCount: Int,
            daysInCycle: Long,
        ): List<Pair<LocalDate, LocalDate>> = buildList {
            val startDate = startDate
            for (i in 0 until cycleCount) {
                val cycleStartDate = startDate.plusDays(i * daysInCycle)
                val cycleEndDate = cycleStartDate.plusDays(daysInCycle - 1)
                add(cycleStartDate to cycleEndDate)
            }
        }
    }
}
