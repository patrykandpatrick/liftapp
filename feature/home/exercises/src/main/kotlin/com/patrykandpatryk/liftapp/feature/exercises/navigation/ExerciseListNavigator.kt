package com.patrykandpatryk.liftapp.feature.exercises.navigation

import androidx.compose.runtime.Stable

@Stable
interface ExerciseListNavigator {
    fun onExercisesPicked(exerciseIDs: List<Long>)

    fun newExercise()

    fun exerciseDetails(exerciseID: Long)

    fun back()
}
