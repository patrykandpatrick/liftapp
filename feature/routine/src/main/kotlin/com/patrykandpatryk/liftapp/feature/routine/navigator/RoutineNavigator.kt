package com.patrykandpatryk.liftapp.feature.routine.navigator

import androidx.compose.runtime.Stable

@Stable
interface RoutineNavigator {
    fun back()
    fun editRoutine(routineId: Long)
    fun exercise(exerciseID: Long)
}
