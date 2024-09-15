package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.extension.update
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.core.ui.ErrorEffectState
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Stable
class NewRoutineState(
    private val routineID: Long,
    private val savedStateHandle: SavedStateHandle,
    private val getRoutine: suspend () -> Pair<Routine, List<Long>>?,
    private val upsertRoutine: suspend (id: Long, name: String, exerciseIds: List<Long>) -> Unit,
    private val getExerciseItems: suspend (exerciseIds: List<Long>) -> Flow<List<RoutineExerciseItem>>,
    private val textFieldStateManager: TextFieldStateManager,
    private val validateExercises: Validator<List<RoutineExerciseItem>>,
    private val coroutineScope: CoroutineScope,
) {
    private val hasSavedState = savedStateHandle.contains(SCREEN_STATE_KEY)

    private val _exercises: MutableState<Validatable<List<RoutineExerciseItem>>> =
        mutableStateOf(validateExercises(emptyList()))

    private val _routineNotFound: MutableState<Boolean> = mutableStateOf(false)

    private val _routineSaved: MutableState<Boolean> = mutableStateOf(false)

    private val _showErrors: MutableState<Boolean> = mutableStateOf(false)

    val name: TextFieldState<String> = textFieldStateManager
        .stringTextField(validators = { nonEmpty() })

    val exercises: State<Validatable<List<RoutineExerciseItem>>> = _exercises

    val exerciseIds: List<Long> = derivedStateOf { exercises.value.value.map { it.id } }.value

    val isEdit: Boolean = routineID != ID_NOT_SET

    val showErrors: State<Boolean> = _showErrors

    val errorEffectState = ErrorEffectState()

    val routineSaved: State<Boolean> = _routineSaved

    val routineNotFound: State<Boolean> = _routineNotFound

    @AssistedInject
    constructor(
        @Assisted routineID: Long,
        @Assisted coroutineScope: CoroutineScope,
        savedStateHandle: SavedStateHandle,
        getRoutine: GetRoutineWithExerciseIdsUseCase,
        upsertRoutine: UpsertRoutineUseCase,
        getExerciseItems: GetRoutineExerciseItemsUseCase,
        textFieldStateManager: TextFieldStateManager,
        validateExercises: NonEmptyCollectionValidator<RoutineExerciseItem, List<RoutineExerciseItem>>,
    ) : this(
        routineID = routineID,
        savedStateHandle = savedStateHandle,
        getRoutine = { getRoutine(routineID).first() },
        upsertRoutine = { id, name, exerciseIds -> upsertRoutine(id, name, exerciseIds) },
        getExerciseItems = { exerciseIds -> getExerciseItems(exerciseIds) },
        textFieldStateManager = textFieldStateManager,
        validateExercises = validateExercises,
        coroutineScope = coroutineScope,
    )

    init {
        if (hasSavedState.not() && routineID != ID_NOT_SET) {
            loadUpdateState()
        }
        savedStateHandle.getStateFlow<List<Long>?>(Constants.Keys.PICKED_EXERCISE_IDS, null)
            .onEach { ids -> if (ids != null) addPickedExercises(ids) }
            .launchIn(coroutineScope)

        savedStateHandle
            .getStateFlow(PICKED_EXERCISES_KEY, emptyList<Long>())
            .flatMapLatest(getExerciseItems::invoke)
            .onEach { exercises ->
                _exercises.value = validateExercises(exercises)
            }
            .launchIn(coroutineScope)
    }

    private fun loadUpdateState() {
        coroutineScope.launch {
            val routineWithExerciseItems = getRoutine()

            if (routineWithExerciseItems == null) {
                _routineNotFound.value = true
            } else {
                val (routine, exerciseIds) = routineWithExerciseItems
                name.updateText(routine.name)
                addPickedExercises(exerciseIds)
            }
        }
    }

    fun addPickedExercises(exerciseIds: List<Long>) {
        savedStateHandle.update(PICKED_EXERCISES_KEY) { ids: List<Long>? ->
            ids
                ?.plus(exerciseIds)
                ?.distinct()
                ?: exerciseIds
        }
    }

    fun removePickedExercise(exerciseId: Long) {
        savedStateHandle.update(PICKED_EXERCISES_KEY) { ids: List<Long>? ->
            ids?.minus(exerciseId)
        }
    }

    fun save() {
        name.updateErrorMessages()
        if (name.hasError || exercises.value.isInvalid) {
            _showErrors.value = true
            errorEffectState.play()
            return
        }

        coroutineScope.launch {
            upsertRoutine(routineID, name.value, exercises.value.value.map { it.id })
            _routineSaved.value = true
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(routineID: Long, coroutineScope: CoroutineScope): NewRoutineState
    }

    companion object {
        private const val PICKED_EXERCISES_KEY = "pickedExercises"
    }
}
