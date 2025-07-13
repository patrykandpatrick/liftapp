package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.NewRoutineRouteData
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.core.ui.ErrorEffectState
import com.patrykandpatryk.liftapp.core.validation.NonEmptyCollectionValidator
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.exception.RoutineNotFoundException
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExerciseIDsUseCase
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseIds
import com.patrykandpatryk.liftapp.domain.routine.UpsertRoutineWithExerciseIdsUseCase
import com.patrykandpatryk.liftapp.domain.routine.invoke
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.feature.newroutine.model.Action
import com.patrykandpatryk.liftapp.feature.newroutine.model.GetExerciseItemsUseCase
import com.patrykandpatryk.liftapp.feature.newroutine.model.NewRoutineSavedState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

@HiltViewModel
class NewRoutineViewModel
@Inject
constructor(
    viewModelScope: CoroutineScope,
    private val getRoutineWithExerciseIDsUseCase: GetRoutineWithExerciseIDsUseCase,
    getExerciseItemsUseCase: GetExerciseItemsUseCase,
    textFieldStateManager: TextFieldStateManager,
    private val routeData: NewRoutineRouteData,
    private val newRoutineSavedState: NewRoutineSavedState,
    private val listValidator:
        NonEmptyCollectionValidator<RoutineExerciseItem, List<RoutineExerciseItem>>,
    private val upsertRoutine: UpsertRoutineWithExerciseIdsUseCase,
    private val navigationCommander: NavigationCommander,
    private val deleteRoutineUseCase: DeleteRoutineUseCase,
) : ViewModel(viewModelScope) {
    private val name = textFieldStateManager.stringTextField(validators = { nonEmpty() })

    private val errorEffectState = ErrorEffectState()

    private val showErrors = MutableStateFlow(false)

    val state =
        combine(getRoutineWithExerciseIDs(), getExerciseItemsUseCase(), showErrors) {
                routine,
                exercises,
                showErrors ->
                if (routine != null) loadRoutineData(routine)
                NewRoutineState(
                    id = routine?.id ?: ID_NOT_SET,
                    routineName = routine?.name.orEmpty(),
                    name = name,
                    exercises = listValidator.validate(exercises),
                    isEdit = routine != null,
                    errorEffectState = errorEffectState,
                    showErrors = showErrors,
                )
            }
            .toLoadableStateFlow(viewModelScope)

    init {
        observePickedExercises()
    }

    private fun getRoutineWithExerciseIDs(): Flow<RoutineWithExerciseIds?> =
        if (routeData.routineID == ID_NOT_SET) {
            flowOf(null)
        } else {
            getRoutineWithExerciseIDsUseCase(routeData.routineID).transform { routine ->
                if (routine == null) {
                    throw RoutineNotFoundException(routeData.routineID)
                } else {
                    emit(routine)
                }
            }
        }

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
            is Action.PopBackStack -> popBackStack()
            is Action.PickExercises -> pickExercises(action.disabledExerciseIDs)
            is Action.ReorderExercise ->
                reorderExercise(action.fromIndex, action.toIndex, action.continuation)
            is Action.DeleteRoutine -> deleteRoutine(action.id)
        }
    }

    private fun addExerciseIDs(exerciseIDs: List<Long>) {
        newRoutineSavedState.addExerciseIDs(exerciseIDs)
    }

    private fun removeExerciseID(exerciseID: Long) {
        newRoutineSavedState.removeExerciseIDs(exerciseID)
    }

    private fun deleteRoutine(id: Long) {
        viewModelScope.launch {
            deleteRoutineUseCase(id)
            routeData.deleteResultKey?.also { navigationCommander.publishResult(it, true) }
            navigationCommander.popBackStack()
        }
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
                upsertRoutine(Routine(name.value, routeData.routineID), exerciseIds)
                navigationCommander.popBackStack()
            }
        }
    }

    private fun pickExercises(disabledExerciseIDs: List<Long>) {
        viewModelScope.launch {
            navigationCommander.navigateTo(
                Routes.Exercise.pick(KEY_EXERCISE_IDS, disabledExerciseIDs)
            )
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun observePickedExercises() {
        navigationCommander
            .getResults<List<Long>>(KEY_EXERCISE_IDS)
            .onEach { newRoutineSavedState.addExerciseIDs(it) }
            .launchIn(viewModelScope)
    }

    private fun reorderExercise(fromIndex: Int, toIndex: Int, continuation: Continuation<Unit>) {
        newRoutineSavedState.reorderExerciseIDs(fromIndex, toIndex)
        viewModelScope.launch {
            val expectedExerciseIds = newRoutineSavedState.exerciseIDs.value
            state
                .filter { state ->
                    state.valueOrNull()?.exercises?.value?.map { it.id } == expectedExerciseIds
                }
                .first()
            continuation.resume(Unit)
        }
    }

    companion object {
        private const val KEY_EXERCISE_IDS = "exercise_ids"
    }
}
