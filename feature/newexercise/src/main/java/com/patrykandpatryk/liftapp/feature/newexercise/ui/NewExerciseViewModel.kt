package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.extension.toggle
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.feature.newexercise.state.ScreenState
import javax.inject.Inject

private const val STATE_KEY = "screen_state"

class NewExerciseViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    internal var state by mutableStateOf(savedStateHandle[STATE_KEY] ?: ScreenState())

    fun updateName(name: String) {
        state = state.copy(name = name)
    }

    fun updateExerciseType(type: ExerciseType) {
        state = state.copy(type = type)
    }

    fun updateMainMuscles(muscle: Muscle) {
        state = state.copy(mainMuscles = state.mainMuscles.toggle(muscle))
    }

    fun updateSecondaryMuscles(muscle: Muscle) {
        state = state.copy(secondaryMuscles = state.secondaryMuscles.toggle(muscle))
    }

    fun updateTertiaryMuscles(muscle: Muscle) {
        state = state.copy(tertiaryMuscles = state.tertiaryMuscles.toggle(muscle))
    }

    override fun onCleared() {
        savedStateHandle[STATE_KEY] = state
    }
}
