package com.patrykandpatryk.liftapp.feature.dashboard.model

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.time.LocalDate

@Immutable
data class DashboardState(
    val dayItems: List<DayItem>,
    val selectedDate: LocalDate,
    val activeWorkouts: List<Workout>,
    val pastWorkouts: List<Workout>,
    val planScheduleItem: PlanScheduleItem?,
) {

    data class DayItem(val date: LocalDate, val isSelected: Boolean, val isToday: Boolean)
}
