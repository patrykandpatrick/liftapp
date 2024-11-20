package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope

@HiltViewModel(assistedFactory = WorkoutViewModel.Factory::class)
class WorkoutViewModel @AssistedInject constructor(
    @Assisted("routineID") routineID: Long,
    @Assisted("workoutID") workoutID: Long,
    workoutStateFactory: WorkoutState.Factory,
    coroutineScope: CoroutineScope,
) : ViewModel(coroutineScope) {

    val workoutState = workoutStateFactory.create(routineID, workoutID, coroutineScope)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("routineID") routineID: Long,
            @Assisted("workoutID") workoutID: Long,
        ): WorkoutViewModel
    }
}