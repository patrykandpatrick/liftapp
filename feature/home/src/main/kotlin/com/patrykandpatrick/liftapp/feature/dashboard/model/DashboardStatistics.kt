package com.patrykandpatrick.liftapp.feature.dashboard.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import kotlin.time.Duration

/** Totals across the workouts finished during the week the dashboard is showing. */
@Immutable
data class DashboardStatistics(
    val volume: Double,
    val volumeUnit: MassUnit,
    val reps: Int,
    val workouts: Int,
    val timeExercised: Duration,
) {
    companion object {
        fun empty(volumeUnit: MassUnit) =
            DashboardStatistics(
                volume = 0.0,
                volumeUnit = volumeUnit,
                reps = 0,
                workouts = 0,
                timeExercised = Duration.ZERO,
            )
    }
}
