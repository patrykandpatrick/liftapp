package com.patrykandpatrick.liftapp.feature.journal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.feature.dashboard.ui.WorkoutCard
import com.patrykandpatrick.liftapp.feature.home.ui.DeleteWorkoutDialog
import com.patrykandpatrick.liftapp.feature.home.ui.WorkoutOptionsModal
import com.patrykandpatrick.liftapp.feature.journal.model.Action
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.History
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.TriangleAlert
import kotlinx.coroutines.flow.Flow

/** The gap between one workout card and the next. */
private val CardSpacing = 12.dp

@Composable
fun JournalScreen(modifier: Modifier = Modifier, viewModel: JournalViewModel = hiltViewModel()) {
    JournalScreen(
        workouts = viewModel.workouts,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun JournalScreen(
    workouts: Flow<PagingData<Workout>>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pagedWorkouts = workouts.collectAsLazyPagingItems()
    var workoutWithOptions by remember { mutableStateOf<Workout?>(null) }
    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }

    LiftAppScaffold(
        modifier =
            modifier.fillMaxHeight().nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(R.string.generic_journal),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
            )
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { paddingValues ->
        val contentHorizontal = LocalDimens.current.screen.horizontalPadding
        if (pagedWorkouts.itemCount == 0 && pagedWorkouts.loadState.refresh is LoadState.Error) {
            EmptyState(
                icon = LiftAppIcons.TriangleAlert,
                message = stringResource(R.string.generic_error_title),
                modifier =
                    Modifier.fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = contentHorizontal),
                actions = {
                    LiftAppButton(onClick = pagedWorkouts::retry) {
                        Text(stringResource(R.string.action_try_again))
                    }
                },
            )
        } else if (
            pagedWorkouts.itemCount == 0 && pagedWorkouts.loadState.refresh is LoadState.NotLoading
        ) {
            EmptyState(
                icon = LiftAppIcons.History,
                message = stringResource(R.string.state_no_workouts),
                modifier =
                    Modifier.fillMaxSize()
                        .padding(paddingValues)
                        .padding(
                            horizontal = contentHorizontal,
                            vertical = LocalDimens.current.screen.verticalPadding,
                        ),
            )
        } else {
            LazyColumn(
                // As on the dashboard, only the vertical inset belongs to the list; the cards inset
                // themselves.
                contentPadding =
                    paddingValues.increaseBy(vertical = LocalDimens.current.screen.verticalPadding),
                verticalArrangement = Arrangement.spacedBy(CardSpacing),
                modifier = Modifier.fillMaxHeight(),
            ) {
                items(
                    count = pagedWorkouts.itemCount,
                    key = { index -> pagedWorkouts.peek(index)?.id ?: index },
                ) { index ->
                    val workout = pagedWorkouts[index]
                    if (workout != null) {
                        WorkoutCard(
                            workout = workout,
                            onClick = { onAction(Action.GoToWorkout(it.id)) },
                            onLongClick = { workoutWithOptions = it },
                            modifier = Modifier.padding(horizontal = contentHorizontal),
                        )
                    }
                }
            }
        }
    }

    WorkoutOptionsModal(
        workout = workoutWithOptions,
        onDismissRequest = { workoutWithOptions = null },
        onDeleteClick = { workoutToDelete = it },
    )

    DeleteWorkoutDialog(
        workout = workoutToDelete,
        onDismissRequest = { workoutToDelete = null },
        onConfirm = { workout ->
            workoutToDelete = null
            onAction(Action.DeleteWorkout(workout.id))
        },
    )
}
