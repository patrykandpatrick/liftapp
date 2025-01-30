package com.patrykandpatryk.liftapp.feature.dashboard.model

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.workout.Workout

@Immutable
data class DashboardState(val activeWorkouts: List<Workout>, val pastWorkouts: List<Workout>)
