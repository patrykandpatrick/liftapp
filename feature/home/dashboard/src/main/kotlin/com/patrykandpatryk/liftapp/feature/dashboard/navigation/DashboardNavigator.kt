package com.patrykandpatryk.liftapp.feature.dashboard.navigation

import androidx.compose.runtime.Stable

@Stable
interface DashboardNavigator {
    fun newWorkout(routineID: Long)

    fun openWorkout(workoutID: Long)
}
