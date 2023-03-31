package com.patrykandpatryk.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.NavGraphBuilder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.domain.state.StateHandler
import com.patrykandpatryk.liftapp.feature.workout.model.WorkoutEvent
import com.patrykandpatryk.liftapp.feature.workout.model.WorkoutIntent
import com.patrykandpatryk.liftapp.feature.workout.model.WorkoutState
import com.patrykandpatryk.liftapp.feature.workout.model.WorkoutStateHandler

fun NavGraphBuilder.addWorkout() {

    composable(route = Routes.Workout) {
        Workout()
    }
}

@Composable
fun Workout(modifier: Modifier = Modifier) {

    val viewModel: WorkoutViewModel = hiltViewModel()

    WorkoutContent(
        stateHandler = viewModel,
        modifier = modifier,
    )
}

@Composable
private fun WorkoutContent(
    stateHandler: StateHandler<WorkoutState, WorkoutIntent, WorkoutEvent>,
    modifier: Modifier = Modifier,
) {

    val state by stateHandler.state.collectAsState()

    Scaffold(modifier = modifier) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
        }
    }
}

@MultiDevicePreview
@Composable
fun WorkoutPreview() {
    LiftAppTheme {
        WorkoutContent(
            stateHandler = WorkoutStateHandler(
                mainDispatcher = Dispatchers.Main.immediate,
                exceptionHandler = CoroutineExceptionHandler { _, _ -> },
            ),
        )
    }
}
