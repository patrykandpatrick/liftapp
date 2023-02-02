package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.di.ValidatorType
import com.patrykandpatryk.liftapp.core.extension.update
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.domain.Constants.Algorithms.SCREEN_STATE_KEY
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineExerciseItemsUseCase
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.feature.newroutine.di.RoutineId
import com.patrykandpatryk.liftapp.feature.newroutine.model.Event
import com.patrykandpatryk.liftapp.feature.newroutine.model.Intent
import com.patrykandpatryk.liftapp.feature.newroutine.model.ScreenState
import com.patrykandpatryk.liftapp.feature.newroutine.usecase.GetRoutineWithExerciseIdsUseCase
import com.patrykandpatryk.liftapp.feature.newroutine.usecase.UpsertRoutineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val PICKED_EXERCISES_KEY = "pickedExercises"

@HiltViewModel
class NewRoutineViewModel @Inject constructor(
    @RoutineId private val routineId: Long,
    private val savedState: SavedStateHandle,
    private val getRoutine: GetRoutineWithExerciseIdsUseCase,
    private val upsertRoutine: UpsertRoutineUseCase,
    private val getExerciseItems: GetRoutineExerciseItemsUseCase,
    exceptionHandler: CoroutineExceptionHandler,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    @ValidatorType.Name private val validateName: Validator<String>,
    private val validateExercises: Validator<List<RoutineExerciseItem>>,
    private val logger: UiLogger,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Event>, LogPublisher by logger {

    private val coroutineContext = dispatcher + exceptionHandler

    private val hasSavedState = savedState.contains(SCREEN_STATE_KEY)

    override val state: StateFlow<ScreenState> = savedState
        .getStateFlow(SCREEN_STATE_KEY, getInitialState())

    override val events: MutableSharedFlow<Event> = MutableSharedFlow()

    init {

        Timber.d("Init, hasSavedState=$hasSavedState, id=$routineId")
        if (hasSavedState.not() && routineId != ID_NOT_SET) {
            loadUpdateState(routineId)
        }

        savedState
            .getStateFlow(PICKED_EXERCISES_KEY, emptyList<Long>())
            .flatMapLatest(getExerciseItems::invoke)
            .onEach { exercises ->
                updateState { state -> state.copy(exercises = validateExercises(exercises)) }
            }
            .flowOn(coroutineContext)
            .launchIn(viewModelScope)
    }

    private fun getInitialState(): ScreenState =
        ScreenState(
            id = routineId,
            name = validateName(""),
            exercises = validateExercises(emptyList()),
        )

    private fun loadUpdateState(routineId: Long) {
        viewModelScope.launch(coroutineContext) {
            val routineWithExerciseItems = getRoutine(routineId).first()

            if (routineWithExerciseItems == null) {
                events.emit(Event.RoutineNotFound)
            } else {
                val (routine, exerciseIds) = routineWithExerciseItems

                updateState { state ->
                    state.copy(
                        name = validateName(routine.name),
                        isEdit = true,
                    )
                }

                addExerciseIds(exerciseIds)
            }
        }
    }

    private inline fun updateState(update: (ScreenState) -> ScreenState) {
        savedState[SCREEN_STATE_KEY] = update(state.value)
    }

    override fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.UpdateName -> updateName(intent.name)
            is Intent.Save -> validateAndSave()
            is Intent.AddPickedExercises -> addExerciseIds(intent.exerciseIds)
            is Intent.RemovePickedExercise -> removeExerciseId(intent.exerciseId)
        }
    }

    private fun updateName(name: String) {
        updateState {
            it.copy(
                name = validateName(name),
                showErrors = false,
            )
        }
    }

    private fun addExerciseIds(exerciseIds: List<Long>) {
        savedState.update(PICKED_EXERCISES_KEY) { ids: List<Long>? ->
            ids
                ?.plus(exerciseIds)
                ?.distinct()
                ?: exerciseIds
        }
    }

    private fun removeExerciseId(exerciseId: Long) {
        savedState.update(PICKED_EXERCISES_KEY) { ids: List<Long>? ->
            ids?.minus(exerciseId)
        }
    }

    private fun validateAndSave() {
        val state = state.value
        if (validateState(state)) save(state)
    }

    private fun validateState(state: ScreenState): Boolean {
        return if (state.name.isInvalid || state.exercises.isInvalid) {
            updateState { it.copy(showErrors = true) }
            false
        } else true
    }

    private fun save(state: ScreenState) {
        viewModelScope.launch(coroutineContext) {
            upsertRoutine(
                id = state.id,
                name = state.name.value,
                exerciseIds = state.exerciseIds,
            )
            events.emit(Event.EntrySaved)
        }
    }
}
