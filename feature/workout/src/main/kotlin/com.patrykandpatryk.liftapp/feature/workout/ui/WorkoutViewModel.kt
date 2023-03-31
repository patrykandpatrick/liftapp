package com.patrykandpatryk.liftapp.feature.workout.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.patrykandpatryk.liftapp.domain.state.StateHandler
import com.patrykandpatryk.liftapp.feature.workout.model.WorkoutEvent
import com.patrykandpatryk.liftapp.feature.workout.model.WorkoutIntent
import com.patrykandpatryk.liftapp.feature.workout.model.WorkoutState
import com.patrykandpatryk.liftapp.feature.workout.model.WorkoutStateHandler
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val workoutStateHandler: WorkoutStateHandler,
) : ViewModel(workoutStateHandler), StateHandler<WorkoutState, WorkoutIntent, WorkoutEvent> by workoutStateHandler
