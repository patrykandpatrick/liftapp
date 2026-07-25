package com.patrykandpatrick.liftapp.plan.ui

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.plan.Plan
import java.time.LocalDate

@Immutable
sealed class PlanState {

    data class ActivePlan(
        val plan: Plan,
        val cycleNumber: Int,
        val cycleCount: Int,
        val currentPlanItemIndex: Int,
        val cycleDates: List<Pair<LocalDate, LocalDate>>,
    ) : PlanState()

    data object NoActivePlan : PlanState()

    companion object {
        fun getAllCycleDates(
            startDate: LocalDate,
            cycleCount: Int,
            daysInCycle: Long,
        ): List<Pair<LocalDate, LocalDate>> = buildList {
            val startDate = startDate
            for (i in 0 until cycleCount) {
                val cycleStartDate = startDate.plusDays(i * daysInCycle)
                val cycleEndDate = cycleStartDate.plusDays(daysInCycle)
                add(cycleStartDate to cycleEndDate)
            }
        }
    }
}
