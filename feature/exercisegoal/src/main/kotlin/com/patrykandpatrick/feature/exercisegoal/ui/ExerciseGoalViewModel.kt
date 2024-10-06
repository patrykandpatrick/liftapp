package com.patrykandpatrick.feature.exercisegoal.ui

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope

@HiltViewModel(assistedFactory = ExerciseGoalViewModel.Factory::class)
class ExerciseGoalViewModel @AssistedInject constructor(
    @Assisted("routineID") private val routineID: Long,
    @Assisted("exerciseID") private val exerciseID: Long,
    stateFactory: ExerciseGoalState.Factory,
    coroutineScope: CoroutineScope,
) : ViewModel() {
    val state: ExerciseGoalState = stateFactory.create(routineID, exerciseID, coroutineScope)

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("routineID") routineID: Long,
            @Assisted("exerciseID") exerciseID: Long,
        ): ExerciseGoalViewModel
    }
}
