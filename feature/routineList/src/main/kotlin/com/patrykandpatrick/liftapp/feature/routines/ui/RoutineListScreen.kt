package com.patrykandpatrick.liftapp.feature.routines.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.Icon
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.increaseBy
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.routine.RoutineCard
import com.patrykandpatrick.liftapp.domain.extension.moved
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.feature.routines.model.Action
import com.patrykandpatrick.liftapp.feature.routines.model.RoutineItem
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.icons.Routine
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyStaggeredGridState

@Composable
fun RoutineListScreen(
    modifier: Modifier = Modifier,
    viewModel: RoutineListViewModel = hiltViewModel(),
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    RoutineListScreen(loadableState = state, onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun RoutineListScreen(
    loadableState: Loadable<RoutineListState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val dimens = LocalDimens.current
    val cardGutter = 8.dp
    val fabHeight = 24.dp + dimens.fab.verticalPadding * 2
    // The scaffold leaves 16 dp below the FAB; mirror that gap above it.
    val bottomContentPadding = fabHeight + dimens.screen.padding * 2

    LiftAppScaffold(
        modifier =
            modifier.fillMaxHeight().nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        floatingActionButton = {
            LiftAppFAB(
                content = {
                    Icon(LiftAppIcons.Plus, null)
                    Text(stringResource(id = R.string.action_new_routine))
                },
                onClick = { onAction(Action.AddNewRoutine) },
            )
        },
        topBar = {
            loadableState.Unfold { state ->
                CompactTopAppBar(
                    scrollBehavior = topAppBarScrollBehavior,
                    title = {
                        if (state.isPickingRoutine) {
                            Text(stringResource(id = R.string.route_pick_routine))
                        } else {
                            Text(stringResource(id = R.string.route_routines))
                        }
                    },
                    navigationIcon = {
                        LiftAppIconButton(onClick = { onAction(Action.PopBackStack) }) {
                            if (state.isPickingRoutine) {
                                Icon(
                                    imageVector = LiftAppIcons.Cross,
                                    contentDescription = stringResource(id = R.string.action_close),
                                )
                            } else {
                                Icon(
                                    imageVector = LiftAppIcons.ArrowBack,
                                    contentDescription = stringResource(id = R.string.action_back),
                                )
                            }
                        }
                    },
                )
            }
        },
        contentWindowInsets = WindowInsets.systemBars,
    ) { internalPadding ->
        loadableState.Unfold { state ->
            if (state.routines.isEmpty()) {
                EmptyState(
                    icon = LiftAppIcons.Routine,
                    message = stringResource(R.string.state_no_routines),
                    modifier =
                        Modifier.fillMaxSize()
                            .padding(internalPadding)
                            .padding(
                                start = dimens.screen.padding,
                                top = dimens.screen.padding,
                                end = dimens.screen.padding,
                                bottom = dimens.screen.padding,
                            ),
                )
            } else {
                var routines by remember(state.routines) { mutableStateOf(state.routines) }
                var orderBeforeDrag by remember { mutableStateOf(emptyList<Long>()) }
                val lazyGridState = rememberLazyStaggeredGridState()
                val reorderableState =
                    rememberReorderableLazyStaggeredGridState(lazyGridState) { from, to ->
                        routines = routines.moved(from.index, to.index)
                    }

                LazyVerticalStaggeredGrid(
                    state = lazyGridState,
                    modifier = Modifier.fillMaxSize(),
                    columns = StaggeredGridCells.Adaptive(minSize = dimens.routine.minCardWidth),
                    contentPadding =
                        internalPadding.increaseBy(
                            start = dimens.screen.padding - cardGutter,
                            top = cardGutter,
                            end = dimens.screen.padding - cardGutter,
                            bottom = bottomContentPadding,
                        ),
                    verticalItemSpacing = cardGutter,
                    horizontalArrangement = Arrangement.spacedBy(cardGutter),
                ) {
                    items(items = routines, key = { it.id }) { routine ->
                        ReorderableItem(
                            state = reorderableState,
                            key = routine.id,
                            enabled = !state.isPickingRoutine,
                        ) {
                            val interactionSource = remember { MutableInteractionSource() }
                            LiftAppCard(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .longPressDraggableHandle(
                                            enabled = !state.isPickingRoutine,
                                            interactionSource = interactionSource,
                                            onDragStarted = {
                                                orderBeforeDrag = routines.map(RoutineItem::id)
                                            },
                                            onDragStopped = {
                                                val routineIDs = routines.map(RoutineItem::id)
                                                if (routineIDs != orderBeforeDrag) {
                                                    onAction(Action.ReorderRoutines(routineIDs))
                                                }
                                            },
                                        ),
                                onClick = { onAction(Action.RoutineClicked(routine.id)) },
                                interactionSource = interactionSource,
                            ) {
                                RoutineCard(
                                    routineName = routine.name,
                                    exerciseNames = routine.exercises,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineListPreview(isPickingRoutine: Boolean) {
    LiftAppTheme {
        RoutineListScreen(
            loadableState =
                Loadable.Success(
                    RoutineListState(
                        routines = PreviewRoutines,
                        isPickingRoutine = isPickingRoutine,
                    )
                ),
            onAction = {},
        )
    }
}

@MultiDevicePreview
@Composable
private fun RoutineListNormalPreview() {
    RoutineListPreview(isPickingRoutine = false)
}

@MultiDevicePreview
@Composable
private fun RoutineListPickingRoutinePreview() {
    RoutineListPreview(isPickingRoutine = true)
}

private val PreviewRoutines =
    listOf(
        RoutineItem(
            id = 0L,
            name = "Routine I",
            exercises =
                listOf("First Exercise", "Second Exercise", "Third Exercise", "Fourth Exercise"),
        ),
        RoutineItem(
            id = 1L,
            name = "Routine II",
            exercises =
                listOf("First Exercise", "Second Exercise", "Third Exercise", "Fourth Exercise"),
        ),
        RoutineItem(
            id = 2L,
            name = "Routine III",
            exercises =
                listOf("First Exercise", "Second Exercise", "Third Exercise", "Fourth Exercise"),
        ),
        RoutineItem(
            id = 3L,
            name = "Routine IV",
            exercises = listOf("First Exercise", "Second Exercise", "Third Exercise"),
        ),
    )
