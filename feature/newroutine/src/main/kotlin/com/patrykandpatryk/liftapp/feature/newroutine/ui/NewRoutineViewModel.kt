package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.core.ui.ErrorEffectState
import com.patrykandpatryk.liftapp.core.validation.NonEmptyCollectionValidator
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseIds
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.feature.newroutine.model.Action
import com.patrykandpatryk.liftapp.feature.newroutine.model.GetExerciseItemsUseCase
import com.patrykandpatryk.liftapp.feature.newroutine.model.GetRoutineWithExerciseIDsUseCase
import com.patrykandpatryk.liftapp.feature.newroutine.model.NewRoutineSavedState
import com.patrykandpatryk.liftapp.feature.newroutine.model.UpsertRoutineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class NewRoutineViewModel
@Inject
constructor(
    viewModelScope: CoroutineScope,
    getRoutineWithExerciseIDsUseCase: GetRoutineWithExerciseIDsUseCase,
    getExerciseItemsUseCase: GetExerciseItemsUseCase,
    textFieldStateManager: TextFieldStateManager,
    private val newRoutineSavedState: NewRoutineSavedState,
    private val listValidator:
        NonEmptyCollectionValidator<RoutineExerciseItem, List<RoutineExerciseItem>>,
    private val upsertRoutine: UpsertRoutineUseCase,
) : ViewModel(viewModelScope) {
    private val name = textFieldStateManager.stringTextField(validators = { nonEmpty() })

    private val errorEffectState = ErrorEffectState()

    private val routineSaved = MutableStateFlow(false)

    private val showErrors = MutableStateFlow(false)

    val state =
        combine(
                getRoutineWithExerciseIDsUseCase(),
                getExerciseItemsUseCase(),
                showErrors,
                routineSaved,
            ) { routine, exercises, showErrors, routineSaved ->
                if (routine != null) loadRoutineData(routine)
                NewRoutineState(
                    id = routine?.id ?: ID_NOT_SET,
                    name = name,
                    exercises = listValidator.validate(exercises),
                    isEdit = routine != null,
                    errorEffectState = errorEffectState,
                    showErrors = showErrors,
                    routineSaved = routineSaved,
                )
            }
            .toLoadableStateFlow(viewModelScope)

    private fun loadRoutineData(routineWithExerciseIds: RoutineWithExerciseIds) {
        if (!newRoutineSavedState.isInitialized) {
            newRoutineSavedState.isInitialized = true
            name.updateText(routineWithExerciseIds.name)
            newRoutineSavedState.addExerciseIDs(routineWithExerciseIds.exerciseIDs)
        }
    }

    internal fun onAction(action: Action) {
        when (action) {
            is Action.AddExercises -> addExerciseIDs(action.ids)
            is Action.RemoveExercise -> removeExerciseID(action.id)
            is Action.SaveRoutine -> save(action.state)
        }
    }

    private fun addExerciseIDs(exerciseIDs: List<Long>) {
        newRoutineSavedState.addExerciseIDs(exerciseIDs)
    }

    private fun removeExerciseID(exerciseID: Long) {
        newRoutineSavedState.removeExerciseIDs(exerciseID)
    }

    private fun save(state: NewRoutineState) {
        with(state) {
            name.updateErrorMessages()
            if (name.hasError || exercises.isInvalid) {
                this@NewRoutineViewModel.showErrors.value = true
                errorEffectState.play()
                return
            }

            viewModelScope.launch {
                upsertRoutine(name.value, exerciseIds)
                this@NewRoutineViewModel.routineSaved.value = true
            }
        }
    }
}
