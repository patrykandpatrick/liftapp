package com.patrykandpatrick.liftapp.feature.dashboard.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewRoutineWithExercises
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitleDefaults
import com.patrykandpatrick.liftapp.core.ui.TopAppBar
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.feature.dashboard.model.Action
import com.patrykandpatrick.liftapp.feature.dashboard.model.DashboardState
import com.patrykandpatrick.liftapp.feature.dashboard.model.DashboardStatistics
import com.patrykandpatrick.liftapp.feature.dashboard.model.PlanScheduleItem
import com.patrykandpatrick.liftapp.feature.dashboard.model.WorkoutTarget
import com.patrykandpatrick.liftapp.feature.home.ui.DeleteWorkoutDialog
import com.patrykandpatrick.liftapp.feature.home.ui.WorkoutOptionsModal
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.Dumbbell
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import java.time.DayOfWeek
import java.time.LocalDate
import kotlin.time.Duration.Companion.minutes

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
    val dimens = LocalDimens.current
    val fabHeight = 24.dp + dimens.fab.verticalPadding * 2

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            loadableState.Unfold { state ->
                state.workoutTarget?.let { target ->
                    LiftAppFAB(
                        onClick = {
                            when (target) {
                                is WorkoutTarget.ActiveWorkout ->
                                    onAction(Action.GoToWorkout(target.workoutID))
                                is WorkoutTarget.PlannedRoutine ->
                                    onAction(Action.NewWorkout(target.routineID))
                            }
                        }
                    ) {
                        Icon(LiftAppIcons.Dumbbell, contentDescription = null)
                        Text(stringResource(R.string.action_work_out))
                    }
                }
            }
        },
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
                // Only the vertical inset belongs to the list. Each element insets itself
                // horizontally, so that one wanting to sit closer to the edge — or to bleed past
                // it — can, without unpicking a padding the list imposed on everything.
                contentPadding =
                    paddingValues.increaseBy(
                        top = dimens.screen.verticalPadding,
                        bottom =
                            dimens.screen.verticalPadding +
                                if (state.workoutTarget != null) {
                                    fabHeight + 16.dp
                                } else {
                                    0.dp
                                },
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
    var workoutWithOptions by remember { mutableStateOf<Workout?>(null) }
    var workoutToDelete by remember { mutableStateOf<Workout?>(null) }

    // The list leaves the gap that belongs between neighbors within a section, and each section
    // adds the rest of the wider gap above itself. Spacing the list by the wider gap instead would
    // push a heading away from its own cards wherever the heading is a list item of its own.
    val contentHorizontal = LocalDimens.current.screen.horizontalPadding
    LazyColumn(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(ListSectionTitleDefaults.withinSectionSpacing),
        modifier = Modifier.fillMaxSize(),
    ) {
        val hasActiveWorkouts = state.activeWorkouts.isNotEmpty()

        if (hasActiveWorkouts) {
            item(key = "active_workouts") {
                ListSectionTitle(
                    title =
                        if (state.activeWorkouts.size == 1) {
                            stringResource(R.string.dashboard_section_active_workout)
                        } else {
                            stringResource(R.string.dashboard_section_active_workouts)
                        },
                    isFirstSection = true,
                    modifier = Modifier.animateItem(),
                )
            }

            items(items = state.activeWorkouts, key = { "workout:${it.id}" }) { workout ->
                WorkoutCard(
                    workout = workout,
                    onClick = { onAction(Action.GoToWorkout(workout.id)) },
                    modifier = Modifier.animateItem().padding(horizontal = contentHorizontal),
                )
            }
        }

        // No heading: the top bar already names the week these totals cover. The gap that a heading
        // would have carried has to be asked for instead, or this section would sit as close to the
        // one above it as a card sits to its neighbor.
        item(key = "statistics") {
            Statistics(
                statistics = state.statistics,
                modifier =
                    Modifier.padding(
                        top = sectionTopPadding(isFirst = !hasActiveWorkouts),
                        start = contentHorizontal,
                        end = contentHorizontal,
                    ),
            )
        }

        item(key = "schedule") {
            ListSectionTitle(title = stringResource(R.string.dashboard_section_schedule))
            DaysOfWeek(
                dateItems = state.dayItems,
                onClick = { onAction(Action.SelectDate(it)) },
                modifier =
                    Modifier.padding(
                        top = ListSectionTitleDefaults.withinSectionSpacing,
                        start = contentHorizontal,
                        end = contentHorizontal,
                    ),
            )
            PlanItem(
                item = state.planScheduleItem,
                isToday = state.dayItems.any { it.isSelected && it.isToday },
                onAction = onAction,
                modifier =
                    Modifier.padding(
                        top = ListSectionTitleDefaults.withinSectionSpacing,
                        start = contentHorizontal,
                        end = contentHorizontal,
                    ),
            )
        }

        item(key = "shortcuts") {
            ListSectionTitle(title = stringResource(R.string.shortcut_section_title))
            Shortcuts(
                onAction = onAction,
                modifier =
                    Modifier.padding(
                        top = ListSectionTitleDefaults.withinSectionSpacing,
                        start = contentHorizontal,
                        end = contentHorizontal,
                    ),
            )
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
                    trailingIcon =
                        if (!state.hasMorePastWorkouts) null
                        else {
                            {
                                PlainLiftAppButton(
                                    onClick = { onAction(Action.Navigate(Routes.Journal)) }
                                ) {
                                    Text(stringResource(R.string.action_show_all))
                                }
                            }
                        },
                )
            }

            items(items = state.pastWorkouts, key = { it.id }) { workout ->
                WorkoutCard(
                    workout = workout,
                    onClick = { onAction(Action.GoToWorkout(workout.id)) },
                    onLongClick = { workoutWithOptions = it },
                    modifier = Modifier.animateItem().padding(horizontal = contentHorizontal),
                )
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

/**
 * What a section adds above itself so that the gap to the section before comes to
 * [ListSectionTitleDefaults.betweenSectionsSpacing], the list having already left
 * [ListSectionTitleDefaults.withinSectionSpacing]. The first section adds nothing: there is nothing
 * above it to be separated from, only the padding the list itself begins with.
 */
private fun sectionTopPadding(isFirst: Boolean): Dp = ListSectionTitleDefaults.topPadding(isFirst)

/**
 * The list already insets its content, so no horizontal padding is added here; that would set the
 * titles in further than the cards they head.
 */
@Composable
private fun ListSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    isFirstSection: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val contentHorizontal = LocalDimens.current.screen.horizontalPadding
    // A button's own padding is invisible until it is pressed, so the inset gives way to it,
    // leaving the label on the edge the cards below sit on. The vertical padding needs no such
    // care: the heading alone sets the row's height.
    val buttonPadding = LiftAppButtonDefaults.plainContentPadding
    val layoutDirection = LocalLayoutDirection.current
    com.patrykandpatrick.liftapp.core.ui.ListSectionTitle(
        title = title,
        paddingValues =
            PaddingValues(
                start = contentHorizontal,
                end =
                    if (trailingIcon == null) contentHorizontal
                    else
                        (contentHorizontal - buttonPadding.calculateEndPadding(layoutDirection))
                            .coerceAtLeast(0.dp),
                top = sectionTopPadding(isFirstSection),
                bottom = ListSectionTitleDefaults.bottomPadding,
            ),
        modifier = modifier,
        trailingIcon = trailingIcon,
    )
}

@Composable
private fun PlanItem(
    item: PlanScheduleItem,
    isToday: Boolean,
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
                is PlanScheduleItem.None ->
                    NonePlanItem(
                        hasActivePlan = planItem.hasActivePlan,
                        isToday = isToday,
                        onAction = onAction,
                    )
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
        statistics =
            DashboardStatistics(
                volume = 12_480.0,
                volumeUnit = MassUnit.Kilograms,
                reps = 412,
                workouts = 4,
                timeExercised = 226.minutes,
            ),
        dayItems = DashboardViewModel.getWeekDays(today, DayOfWeek.MONDAY),
        selectedDate = today,
        activeWorkouts = emptyList(),
        pastWorkouts = emptyList(),
        hasMorePastWorkouts = false,
        planScheduleItem =
            PlanScheduleItem.Routine(
                routine = PreviewRoutineWithExercises.routines.first(),
                workout = null,
            ),
        workoutTarget =
            WorkoutTarget.PlannedRoutine(PreviewRoutineWithExercises.routines.first().id),
    )
}
