package com.patrykandpatryk.liftapp.feature.newroutine.navigation

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.core.navigation.NavigationResultListener

@Stable
interface NewRoutineNavigator : NavigationResultListener {
    fun back()

    fun pickExercises(disabledExerciseIDs: List<Long>)
}
