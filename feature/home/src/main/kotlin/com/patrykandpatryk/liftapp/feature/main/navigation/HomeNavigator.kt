package com.patrykandpatryk.liftapp.feature.main.navigation

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.feature.bodymeasurementlist.navigation.BodyMeasurementListNavigator
import com.patrykandpatryk.liftapp.feature.dashboard.navigation.DashboardNavigator
import com.patrykandpatryk.liftapp.feature.exercises.navigation.ExerciseListNavigator

@Immutable
interface HomeNavigator :
    DashboardNavigator, ExerciseListNavigator, BodyMeasurementListNavigator
