package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.GetEditableWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertExerciseSetUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertGoalSetsUseCase
import com.patrykandpatryk.liftapp.domain.Constants.Workout.EXERCISE_CHANGE_DELAY
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutViewModel
@Inject
constructor(
    getEditableWorkoutUseCase: GetEditableWorkoutUseCase,
    private val upsertGoalSets: UpsertGoalSetsUseCase,
    private val upsertExerciseSet: UpsertExerciseSetUseCase,
    coroutineScope: CoroutineScope,
) : ViewModel(coroutineScope) {

    private val customPage = MutableSharedFlow<Int>()

    val workout: StateFlow<EditableWorkout?> =
        getEditableWorkoutUseCase().stateIn(coroutineScope, SharingStarted.Lazily, null)

    val selectedPage: StateFlow<Int> =
        merge(
                customPage,
                workout
                    .filterNotNull()
                    .map { it.firstIncompleteExerciseIndex }
                    .withIndex()
                    .transform {
                        if (it.index > 0) delay(EXERCISE_CHANGE_DELAY)
                        emit(it.value)
                    },
            )
            .stateIn(coroutineScope, SharingStarted.Lazily, 0)

    fun onPageDelta(delta: Int) {
        viewModelScope.launch {
            val workout = workout.filterNotNull().first()
            val currentPage = selectedPage.value
            val nextPage = (currentPage + delta).coerceIn(0, workout.exercises.lastIndex)
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

    fun saveSet(exercise: EditableWorkout.Exercise, set: EditableExerciseSet, setIndex: Int) {
        viewModelScope.launch { upsertExerciseSet(getWorkout().id, exercise.id, set, setIndex) }
    }
}
