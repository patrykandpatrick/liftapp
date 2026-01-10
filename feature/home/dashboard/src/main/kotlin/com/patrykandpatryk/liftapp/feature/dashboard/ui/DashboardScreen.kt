package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.increaseBy
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewRoutineWithExercises
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.feature.dashboard.model.Action
import com.patrykandpatryk.liftapp.feature.dashboard.model.DashboardState
import com.patrykandpatryk.liftapp.feature.dashboard.model.PlanScheduleItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val loadableState = viewModel.state.collectAsState().value

    DashboardScreen(
        loadableState = loadableState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun DashboardScreen(
    loadableState: Loadable<DashboardState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_dashboard),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { paddingValues ->
        loadableState.Unfold { state ->
            Content(
                state = state,
                onAction = onAction,
                contentPadding =
                    paddingValues.increaseBy(
                        horizontal = LocalDimens.current.padding.contentHorizontal,
                        vertical = LocalDimens.current.padding.contentVertical,
                    ),
            )
        }
    }
}

@Composable
private fun Content(
    state: DashboardState,
    onAction: (Action) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemVertical),
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "shortcuts") {
            ListSectionTitle(title = "Shortcuts" /* TODO */)
            Shortcuts(
                onAction = onAction,
                modifier = Modifier.padding(vertical = dimens.padding.itemVertical),
            )
        }

        item(key = "days_of_week") {
            DaysOfWeek(dateItems = state.dayItems, onClick = { onAction(Action.SelectDate(it)) })
        }

        item(key = "selected_date") {
            val datePattern = stringResource(R.string.date_weekday_day_month)
            val dateFormatter = remember(datePattern) { DateTimeFormatter.ofPattern(datePattern) }
            AnimatedContent(
                targetState = dateFormatter.format(state.selectedDate),
                modifier = Modifier.animateItem(),
            ) { title ->
                ListSectionTitle(title = title)
            }
        }

        item { PlanItem(state.planScheduleItem, onAction) }

        if (state.activeWorkouts.isNotEmpty()) {
            item(key = "active_workouts") {
                ListSectionTitle(
                    title =
                        if (state.activeWorkouts.size == 1) {
                            stringResource(R.string.dashboard_section_active_workout)
                        } else {
                            stringResource(R.string.dashboard_section_active_workouts)
                        },
                    modifier = Modifier.animateItem(),
                )
            }

            items(items = state.activeWorkouts, key = { "workout:${it.id}" }) { workout ->
                WorkoutCard(
                    workout = workout,
                    onClick = { onAction(Action.GoToWorkout(workout.id)) },
                    modifier = Modifier.animateItem(),
                )
            }
        }

        if (state.pastWorkouts.isNotEmpty()) {
            item(key = "past_workouts") {
                ListSectionTitle(
                    title =
                        if (state.pastWorkouts.size == 1) {
                            stringResource(R.string.dashboard_section_recent_workout)
                        } else {
                            stringResource(R.string.dashboard_section_recent_workouts)
                        },
                    modifier = Modifier.animateItem(),
                )
            }

            items(items = state.pastWorkouts, key = { it.id }) { workout ->
                WorkoutCard(
                    workout = workout,
                    onClick = { onAction(Action.GoToWorkout(workout.id)) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@Composable
private fun ListSectionTitle(title: String, modifier: Modifier = Modifier) {
    com.patrykandpatryk.liftapp.core.ui.ListSectionTitle(
        title = title,
        paddingValues =
            PaddingValues(
                start = LocalDimens.current.padding.contentHorizontal,
                top = LocalDimens.current.padding.itemVerticalSmall,
                bottom = 4.dp,
                end = LocalDimens.current.padding.contentHorizontal,
            ),
        modifier = modifier,
    )
}

@Composable
private fun PlanItem(
    item: PlanScheduleItem,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    LookaheadScope {
        AnimatedContent(
            targetState = item,
            contentAlignment = Alignment.TopCenter,
            modifier = modifier.fillMaxWidth(),
        ) { planItem ->
            when (planItem) {
                PlanScheduleItem.Rest -> RestPlanItem()
                is PlanScheduleItem.Routine -> RoutinePlanItem(planItem, onAction)
                else -> NonePlanItem(onAction)
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun DashboardScreenPreview() {
    PreviewTheme {
        DashboardScreen(loadableState = Loadable.Success(getPreviewDashboardState()), onAction = {})
    }
}

private fun getPreviewDashboardState(): DashboardState {
    val today = LocalDate.now()
    return DashboardState(
        dayItems = DashboardViewModel.getWeekDays(today),
        selectedDate = today,
        activeWorkouts = emptyList(),
        pastWorkouts = emptyList(),
        planScheduleItem =
            PlanScheduleItem.Routine(
                routine = PreviewRoutineWithExercises.routines.first(),
                workout = null,
            ),
    )
}
