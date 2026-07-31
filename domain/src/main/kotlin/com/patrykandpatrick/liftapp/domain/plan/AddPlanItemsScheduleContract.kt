package com.patrykandpatrick.liftapp.domain.plan

import java.time.LocalDate

interface AddPlanItemsScheduleContract {
    suspend fun addPlanItemsSchedule(plan: Plan, startDate: LocalDate, cycleCount: Int)
}
