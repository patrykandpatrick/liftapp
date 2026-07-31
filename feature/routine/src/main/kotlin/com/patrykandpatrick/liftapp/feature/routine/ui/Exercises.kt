package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.model.getPrettyStringLong
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.feature.routine.model.Action
import com.patrykandpatrick.liftapp.feature.routine.model.ScreenState
import com.patrykandpatrick.liftapp.ui.icons.Goal
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
internal fun Exercises(
    loadableState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    loadableState.Unfold { state ->
        Exercises(state = state, onAction = onAction, modifier = modifier)
    }
}

@Composable
private fun Exercises(
    state: ScreenState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize().background(colorScheme.background)) {
        itemsIndexed(items = state.exercises, key = { _, exercise -> exercise.id }) {
            index,
            exercise ->
            ListItem(
                exercise = exercise,
                onItemClick = { onAction(Action.NavigateToExercise(it)) },
                onGoalClick = { onAction(Action.NavigateToExerciseGoal(it)) },
                modifier = Modifier.animateItem(),
            )
        }
    }
}

@Composable
fun ListItem(
    exercise: RoutineExerciseItem,
    onItemClick: (exerciseID: Long) -> Unit,
    onGoalClick: (exerciseID: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        title = { Text(exercise.name) },
        modifier = modifier,
        description = {
            Text(
                buildAnnotatedString {
                    append(exercise.goal.getPrettyStringLong(exercise.type))
                    append("\n")
                    append(exercise.muscles)
                }
            )
        },
        actions = {
            IconButton(onClick = { onGoalClick(exercise.id) }) {
                Icon(
                    imageVector = LiftAppIcons.Goal,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        },
    ) {
        onItemClick(exercise.id)
    }
}
