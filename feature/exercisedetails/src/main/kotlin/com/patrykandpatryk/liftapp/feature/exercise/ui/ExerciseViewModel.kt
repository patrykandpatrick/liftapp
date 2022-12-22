package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.logging.LogPublisher
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.core.model.MuscleModel
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatryk.liftapp.domain.exercise.GetExerciseUseCase
import com.patrykandpatryk.liftapp.domain.muscle.MuscleImageProvider
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.feature.exercise.di.ExerciseId
import com.patrykandpatryk.liftapp.feature.exercise.model.Event
import com.patrykandpatryk.liftapp.feature.exercise.model.Intent
import com.patrykandpatryk.liftapp.feature.exercise.model.ScreenState
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
class ExerciseViewModel @Inject constructor(
    @ExerciseId private val exerciseId: Long,
    getExercise: GetExerciseUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val logger: UiLogger,
    private val muscleImageProvider: MuscleImageProvider,
    isDarkModeReceiver: IsDarkModeReceiver,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Event>, LogPublisher by logger {

    private val eventChannel = Channel<Event>()

    override val state: StateFlow<ScreenState> = savedStateHandle
        .getStateFlow(SCREEN_STATE_KEY, ScreenState.Loading)

    override val events: Flow<Event> = eventChannel.receiveAsFlow()

    init {
        combine(
            getExercise(exerciseId),
            isDarkModeReceiver(),
        ) { exercise, isSystemInLightMode ->
            if (exercise == null) {
                Timber.e("Exercise with id $exerciseId not found!")
                eventChannel.send(Event.ExerciseNotFound)
            } else {

                val bitmapPath = muscleImageProvider.getMuscleImagePath(
                    exercise.mainMuscles,
                    exercise.secondaryMuscles,
                    exercise.tertiaryMuscles,
                    isDark = isSystemInLightMode,
                )

                updateScreenState {
                    mutate(
                        name = exercise.displayName,
                        imagePath = bitmapPath,
                        muscles = MuscleModel.create(
                            primaryMuscles = exercise.mainMuscles,
                            secondaryMuscles = exercise.secondaryMuscles,
                            tertiaryMuscles = exercise.tertiaryMuscles,
                        ),
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    override fun handleIntent(intent: Intent) {
        when (intent) {
            Intent.Delete -> updateScreenState { mutate(showDeleteDialog = false) }
            Intent.Edit -> sendEditExerciseEvent()
            Intent.HideDeleteDialog -> updateScreenState { mutate(showDeleteDialog = false) }
            Intent.ShowDeleteDialog -> updateScreenState { mutate(showDeleteDialog = true) }
        }
    }

    private fun sendEditExerciseEvent() {
        viewModelScope.launch {
            eventChannel.send(Event.EditExercise(id = exerciseId))
        }
    }

    private inline fun updateScreenState(block: ScreenState.() -> ScreenState) {
        savedStateHandle[SCREEN_STATE_KEY] = state.value.run(block)
    }
}
