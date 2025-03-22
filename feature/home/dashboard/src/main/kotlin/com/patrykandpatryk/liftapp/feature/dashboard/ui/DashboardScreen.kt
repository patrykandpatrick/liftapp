package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.increaseBy
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.feature.dashboard.model.Action

@Composable
fun DashboardScreen(
    padding: PaddingValues,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier =
            modifier.padding(padding).nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_dashboard),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { paddingValues ->
        val state = viewModel.state.collectAsState().value ?: return@Scaffold

        LazyColumn(
            contentPadding =
                paddingValues.increaseBy(
                    horizontal = LocalDimens.current.padding.contentHorizontal,
                    vertical = LocalDimens.current.padding.contentVertical,
                ),
            verticalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemVertical),
            modifier = Modifier,
        ) {
            if (state.activeWorkouts.isNotEmpty()) {
                item {
                    ListSectionTitle(
                        title =
                            if (state.activeWorkouts.size == 1) {
                                stringResource(R.string.dashboard_section_active_workout)
                            } else {
                                stringResource(R.string.dashboard_section_active_workouts)
                            },
                        paddingValues =
                            PaddingValues(
                                start = LocalDimens.current.padding.contentHorizontal,
                                top = LocalDimens.current.padding.itemVerticalSmall,
                                bottom = 4.dp,
                                end = LocalDimens.current.padding.contentHorizontal,
                            ),
                    )
                }

                items(items = state.activeWorkouts, key = { it.id }) { workout ->
                    WorkoutCard(
                        workout = workout,
                        onClick = { viewModel.onAction(Action.OpenWorkout(workout.id)) },
                    )
                }
            }

            if (state.pastWorkouts.isNotEmpty()) {
                item {
                    ListSectionTitle(
                        title =
                            if (state.pastWorkouts.size == 1) {
                                stringResource(R.string.dashboard_section_recent_workout)
                            } else {
                                stringResource(R.string.dashboard_section_recent_workouts)
                            },
                        paddingValues =
                            PaddingValues(
                                start = LocalDimens.current.padding.contentHorizontal,
                                top = LocalDimens.current.padding.itemVerticalSmall,
                                bottom = 4.dp,
                                end = LocalDimens.current.padding.contentHorizontal,
                            ),
                    )
                }

                items(items = state.pastWorkouts, key = { it.id }) { workout ->
                    WorkoutCard(
                        workout = workout,
                        onClick = { viewModel.onAction(Action.OpenWorkout(workout.id)) },
                    )
                }
            }
        }
    }
}
