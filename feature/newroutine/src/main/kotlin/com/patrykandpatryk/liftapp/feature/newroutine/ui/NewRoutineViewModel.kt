package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.extension.update
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.core.validation.NonEmptyCollectionValidator
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.Constants.Algorithms.SCREEN_STATE_KEY
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineExerciseItemsUseCase
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.feature.newroutine.usecase.GetRoutineWithExerciseIdsUseCase
import com.patrykandpatryk.liftapp.feature.newroutine.usecase.UpsertRoutineUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

private const val PICKED_EXERCISES_KEY = "pickedExercises"

@HiltViewModel(assistedFactory = NewRoutineViewModel.Factory::class)
class NewRoutineViewModel(
    private val routineID: Long,
    private val savedState: SavedStateHandle,
    private val getRoutine: suspend () -> Pair<Routine, List<Long>>?,
    private val upsertRoutine: suspend (id: Long, name: String, exerciseIds: List<Long>) -> Unit,
    private val getExerciseItems: suspend (exerciseIds: List<Long>) -> Flow<List<RoutineExerciseItem>>,
    private val textFieldStateManager: TextFieldStateManager,
    private val validateExercises: Validator<List<RoutineExerciseItem>>,
    viewModelScope: CoroutineScope,
) : ViewModel(viewModelScope), NewRoutineState {
    private val hasSavedState = savedState.contains(SCREEN_STATE_KEY)

    override val name: TextFieldState<String> = textFieldStateManager.stringTextField(
        validators = { nonEmpty() }
    )

    override val exercises: MutableState<Validatable<List<RoutineExerciseItem>>> = mutableStateOf(validateExercises(emptyList()))

    override val exerciseIds: List<Long> = derivedStateOf { exercises.value.value.map { it.id } }.value

    override val isEdit: Boolean = routineID != ID_NOT_SET

    override val routineNotFound: MutableState<Boolean> = mutableStateOf(false)

    override val routineSaved: MutableState<Boolean> = mutableStateOf(false)

    override val showErrors: MutableState<Boolean> = mutableStateOf(false)

    @AssistedInject
    constructor(
        @Assisted routineID: Long,
        savedState: SavedStateHandle,
        getRoutine: GetRoutineWithExerciseIdsUseCase,
        upsertRoutine: UpsertRoutineUseCase,
        getExerciseItems: GetRoutineExerciseItemsUseCase,
        textFieldStateManager: TextFieldStateManager,
        validateExercises: NonEmptyCollectionValidator<RoutineExerciseItem, List<RoutineExerciseItem>>,
        viewModelScope: CoroutineScope,
    ) : this(
        routineID = routineID,
        savedState = savedState,
        getRoutine = { getRoutine(routineID).first() },
        upsertRoutine = { id, name, exerciseIds -> upsertRoutine(id, name, exerciseIds) },
        getExerciseItems = { exerciseIds -> getExerciseItems(exerciseIds) },
        textFieldStateManager = textFieldStateManager,
        validateExercises = validateExercises,
        viewModelScope = viewModelScope,
    )

    init {
        if (hasSavedState.not() && routineID != ID_NOT_SET) {
            loadUpdateState()
        }
        savedState.getStateFlow<List<Long>?>(Constants.Keys.PICKED_EXERCISE_IDS, null)
            .onEach { ids -> if (ids != null) addPickedExercises(ids) }
            .launchIn(viewModelScope)

        savedState
            .getStateFlow(PICKED_EXERCISES_KEY, emptyList<Long>())
            .flatMapLatest(getExerciseItems::invoke)
            .onEach { exercises ->
                this.exercises.value = validateExercises(exercises)
            }
            .launchIn(viewModelScope)
    }

    private fun loadUpdateState() {
        viewModelScope.launch {
            val routineWithExerciseItems = getRoutine()

            if (routineWithExerciseItems == null) {
                routineNotFound.value = true
            } else {
                val (routine, exerciseIds) = routineWithExerciseItems
                name.updateText(routine.name)
                addPickedExercises(exerciseIds)
            }
        }
    }

    override fun addPickedExercises(exerciseIds: List<Long>) {
        savedState.update(PICKED_EXERCISES_KEY) { ids: List<Long>? ->
            ids
                ?.plus(exerciseIds)
                ?.distinct()
                ?: exerciseIds
        }
    }

    override fun removePickedExercise(exerciseId: Long) {
        savedState.update(PICKED_EXERCISES_KEY) { ids: List<Long>? ->
            ids?.minus(exerciseId)
        }
    }

    override fun save() {
        name.updateErrorMessages()
        if (name.hasError || exercises.value.isInvalid) {
            showErrors.value = true
            return
        }

        viewModelScope.launch {
            upsertRoutine(routineID, name.value, exercises.value.value.map { it.id })
            routineSaved.value = true
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(routineID: Long): NewRoutineViewModel
    }
}
