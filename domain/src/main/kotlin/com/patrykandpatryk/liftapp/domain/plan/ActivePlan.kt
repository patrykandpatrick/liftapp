package com.patrykandpatryk.liftapp.domain.plan

import java.time.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ActivePlan(
    val planID: Long,
    @Contextual val startDate: LocalDate,
    val cycleCount: Int,
    val dayOffset: Long,
)
