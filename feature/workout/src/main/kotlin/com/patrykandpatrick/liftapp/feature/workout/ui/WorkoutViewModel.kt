package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.GetEditableWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpdateWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertExerciseSetUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertGoalSetsUseCase
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.domain.Constants.Workout.EXERCISE_CHANGE_DELAY
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

@HiltViewModel
class WorkoutViewModel
@Inject
constructor(
    getEditableWorkoutUseCase: GetEditableWorkoutUseCase,
    private val upsertGoalSets: UpsertGoalSetsUseCase,
    private val upsertExerciseSet: UpsertExerciseSetUseCase,
    private val updateWorkoutUseCase: UpdateWorkoutUseCase,
    coroutineScope: CoroutineScope,
) : ViewModel(coroutineScope) {

    private val customPage = MutableSharedFlow<Int>()

    val workout: StateFlow<EditableWorkout?> =
        getEditableWorkoutUseCase()
            .transformLatest { workout ->
                emit(workout)
                if (workout.endDate == null) {
                    keepEndDateTimeUpdated(workout.summary.endDate, workout.summary.endTime)
                }
            }
            .stateIn(coroutineScope, SharingStarted.Lazily, null)

    val selectedPage: StateFlow<Int> =
        merge(
                customPage,
                workout
                    .filterNotNull()
                    .distinctUntilChangedBy { it.firstIncompleteExerciseIndex }
                    .withIndex()
                    .transform { (index, workout) ->
                        val firstIncompleteExerciseIndex = workout.firstIncompleteExerciseIndex
                        if (index > 0) delay(EXERCISE_CHANGE_DELAY)
                        val page =
                            if (firstIncompleteExerciseIndex == -1) workout.exercises.size
                            else firstIncompleteExerciseIndex
                        emit(page)
                    },
            )
            .stateIn(coroutineScope, SharingStarted.Lazily, 0)

    fun onAction(action: Action) {
        when (action) {
            is Action.MovePageBy -> onPageDelta(action.delta)
            is Action.FinishWorkout -> Unit // TODO
            is Action.UpdateWorkoutName -> updateWorkoutName(action.name)
            is Action.UpdateWorkoutStartDateTime ->
                updateWorkoutStartDateTime(action.date, action.time)
            is Action.UpdateWorkoutEndDateTime -> updateWorkoutEndDateTime(action.date, action.time)
            is Action.UpdateWorkoutNotes -> updateWorkoutNotes(action.notes)
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

    fun increaseSetCount(exercise: EditableWorkout.Exercise) {
        viewModelScope.launch { upsertGoalSets(getWorkout().id, exercise, 1) }
    }

    fun decreaseSetCount(exercise: EditableWorkout.Exercise) {
        viewModelScope.launch { upsertGoalSets(getWorkout().id, exercise, -1) }
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

    fun saveSet(exercise: EditableWorkout.Exercise, set: EditableExerciseSet, setIndex: Int) {
        viewModelScope.launch { upsertExerciseSet(getWorkout().id, exercise.id, set, setIndex) }
    }
}
