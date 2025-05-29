package com.patrykandpatryk.liftapp.domain.plan

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface GetPlanItemContract {
    fun getPlanItem(date: LocalDate): Flow<Plan.Item?>
}
