package com.patrykandpatryk.liftapp.feature.routines

import androidx.compose.runtime.Stable

@Stable
interface RoutineListNavigator {
    fun newRoutine()
    fun routine(routineID: Long)
}
