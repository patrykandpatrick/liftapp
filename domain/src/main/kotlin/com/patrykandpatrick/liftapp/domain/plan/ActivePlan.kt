package com.patrykandpatrick.liftapp.domain.plan

import com.patrykandpatrick.liftapp.domain.target.Mesocycle
import java.time.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * [deloadEveryCycles] is the block length the plan was generated with, kept so the app can say
 * which week is a deload without inferring it from the targets. Null means the plan runs straight
 * through, and being the default it stays out of the stored JSON entirely.
 */
@Serializable
data class ActivePlan(
    val planID: Long,
    @Contextual val startDate: LocalDate,
    val cycleCount: Int,
    val deloadEveryCycles: Int? = null,
) {
    val mesocycle: Mesocycle
        get() = Mesocycle(deloadEveryCycles)
}
