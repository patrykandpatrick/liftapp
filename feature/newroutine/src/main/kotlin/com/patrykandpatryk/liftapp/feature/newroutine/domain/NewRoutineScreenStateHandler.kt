package com.patrykandpatryk.liftapp.feature.newroutine.domain

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.extension.update
import com.patrykandpatryk.liftapp.core.validation.Name
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.routine.Routine
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.domain.validation.toValid
import com.patrykandpatryk.liftapp.feature.newroutine.di.RoutineId
import com.patrykandpatryk.liftapp.feature.newroutine.ui.ExerciseItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SCREEN_STATE_KEY = "screenState"
private const val PICKED_EXERCISES_KEY = "pickedExercises"

class NewRoutineScreenStateHandler @Inject constructor(
    @RoutineId private val routineId: Long?,
    private val savedState: SavedStateHandle,
    private val routineRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository,
    private val exerciseToItemMapper: Mapper<Exercise, ExerciseItem>,
    exceptionHandler: CoroutineExceptionHandler,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
    @Name private val validateName: Validator<String>,
) : ScreenStateHandler<ScreenState, Intent, Event> {

    private val scope = CoroutineScope(SupervisorJob() + dispatcher + exceptionHandler)

    override val state: StateFlow<ScreenState> = savedState
        .getStateFlow(SCREEN_STATE_KEY, getInitialState())

    override val events: MutableSharedFlow<Event> = MutableSharedFlow()

    init {

        if (state.value == ScreenState.Loading && routineId != null) {
            loadUpdateState(routineId)
        }

        savedState
            .getStateFlow(PICKED_EXERCISES_KEY, emptyList<Long>())
            .flatMapLatest(exerciseRepository::getExercises)
            .map(exerciseToItemMapper::invoke)
            .onEach { exercises ->
                updateState { state -> state.mutate(exercises = exercises) }
            }.launchIn(scope)
    }

    private fun getInitialState(): ScreenState =
        if (routineId == null) {
            ScreenState.Insert(
                name = validateName(""),
            )
        } else ScreenState.Loading

    private fun loadUpdateState(routineId: Long) {
        scope.launch {
            val routine = routineRepository.getRoutine(routineId).first()

            if (routine == null) {
                events.emit(Event.RoutineNotFound)
            } else {
                updateState {
                    ScreenState.Update(
                        id = routineId,
                        name = routine.name.toValid(),
                    )
                }
            }
        }
    }

    private fun updateState(update: (ScreenState) -> ScreenState) {
        savedState[SCREEN_STATE_KEY] = update(state.value)
    }

    override fun close() {
        scope.cancel()
    }

    override fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.UpdateName -> updateName(intent.name)
            is Intent.Save -> save()
            is Intent.AddPickedExercises -> addPickedExercises(intent.exerciseIds)
            is Intent.RemovePickedExercise -> removePickedExercise(intent.exerciseId)
        }
    }

    private fun updateName(name: String) {
        updateState {
            it.mutate(
                name = validateName(name),
                showErrors = false,
            )
        }
    }

    private fun addPickedExercises(exerciseIds: List<Long>) {
        savedState.update(PICKED_EXERCISES_KEY) { ids: List<Long>? ->
            ids?.plus(exerciseIds) ?: exerciseIds
        }
    }

    private fun removePickedExercise(exerciseId: Long) {
        savedState.update(PICKED_EXERCISES_KEY) { ids: List<Long>? ->
            ids?.minus(exerciseId)
        }
    }

    private fun save() {
        val state = state.value
        val name = state.name

        if (name.isInvalid) {
            updateState { it.mutate(showErrors = true) }
        } else {
            scope.launch {
                routineRepository.upsert(
                    Routine(
                        id = routineId ?: ID_NOT_SET,
                        name = name.value,
                    ),
                )
                events.emit(Event.EntrySaved)
            }
        }
    }
}
