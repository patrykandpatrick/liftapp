package com.patrykandpatryk.liftapp.feature.routine.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.provider.navigator
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.feature.routine.model.ScreenState

@Composable
internal fun Exercises(
    modifier: Modifier = Modifier,
) {

    val viewModel: RoutineViewModel = hiltViewModel()

    val state by viewModel.state.collectAsState()

    Exercises(
        state = state,
        modifier = modifier,
    )
}

@Composable
private fun Exercises(
    state: ScreenState,
    modifier: Modifier = Modifier,
) {

    val navigator = navigator

    LazyColumn(
        modifier = modifier.fillMaxSize(),
    ) {

        items(
            items = state.exercises,
            key = { it.id },
        ) { exercise ->

            ListItem(
                iconPainter = painterResource(id = exercise.iconRes),
                title = exercise.name,
                description = exercise.muscles,
            ) {
                navigator.navigate(Routes.Exercise.create(exerciseId = exercise.id))
            }
        }
    }
}
