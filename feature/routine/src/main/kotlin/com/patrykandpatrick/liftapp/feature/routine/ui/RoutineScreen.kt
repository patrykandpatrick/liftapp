package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.backup.HandleShareBackupEvents
import com.patrykandpatrick.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatrick.liftapp.core.model.valueOrNull
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.TopAppBarWithTabs
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemWithExercises
import com.patrykandpatrick.liftapp.feature.routine.model.Action
import com.patrykandpatrick.liftapp.feature.routine.model.RoutineTab
import com.patrykandpatrick.liftapp.feature.routine.model.ScreenState
import com.patrykandpatrick.liftapp.feature.routine.model.routineTabItems
import com.patrykandpatrick.liftapp.ui.component.LiftAppBottomToolbar
import com.patrykandpatrick.liftapp.ui.component.LiftAppDestructiveActionDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.Delete
import com.patrykandpatrick.liftapp.ui.icons.Dumbbell
import com.patrykandpatrick.liftapp.ui.icons.Edit
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.icons.RefreshCcwDot
import com.patrykandpatrick.liftapp.ui.icons.Share
import kotlinx.coroutines.launch

@Composable
fun RoutineScreen(modifier: Modifier = Modifier) {
    val viewModel: RoutineViewModel = hiltViewModel()

    val loadableState by viewModel.screenState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    HandleShareBackupEvents(viewModel.share)

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
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val state = loadableState.valueOrNull()
    val showWorkoutFab = state?.items?.isNotEmpty() == true
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var removalToConfirm by remember { mutableStateOf<Action?>(null) }
    var fabHeight by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val dimens = LocalDimens.current

    if (showDeleteDialog && state != null) {
        DeleteRoutineDialog(
            routineName = state.name,
            onDismissRequest = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                onAction(Action.Delete)
            },
        )
    }

    val removalAction = removalToConfirm
    val removalCopy =
        when {
            removalAction is Action.RemoveItem && state != null -> {
                state.items
                    .firstOrNull { it.id == removalAction.itemID }
                    ?.let { item ->
                        val name =
                            if (item.isSuperset) {
                                stringResource(R.string.title_superset)
                            } else {
                                item.exercises.single().name
                            }
                        name to stringResource(R.string.routine_item_remove_message)
                    }
            }
            removalAction is Action.RemoveSupersetExercise && state != null -> {
                state.items
                    .firstOrNull { it.id == removalAction.itemID }
                    ?.exercises
                    ?.firstOrNull { it.id == removalAction.exerciseID }
                    ?.let { exercise ->
                        exercise.name to
                            stringResource(R.string.routine_superset_exercise_remove_message)
                    }
            }
            else -> null
        }

    if (removalAction != null && removalCopy != null) {
        LiftAppDestructiveActionDialog(
            title = stringResource(R.string.generic_remove_something, removalCopy.first),
            text = removalCopy.second,
            confirmText = stringResource(R.string.action_remove),
            dismissText = stringResource(android.R.string.cancel),
            onDismissRequest = { removalToConfirm = null },
            onConfirm = {
                removalToConfirm = null
                onAction(removalAction)
            },
        )
    }
    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBarWithTabs(
                title = loadableState.valueOrNull()?.name.orEmpty(),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = { onAction(Action.PopBackStack) },
                selectedTabIndex = { pagerState.currentPage },
                selectedTabOffset = { pagerState.currentPageOffsetFraction },
                onTabSelected = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
                tabs = routineTabItems,
            )
        },
        bottomBar = {
            RoutineActionBar(
                onEdit = { onAction(Action.Edit) },
                onDelete = { showDeleteDialog = true },
                onShare = { onAction(Action.Share) },
                onAddExercise = { onAction(Action.PickExercises(state?.exerciseIDs.orEmpty())) },
                onAddSuperset = { onAction(Action.NewSuperset) },
                enabled = state != null,
            )
        },
        floatingActionButton = {
            if (showWorkoutFab) {
                LiftAppFAB(
                    onClick = { onAction(Action.StartWorkout) },
                    modifier =
                        Modifier.onSizeChanged { size ->
                            fabHeight = with(density) { size.height.toDp() }
                        },
                ) {
                    Icon(LiftAppIcons.Dumbbell, null)
                    Text(stringResource(R.string.action_work_out))
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { paddingValues ->
        // The scaffold leaves the floating action button out of its padding, so the content keeps
        // its own room for it.
        val contentBottomPadding =
            if (showWorkoutFab) {
                fabHeight + ScaffoldFabBottomSpacing + dimens.screen.verticalPadding
            } else {
                0.dp
            }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            contentPadding = paddingValues,
        ) { index ->
            when (RoutineTab.entries[index]) {
                RoutineTab.Exercises ->
                    Exercises(
                        loadableState = loadableState,
                        onAction = { action ->
                            when (action) {
                                is Action.RemoveItem,
                                is Action.RemoveSupersetExercise -> removalToConfirm = action
                                else -> onAction(action)
                            }
                        },
                        bottomPadding = contentBottomPadding,
                    )
                RoutineTab.Details ->
                    Details(loadableState = loadableState, bottomPadding = contentBottomPadding)
            }
        }
    }
}

@Composable
private fun RoutineActionBar(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onAddExercise: () -> Unit,
    onAddSuperset: () -> Unit,
    enabled: Boolean,
) {
    LiftAppBottomToolbar {
        Row(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiftAppIconButton(onClick = onShare, enabled = enabled) {
                Icon(
                    imageVector = LiftAppIcons.Share,
                    contentDescription = stringResource(R.string.backup_action_share),
                )
            }
            LiftAppIconButton(onClick = onDelete, enabled = enabled) {
                Icon(
                    imageVector = LiftAppIcons.Delete,
                    contentDescription = stringResource(R.string.action_delete),
                )
            }
            LiftAppIconButton(onClick = onEdit, enabled = enabled) {
                Icon(
                    imageVector = LiftAppIcons.Edit,
                    contentDescription = stringResource(R.string.action_edit),
                )
            }
            LiftAppIconButton(onClick = onAddExercise, enabled = enabled) {
                Icon(
                    imageVector = LiftAppIcons.Plus,
                    contentDescription = stringResource(R.string.action_add_exercise),
                )
            }
            LiftAppIconButton(onClick = onAddSuperset, enabled = enabled) {
                Icon(
                    imageVector = LiftAppIcons.RefreshCcwDot,
                    contentDescription = stringResource(R.string.action_add_superset),
                )
            }
        }
    }
}

/** The gap the scaffold leaves under the floating action button. */
private val ScaffoldFabBottomSpacing = 16.dp

@MultiDevicePreview
@Composable
private fun RoutineScreenPreview() {
    PreviewTheme {
        RoutineScreen(
            loadableState =
                Loadable.Success(
                    ScreenState(
                        name = "Full Body",
                        items =
                            listOf(
                                RoutineItemWithExercises(
                                    id = 1,
                                    type = RoutineItemType.Exercise,
                                    exercises =
                                        listOf(
                                            RoutineExerciseItem(
                                                0L,
                                                "Bench Press",
                                                "Chest",
                                                ExerciseType.Weight,
                                                Goal.default,
                                            )
                                        ),
                                ),
                                RoutineItemWithExercises(
                                    id = 2,
                                    type = RoutineItemType.Exercise,
                                    exercises =
                                        listOf(
                                            RoutineExerciseItem(
                                                1L,
                                                "Squat",
                                                "Legs",
                                                ExerciseType.Weight,
                                                Goal.default,
                                            )
                                        ),
                                ),
                                RoutineItemWithExercises(
                                    id = 3,
                                    type = RoutineItemType.Exercise,
                                    exercises =
                                        listOf(
                                            RoutineExerciseItem(
                                                2L,
                                                "Deadlift",
                                                "Back",
                                                ExerciseType.Weight,
                                                Goal.default,
                                            )
                                        ),
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
