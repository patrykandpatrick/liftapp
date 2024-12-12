package com.patrykandpatryk.liftapp.feature.routine.navigator

import androidx.compose.runtime.Stable

@Stable
interface RoutineNavigator {
    fun back()

    fun editRoutine()

    fun exercise(exerciseID: Long)

    fun exerciseGoal(exerciseID: Long)

    fun newWorkout()
}
