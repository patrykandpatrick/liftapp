package com.patrykandpatrick.liftapp.feature.musclevolume.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.domain.date.DAYS_IN_WEEK
import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.muscle.MuscleVolume

@Immutable
data class ScreenState(
    val muscleVolume: MuscleVolume,
    /** What the active plan asks for in a week, or null when no plan covers today. */
    val plannedVolume: MuscleVolume?,
    val dateInterval: DateInterval,
    val dateIntervalOptions: List<DateInterval>,
) {
    /**
     * Whether the rows are a rate rather than a count. Read off the same elapsed days the use case
     * divides by, so the caption cannot claim an average over a window that was left undivided.
     */
    val isWeeklyAverage: Boolean = dateInterval.elapsedDays > DAYS_IN_WEEK
}
