package com.patrykandpatrick.liftapp.feature.exercise.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.chart.ExtraStoreKey
import com.patrykandpatrick.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.model.valueOrNull
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.LiftAppModalBottomSheetWithTopAppBar
import com.patrykandpatrick.liftapp.core.ui.TopAppBarWithTabs
import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseStatistics
import com.patrykandpatrick.liftapp.domain.exerciseset.getSummaryTypes
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.model.toLoadable
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.feature.exercise.model.Action
import com.patrykandpatrick.liftapp.feature.exercise.model.ExerciseTab
import com.patrykandpatrick.liftapp.feature.exercise.model.ScreenState
import com.patrykandpatrick.liftapp.feature.exercise.model.exerciseTabItems
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialogDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.Edit
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.MoreVertical
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.columnModel
import java.time.DayOfWeek
import java.time.LocalDateTime
import kotlin.random.Random
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun ExerciseDetailsScreen(modifier: Modifier = Modifier) {
    val viewModel: ExerciseDetailsViewModel = hiltViewModel()

    val loadableState by viewModel.screenState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    ExerciseDetailsScreen(
        modifier = modifier,
        screenState = loadableState,
        onAction = viewModel::handleIntent,
        snackbarHostState = snackbarHostState,
    )

    loadableState.Unfold(onError = null) { state ->
        DeleteExerciseDialog(
            isVisible = state.showDeleteDialog,
            exerciseName = state.name,
            onDismissRequest = { viewModel.handleIntent(Action.HideDeleteDialog) },
            onConfirm = { viewModel.handleIntent(Action.Delete) },
        )
    }
}

