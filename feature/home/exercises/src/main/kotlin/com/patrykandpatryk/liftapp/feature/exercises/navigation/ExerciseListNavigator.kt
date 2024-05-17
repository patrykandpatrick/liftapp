package com.patrykandpatryk.liftapp.feature.exercises.navigation

import androidx.compose.runtime.Stable

@Stable
interface ExerciseListNavigator {
    fun onExercisesPicked(exerciseIds: List<Long>)
    fun newExercise()
    fun editExercise(exerciseId: Long)
    fun back()
}
