package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.GetEditableWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpdateWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertExerciseSetUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertGoalSetsUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutIterator
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.domain.Constants.Workout.EXERCISE_CHANGE_DELAY
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommand
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class WorkoutViewModel
@Inject
constructor(
    getEditableWorkoutUseCase: GetEditableWorkoutUseCase,
    private val upsertGoalSets: UpsertGoalSetsUseCase,
    private val upsertExerciseSet: UpsertExerciseSetUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    private val navigationCommander: NavigationCommander,
    coroutineScope: CoroutineScope,
    savedStateHandle: SavedStateHandle,
) : ViewModel(coroutineScope) {

    private val customPage = MutableSharedFlow<Int>()

    private val selectedItem =
        savedStateHandle.getMutableStateFlow<WorkoutIterator.Item?>(
            "selectedExerciseAndSetIndex",
            null,
        )

    val workout: StateFlow<EditableWorkout?> =
        getEditableWorkoutUseCase()
            .transformLatest { workout ->
                emit(workout)
                if (workout.endDate == null) {
                    keepEndDateTimeUpdated(workout.summary.endDate, workout.summary.endTime)
                }
            }
            .combine(selectedItem) { workout, selectedExerciseAndSetIndex ->
                workout.copy(
                    selectedSelectedExerciseAndSet =
                        selectedExerciseAndSetIndex?.let { (_, exerciseIndex, setIndex) ->
                            workout.iterator.getItem(exerciseIndex, setIndex)
                        }
                )
            }
            .stateIn(coroutineScope, SharingStarted.Lazily, null)

    val selectedPage: StateFlow<Int> =
        merge(
                customPage,
                workout
                    .filterNotNull()
                    .distinctUntilChangedBy { it.startPageIndex }
                    .withIndex()
                    .transform { (index, workout) ->
                        if (index > 0) delay(EXERCISE_CHANGE_DELAY)
                        emit(workout.startPageIndex)
                    },
            )
            .stateIn(coroutineScope, SharingStarted.Lazily, 0)

    fun onAction(action: Action) {
        when (action) {
            is Action.MovePageBy -> onPageDelta(action.delta)
            is Action.SelectPage -> selectPage(action.pageIndex)
            is Action.SaveSet -> saveSet(action.workout, action.item)
            is Action.FinishWorkout -> finishWorkout()
            is Action.UpdateWorkoutName -> updateWorkoutName(action.name)
            is Action.UpdateWorkoutStartDateTime ->
                updateWorkoutStartDateTime(action.date, action.time)
            is Action.UpdateWorkoutEndDateTime -> updateWorkoutEndDateTime(action.date, action.time)
            is Action.UpdateWorkoutNotes -> updateWorkoutNotes(action.notes)
            is Action.AddSet -> updateSetCount(exercise = action.exercise, delta = 1)
            is Action.RemoveSet -> updateSetCount(exercise = action.exercise, delta = -1)
            is Action.GoToExerciseDetails -> goToExerciseDetails(action.exerciseID)
            is Action.PopBackStack -> popBackStack()
        }
    }

    private suspend fun keepEndDateTimeUpdated(
        endDate: TextFieldState<LocalDate>,
        endTime: TextFieldState<LocalTime>,
    ) {
        while (currentCoroutineContext().isActive) {
            endDate.updateValue(LocalDate.now())
            endTime.updateValue(LocalTime.now())
            delay((60 - LocalTime.now().second).seconds)
        }
    }

    private fun onPageDelta(delta: Int) {
        viewModelScope.launch {
            val pages = workout.filterNotNull().first().pages
            val currentPage = selectedPage.value
            val nextPage = (currentPage + delta).coerceIn(0, pages.lastIndex)
            customPage.emit(nextPage)
        }
    }

    fun selectPage(page: Int) {
        viewModelScope.launch { customPage.emit(page) }
    }

    private fun updateSetCount(exercise: EditableWorkout.Exercise, delta: Int) {
        viewModelScope.launch {
            upsertGoalSets(getWorkout().id, exercise, exercise.sets.size + delta)
        }
    }

    private suspend fun getWorkout(): EditableWorkout = workout.filterNotNull().first()

    private fun updateWorkoutName(name: TextFieldState<String>) {
        if (name.hasError) return
        viewModelScope.launch {
            val workoutID = getWorkout().id
            updateWorkoutUseCase(workoutID = workoutID, name = name.value)
        }
    }

    private fun updateWorkoutStartDateTime(
        date: TextFieldState<LocalDate>,
        time: TextFieldState<LocalTime>,
    ) {
        if (date.hasError) return
        viewModelScope.launch {
            updateWorkoutUseCase(
                workoutID = getWorkout().id,
                startDate = date.value.atTime(time.value),
            )
        }
    }

    private fun updateWorkoutEndDateTime(
        date: TextFieldState<LocalDate>,
        time: TextFieldState<LocalTime>,
    ) {
        if (date.hasError) return
        viewModelScope.launch {
            updateWorkoutUseCase(
                workoutID = getWorkout().id,
                endDate = date.value.atTime(time.value),
            )
        }
    }

    private fun updateWorkoutNotes(notes: TextFieldState<String>) {
        if (notes.hasError) return
        viewModelScope.launch {
            updateWorkoutUseCase(workoutID = getWorkout().id, notes = notes.value)
        }
    }

    private fun finishWorkout() {
        viewModelScope.launch {
            returnToHome()
            withContext(NonCancellable) {
                val workout = getWorkout()
                if (workout.endDate == null) {
                    updateWorkoutUseCase(workoutID = workout.id, endDate = LocalDateTime.now())
                }
            }
        }
    }

    fun saveSet(workout: EditableWorkout, item: WorkoutIterator.Item) {
        viewModelScope.launch {
            upsertExerciseSet(workout.id, item.exercise.id, item.set, item.setIndex)
            selectedItem.value = workout.iterator.getNextIncomplete(item)
        }
    }

    private fun goToExerciseDetails(exerciseID: Long) {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.Exercise.details(exerciseID))
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private suspend fun returnToHome() {
        navigationCommander.navigateTo(
            NavigationCommand.Route(route = Routes.Home, popUpTo = Routes.Home)
        )
    }
}
