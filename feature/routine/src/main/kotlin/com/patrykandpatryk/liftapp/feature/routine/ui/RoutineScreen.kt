package com.patrykandpatryk.liftapp.feature.routine.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.TopAppBarWithTabs
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatryk.liftapp.feature.routine.model.Action
import com.patrykandpatryk.liftapp.feature.routine.model.RoutineTab
import com.patrykandpatryk.liftapp.feature.routine.model.ScreenState
import com.patrykandpatryk.liftapp.feature.routine.model.routineTabItems
import kotlinx.coroutines.launch

@Composable
fun RoutineScreen(modifier: Modifier = Modifier) {
    val viewModel: RoutineViewModel = hiltViewModel()

    val loadableState by viewModel.screenState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    RoutineScreen(
        modifier = modifier,
        loadableState = loadableState,
        onAction = viewModel::handleAction,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
private fun RoutineScreen(
    modifier: Modifier = Modifier,
    loadableState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val pagerState = rememberPagerState { RoutineTab.entries.size }
    val scope = rememberCoroutineScope()

    LiftAppScaffold(
        modifier = modifier,
        topBar = {
            TopAppBarWithTabs(
                title = loadableState.valueOrNull()?.name.orEmpty(),
                onBackClick = { onAction(Action.PopBackStack) },
                actions = {
                    LiftAppIconButton(onClick = { onAction(Action.Edit) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = stringResource(id = R.string.action_edit),
                        )
                    }
                },
                selectedTabIndex = { pagerState.currentPage },
                selectedTabOffset = { pagerState.currentPageOffsetFraction },
                onTabSelected = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                tabs = routineTabItems,
            )
        },
        floatingActionButton = {
            LiftAppFAB(onClick = { onAction(Action.StartWorkout) }) {
                Icon(painterResource(R.drawable.ic_workout), null)
                Text(stringResource(R.string.action_work_out))
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            contentPadding = paddingValues,
        ) { index ->
            when (RoutineTab.entries[index]) {
                RoutineTab.Exercises -> Exercises(loadableState, onAction)
                RoutineTab.Details -> Details(loadableState)
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun RoutineScreenPreview() {
    PreviewTheme {
        RoutineScreen(
            loadableState =
                Loadable.Success(
                    ScreenState(
                        name = "Full Body",
                        exercises =
                            listOf(
                                RoutineExerciseItem(
                                    0L,
                                    "Bench Press",
                                    "Chest",
                                    ExerciseType.Weight,
                                    Goal.default,
                                ),
                                RoutineExerciseItem(
                                    1L,
                                    "Squat",
                                    "Legs",
                                    ExerciseType.Weight,
                                    Goal.default,
                                ),
                                RoutineExerciseItem(
                                    2L,
                                    "Deadlift",
                                    "Back",
                                    ExerciseType.Weight,
                                    Goal.default,
                                ),
                            ),
                        primaryMuscles = emptyList(),
                        secondaryMuscles = emptyList(),
                        tertiaryMuscles = emptyList(),
                    )
                ),
            onAction = {},
            snackbarHostState = SnackbarHostState(),
        )
    }
}