@Composable
private fun ExerciseDetailsScreen(
    screenState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState { ExerciseTab.entries.size }
    val scope = rememberCoroutineScope()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LiftAppScaffold(
        modifier =
            modifier.imePadding().nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarWithTabs(
                title = screenState.valueOrNull()?.name.orEmpty(),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
                selectedTabIndex = { pagerState.currentPage },
                selectedTabOffset = { pagerState.currentPageOffsetFraction },
                onTabSelected = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                tabs = exerciseTabItems,
                actions = {
                    val (optionsVisible, setOptionsVisible) = remember { mutableStateOf(false) }

                    OptionsModal(
                        isVisible = optionsVisible,
                        onDismissRequest = { setOptionsVisible(false) },
                        onAction = onAction,
                    )

                    LiftAppIconButton(onClick = { setOptionsVisible(true) }) {
                        Icon(
                            imageVector = LiftAppIcons.MoreVertical,
                            contentDescription = stringResource(id = R.string.action_more),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        screenState.Unfold(
            modifier =
                Modifier.padding(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding(),
                    end = paddingValues.calculateEndPadding(layoutDirection),
                )
        ) { state ->
            HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { index ->
                when (ExerciseTab.entries[index]) {
                    ExerciseTab.Statistics ->
                        Statistics(
                            modifier = Modifier.fillMaxSize(),
                            state = state,
                            onAction = onAction,
                            bottomContentPadding = paddingValues.calculateBottomPadding(),
                        )

                    ExerciseTab.Details -> Details()
                }
            }
        }
    }
}

@Composable
private fun OptionsModal(
    isVisible: Boolean,
    onDismissRequest: () -> Unit,
    onAction: (Action) -> Unit,
) {
    if (isVisible) {
        LiftAppModalBottomSheetWithTopAppBar(
            onDismissRequest = onDismissRequest,
            containerColor = colorScheme.background,
        ) { dismiss ->
            Spacer(Modifier.height(8.dp))

            LiftAppListItem(
                title = { Text(stringResource(R.string.action_edit)) },
                icon = {
                    LiftAppListItemDefaults.Icon {
                        Icon(
                            imageVector = LiftAppIcons.Edit,
                            contentDescription = stringResource(id = R.string.action_edit),
                        )
                    }
                },
                position = LiftAppListItemPosition(index = 0, count = 2),
                modifier = Modifier.padding(horizontal = dimens.screen.padding),
                onClick = {
                    dismiss()
                    onAction(Action.Edit)
                },
            )

            LiftAppListItem(
                title = { Text(stringResource(R.string.action_delete)) },
                icon = {
                    LiftAppListItemDefaults.Icon {
                        Icon(
                            imageVector = LiftAppIcons.Delete,
                            contentDescription = stringResource(id = R.string.action_delete),
                        )
                    }
                },
                position = LiftAppListItemPosition(index = 1, count = 2),
                modifier = Modifier.padding(horizontal = dimens.screen.padding),
                onClick = {
                    dismiss()
                    onAction(Action.ShowDeleteDialog)
                },
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DeleteExerciseDialog(
    isVisible: Boolean,
    exerciseName: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (isVisible) {
        LiftAppAlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.generic_delete_something, exerciseName))
            },
            text = { Text(text = stringResource(id = R.string.exercise_delete_message)) },
            dismissButton = {
                LiftAppAlertDialogDefaults.DismissButton(
                    onClick = onDismissRequest,
                    text = stringResource(id = android.R.string.cancel),
                )
            },
            confirmButton = {
                PlainLiftAppButton(onClick = onConfirm) {
                    Text(text = stringResource(id = R.string.action_delete))
                }
            },
            icon = { Icon(imageVector = LiftAppIcons.Delete, contentDescription = null) },
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Composable
internal fun getScreenStateForPreview(): ScreenState {
    val cartesianChartModelProducer = CartesianChartModelProducer()
    val dateInterval = DateInterval.exerciseOptions(DayOfWeek.MONDAY).first()
    val summaryTypeOptions = ExerciseType.Weight.getSummaryTypes()

    runBlocking {
        cartesianChartModelProducer.runTransaction {
            extras {
                it[ExtraStoreKey.MinX] =
                    dateInterval.periodStartTime.toLocalDate().toEpochDay().toDouble()
                it[ExtraStoreKey.MaxX] =
                    dateInterval.periodEndTime.toLocalDate().toEpochDay().toDouble()
                it[ExtraStoreKey.DateInterval] = dateInterval
                it[ExtraStoreKey.ValueUnit] = MassUnit.Kilograms
            }
            val x = buildList {
                var current = dateInterval.periodStartTime
                while (current.isBefore(dateInterval.periodEndTime)) {
                    add(current.toLocalDate().toEpochDay())
                    current = current.plusDays(3)
                }
            }

            columnModel {
                series(
                    x = x,
                    y = x.mapIndexed { index, _ -> 45.0 + (index * Random.nextDouble(-.2, .35)) },
                )
                series(
                    x = x,
                    y = x.mapIndexed { index, _ -> 45.0 + (index * Random.nextDouble(-.2, .35)) },
                )
                series(
                    x = x,
                    y = x.mapIndexed { index, _ -> 45.0 + (index * Random.nextDouble(-.2, .35)) },
                )
                series(
                    x = x,
                    y = x.mapIndexed { index, _ -> 45.0 + (index * Random.nextDouble(-.2, .35)) },
                )
                series(
                    x = x,
                    y = x.mapIndexed { index, _ -> 45.0 + (index * Random.nextDouble(-.2, .35)) },
                )
            }
        }
    }

    val exerciseSetGroups =
        listOf(
            ExerciseSetGroup(
                workoutID = 1,
                workoutName = "Upper body",
                exerciseID = 1,
                sets =
                    listOf(
                        ExerciseSet.Weight(
                            weight = 42.5,
                            reps = 8,
                            weightUnit = MassUnit.Kilograms,
                            notes = "Keep the eccentric slow and controlled.",
                        ),
                        ExerciseSet.Weight(
                            weight = 42.5,
                            reps = 7,
                            weightUnit = MassUnit.Kilograms,
                        ),
                        ExerciseSet.Weight(
                            weight = 40.0,
                            reps = 8,
                            weightUnit = MassUnit.Kilograms,
                            notes = "Reduce the weight before form breaks down.",
                        ),
                    ),
                workoutStartDate = LocalDateTime.now().minusDays(2),
                notes = "Keep the elbows close to the body.",
            )
        )

    return ScreenState(
        name = "Bicep Curl",
        showDeleteDialog = false,
        primaryMuscles = emptyList(),
        secondaryMuscles = emptyList(),
        tertiaryMuscles = emptyList(),
        hasExerciseHistory = true,
        exerciseSetGroups = exerciseSetGroups,
        exerciseStatistics =
            ExerciseStatistics.Weight(
                totalVolume = 957.5,
                totalReps = 23,
                minimumWeight = 40.0,
                maximumWeight = 42.5,
                massUnit = MassUnit.Kilograms,
            ),
        cartesianChartModelProducer = cartesianChartModelProducer,
        dateInterval = dateInterval,
        dateIntervalOptions = DateInterval.exerciseOptions(DayOfWeek.MONDAY),
        summaryType = summaryTypeOptions.first(),
        summaryTypeOptions = summaryTypeOptions,
    )
}

@MultiDevicePreview
@Composable
fun PreviewExerciseDetails() {
    PreviewTheme {
        ExerciseDetailsScreen(
            screenState = getScreenStateForPreview().toLoadable(),
            onAction = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewDeleteExerciseDialog() {
    PreviewTheme {
        DeleteExerciseDialog(
            isVisible = true,
            exerciseName = "Bicep Curl",
            onDismissRequest = {},
            onConfirm = {},
        )
    }
}
