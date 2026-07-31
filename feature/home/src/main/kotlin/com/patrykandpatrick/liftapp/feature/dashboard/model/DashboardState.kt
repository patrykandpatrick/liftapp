package com.patrykandpatrick.liftapp.feature.dashboard.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.domain.workout.Workout
import java.time.LocalDate

@Immutable
data class DashboardState(
    val statistics: DashboardStatistics,
    val dayItems: List<DayItem>,
    val selectedDate: LocalDate,
    val activeWorkouts: List<Workout>,
    val pastWorkouts: List<Workout>,
    /** Whether the journal holds a finished workout that [pastWorkouts] does not show. */
    val hasMorePastWorkouts: Boolean,
    val planScheduleItem: PlanScheduleItem,
    /** The workout resumed or started by the dashboard's primary action. */
    val workoutTarget: WorkoutTarget?,
) {

    data class DayItem(val date: LocalDate, val isSelected: Boolean, val isToday: Boolean)
}
