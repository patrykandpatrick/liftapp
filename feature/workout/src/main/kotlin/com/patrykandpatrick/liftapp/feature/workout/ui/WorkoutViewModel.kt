package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.GetEditableWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertExerciseSetUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertGoalSetsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
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

    val workout: StateFlow<EditableWorkout?> =
        getEditableWorkoutUseCase().stateIn(coroutineScope, SharingStarted.Lazily, null)

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
