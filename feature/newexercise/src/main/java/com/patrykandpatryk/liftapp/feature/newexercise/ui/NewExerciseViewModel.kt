package com.patrykandpatryk.liftapp.feature.newexercise.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.data.NewExerciseRouteData
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.core.viewmodel.SavedStateHandleViewModel
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.exercise.GetExerciseUseCase
import com.patrykandpatryk.liftapp.domain.exercise.InsertExercisesUseCase
import com.patrykandpatryk.liftapp.domain.exercise.UpdateExercisesUseCase
import com.patrykandpatryk.liftapp.domain.extension.toggle
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import com.patrykandpatryk.liftapp.domain.validation.toValid
import com.patrykandpatryk.liftapp.feature.newexercise.model.Action
import com.patrykandpatryk.liftapp.feature.newexercise.model.NewExerciseState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

private const val STATE_KEY = "screen_state"

@HiltViewModel
class NewExerciseViewModel
@Inject
constructor(
    private val newExerciseRouteData: NewExerciseRouteData,
    private val logger: UiLogger,
    private val getExercise: GetExerciseUseCase,
    override val savedStateHandle: SavedStateHandle,
    private val insertExercise: InsertExercisesUseCase,
    private val updateExercise: UpdateExercisesUseCase,
    private val exerciseToStateMapper: Mapper<Exercise, NewExerciseState>,
    private val stateToInsertExercise: Mapper<NewExerciseState.Valid, Exercise.Insert>,
    private val stateToUpdateExercise: Mapper<NewExerciseState.Valid, Exercise.Update>,
    private val navigationCommander: NavigationCommander,
) : ViewModel(), SavedStateHandleViewModel, LogPublisher by logger {
    init {
        if (newExerciseRouteData.exerciseID != ID_NOT_SET)
            loadExerciseState(newExerciseRouteData.exerciseID)
    }

    internal var state: NewExerciseState by
        saveable(STATE_KEY) { mutableStateOf(NewExerciseState.Invalid()) }

    fun onAction(action: Action) {
        when (action) {
            is Action.UpdateName -> updateName(action.name)
            is Action.UpdateExerciseType -> updateExerciseType(action.exerciseType)
            is Action.MainMuscleListAction -> updateMainMuscles(action.listAction)
            is Action.SecondaryMuscleListAction -> updateSecondaryMuscles(action.listAction)
            is Action.TertiaryMuscleListAction -> updateTertiaryMuscles(action.listAction)
            Action.Save -> save()
            Action.PopBackStack -> popBackStack()
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun updateName(name: String) {
        val validatableName: Validatable<Name> =
            if (name.isBlank()) {
                Name.Raw(name).toInvalid()
            } else {
                Name.Raw(name).toValid()
            }
        state = state.copyState(name = validatableName, displayName = name)
    }

    private fun updateExerciseType(type: ExerciseType) {
        state = state.copyState(type = type)
    }

    private fun updateMainMuscles(action: Action.ListAction) {
        val updatedMuscles =
            when (action) {
                Action.ListAction.Clear -> emptyList()
                is Action.ListAction.ToggleMuscle ->
                    state.primaryMuscles.value.toggle(action.muscle)
            }
        val mainMusclesValidatable =
            if (updatedMuscles.isEmpty()) {
                updatedMuscles.toInvalid()
            } else {
                updatedMuscles.toValid()
            }
        state = state.copyState(mainMuscles = mainMusclesValidatable)
    }

    private fun updateSecondaryMuscles(action: Action.ListAction) {
        val updatedMuscles =
            when (action) {
                Action.ListAction.Clear -> emptyList()
                is Action.ListAction.ToggleMuscle -> state.secondaryMuscles.toggle(action.muscle)
            }
        state = state.copyState(secondaryMuscles = updatedMuscles)
    }

    private fun updateTertiaryMuscles(action: Action.ListAction) {
        val updatedMuscles =
            when (action) {
                Action.ListAction.Clear -> emptyList()
                is Action.ListAction.ToggleMuscle -> state.tertiaryMuscles.toggle(action.muscle)
            }
        state = state.copyState(tertiaryMuscles = updatedMuscles)
    }

    private fun save() {
        when (val state = state) {
            is NewExerciseState.Valid -> {
                insertOrUpdateExercise(state)
                popBackStack()
            }

            is NewExerciseState.Invalid -> {
                this.state = state.copy(showErrors = true)
            }
        }
    }

    private fun insertOrUpdateExercise(state: NewExerciseState.Valid) {
        viewModelScope.launch {
            if (newExerciseRouteData.exerciseID != ID_NOT_SET) {
                updateExercise(stateToUpdateExercise(state))
            } else {
                insertExercise(stateToInsertExercise(state))
            }
        }
    }

    private fun loadExerciseState(exerciseId: Long) {
        viewModelScope.launch {
            getExercise(exerciseId)
                .firstOrNull()
                ?.let { exerciseToStateMapper(it) }
                ?.also { existingExerciseState -> state = existingExerciseState }
        }
    }
}
