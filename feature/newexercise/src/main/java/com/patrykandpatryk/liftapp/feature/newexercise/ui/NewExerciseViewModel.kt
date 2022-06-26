package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.exercise.InsertExercisesUseCase
import com.patrykandpatryk.liftapp.domain.extension.toggle
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.validation.toInValid
import com.patrykandpatryk.liftapp.domain.validation.toValid
import com.patrykandpatryk.liftapp.feature.newexercise.state.NewExerciseState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val STATE_KEY = "screen_state"

@HiltViewModel
class NewExerciseViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val insertExercise: InsertExercisesUseCase,
    private val stateToInsertExercise: Mapper<NewExerciseState.Valid, Exercise.Insert>,
) : ViewModel() {

    internal var state: NewExerciseState by mutableStateOf(
        value = savedStateHandle[STATE_KEY] ?: NewExerciseState.Invalid(),
    )

    fun updateName(name: String) {
        val validableName = if (name.isBlank()) {
            name.toInValid()
        } else {
            name.toValid()
        }
        state = state.copyState(name = validableName)
    }

    fun updateExerciseType(type: ExerciseType) {
        state = state.copyState(type = type)
    }

    fun updateMainMuscles(muscle: Muscle) {
        val updatedMuscle = state.mainMuscles.value.toggle(muscle)
        val mainMusclesValidable = if (updatedMuscle.isEmpty()) {
            updatedMuscle.toInValid()
        } else {
            updatedMuscle.toValid()
        }
        state = state.copyState(mainMuscles = mainMusclesValidable)
    }

    fun updateSecondaryMuscles(muscle: Muscle) {
        state = state.copyState(secondaryMuscles = state.secondaryMuscles.toggle(muscle))
    }

    fun updateTertiaryMuscles(muscle: Muscle) {
        state = state.copyState(tertiaryMuscles = state.tertiaryMuscles.toggle(muscle))
    }

    fun save(): Boolean {
        return when (val state = state) {
            is NewExerciseState.Valid -> {
                viewModelScope.launch {
                    insertExercise(stateToInsertExercise(state))
                }
                true
            }
            is NewExerciseState.Invalid -> {
                this.state = state.copy(showErrors = true)
                false
            }
        }
    }

    override fun onCleared() {
        savedStateHandle[STATE_KEY] = state
    }
}
