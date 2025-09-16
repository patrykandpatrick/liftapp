package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.chart.ExtraStoreKey
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.TopAppBarWithTabs
import com.patrykandpatryk.liftapp.domain.date.DateInterval
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.exerciseset.getSummaryTypes
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.model.toLoadable
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.feature.exercise.model.Action
import com.patrykandpatryk.liftapp.feature.exercise.model.ExerciseTab
import com.patrykandpatryk.liftapp.feature.exercise.model.ScreenState
import com.patrykandpatryk.liftapp.feature.exercise.model.exerciseTabItems
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

    loadableState.Unfold { state ->
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

    LiftAppScaffold(
        modifier = modifier.imePadding(),
        topBar = {
            TopAppBarWithTabs(
                title = screenState.valueOrNull()?.name.orEmpty(),
                onBackClick = { onAction(Action.PopBackStack) },
                selectedTabIndex = { pagerState.currentPage },
                selectedTabOffset = { pagerState.currentPageOffsetFraction },
                onTabSelected = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                tabs = exerciseTabItems,
                actions = {
                    LiftAppIconButton(onClick = { onAction(Action.Edit) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = stringResource(id = R.string.action_edit),
                        )
                    }

                    LiftAppIconButton(onClick = { onAction(Action.ShowDeleteDialog) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = stringResource(id = R.string.action_delete),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        screenState.Unfold(modifier = Modifier.padding(paddingValues)) { state ->
            HorizontalPager(modifier = Modifier.fillMaxSize(), state = pagerState) { index ->
                when (ExerciseTab.entries[index]) {
                    ExerciseTab.Statistics ->
                        Statistics(
                            modifier = Modifier.fillMaxSize(),
                            state = state,
                            onAction = onAction,
                        )

                    ExerciseTab.Details -> Details()
                }
            }
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
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = stringResource(id = R.string.generic_delete_something, exerciseName))
            },
            text = { Text(text = stringResource(id = R.string.exercise_delete_message)) },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(id = R.string.action_delete))
                }
            },
        )
    }
}

@Composable
internal fun getScreenStateForPreview(): ScreenState {
    val cartesianChartModelProducer = CartesianChartModelProducer()
    val dateInterval = DateInterval.exerciseOptions.first()
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

            columnSeries {
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

    return ScreenState(
        name = "Bicep Curl",
        showDeleteDialog = false,
        primaryMuscles = emptyList(),
        secondaryMuscles = emptyList(),
        tertiaryMuscles = emptyList(),
        exerciseSetGroups = emptyList(),
        cartesianChartModelProducer = cartesianChartModelProducer,
        dateInterval = dateInterval,
        dateIntervalOptions = DateInterval.exerciseOptions,
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
