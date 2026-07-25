package com.patrykandpatrick.liftapp.feature.plan.configurator.ui

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.text.IntTextFieldState
import com.patrykandpatryk.liftapp.core.text.LocalDateTextFieldState
import com.patrykandpatryk.liftapp.domain.plan.Plan
import java.time.LocalDate

@Stable
data class ScreenState(
    val plan: Plan,
    val startDate: LocalDateTextFieldState,
    val cycleCount: IntTextFieldState,
    val endDate: LocalDate,
    val lengthWeeks: Int,
    val lengthRemainingDays: Int,
)
