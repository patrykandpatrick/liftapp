package com.patrykandpatryk.liftapp.feature.routine.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.core.model.MuscleModel
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.muscle.MuscleImageProvider
import com.patrykandpatryk.liftapp.domain.routine.DeleteRoutineUseCase
import com.patrykandpatryk.liftapp.domain.routine.GetRoutineWithExercisesUseCase
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.feature.routine.di.RoutineId
import com.patrykandpatryk.liftapp.feature.routine.model.Event
import com.patrykandpatryk.liftapp.feature.routine.model.Intent
import com.patrykandpatryk.liftapp.feature.routine.model.ScreenState
import com.patrykandpatryk.liftapp.feature.routine.usecase.ReorderExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

private const val SCREEN_STATE_KEY = "screenState"

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val logger: UiLogger,
    @RoutineId private val routineId: Long,
    isDarkModeReceiver: IsDarkModeReceiver,
    getRoutine: GetRoutineWithExercisesUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val deleteRoutine: DeleteRoutineUseCase,
    private val muscleImageProvider: MuscleImageProvider,
    private val reorderExercisesUseCase: ReorderExercisesUseCase,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Event>, LogPublisher by logger {

    private val eventChannel = Channel<Event>()

    override val state: StateFlow<ScreenState> = savedStateHandle
        .getStateFlow(SCREEN_STATE_KEY, ScreenState.Loading)

    override val events: Flow<Event> = eventChannel.receiveAsFlow()

    init {
        combine(
            getRoutine(routineId),
            isDarkModeReceiver(),
        ) { routine, isDarkMode ->
            if (routine == null) {
                Timber.e("Routine with id $routineId not found, or deleted.")
                eventChannel.send(Event.RoutineNotFound)
            } else {
                updateState(routine = routine, isDarkMode = isDarkMode)
            }
        }.launchIn(viewModelScope)
    }

    private fun updateState(
        routine: RoutineWithExercises,
        isDarkMode: Boolean,
    ) {

        updateScreenState {
            mutate(
                name = routine.name,
                exercises = routine.exercises,
                muscles = MuscleModel.create(
                    primaryMuscles = routine.primaryMuscles,
                    secondaryMuscles = routine.secondaryMuscles,
                    tertiaryMuscles = routine.tertiaryMuscles,
                ),
            )
        }

        loadBitmap(
            primaryMuscles = routine.primaryMuscles,
            secondaryMuscles = routine.secondaryMuscles,
            tertiaryMuscles = routine.tertiaryMuscles,
            isDarkMode = isDarkMode,
        )
    }

    private fun loadBitmap(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
        isDarkMode: Boolean,
    ) {
        viewModelScope.launch {
            updateScreenState {
                mutate(
                    imagePath = muscleImageProvider.getMuscleImagePath(
                        primaryMuscles,
                        secondaryMuscles,
                        tertiaryMuscles,
                        isDark = isDarkMode,
                    ),
                )
            }
        }
    }

    override fun handleIntent(intent: Intent) {
        when (intent) {
            Intent.Edit -> handleEdit()
            Intent.ShowDeleteDialog -> handleDeleteDialogVisibility(visible = true)
            Intent.HideDeleteDialog -> handleDeleteDialogVisibility(visible = false)
            Intent.Delete -> deleteRoutine()
            is Intent.Reorder -> reorder(intent)
        }
    }

    private fun reorder(reorder: Intent.Reorder) {
        viewModelScope.launch {
            val exercises = ArrayList(state.value.exercises)
            val exercise = exercises.removeAt(reorder.from)
            exercises.add(reorder.to, exercise)
            reorderExercisesUseCase(
                routineId = routineId,
                exercises = exercises,
            )
        }
    }

    private fun handleEdit() {
        viewModelScope.launch {
            eventChannel.send(Event.EditRoutine(routineId))
        }
    }

    private fun deleteRoutine() {
        handleDeleteDialogVisibility(visible = false)
        viewModelScope.launch {
            deleteRoutine(routineId)
        }
    }

    private fun handleDeleteDialogVisibility(visible: Boolean) {
        updateScreenState { mutate(showDeleteDialog = visible) }
    }

    private inline fun updateScreenState(block: ScreenState.() -> ScreenState) {
        savedStateHandle[SCREEN_STATE_KEY] = state.value.run(block)
    }
}
