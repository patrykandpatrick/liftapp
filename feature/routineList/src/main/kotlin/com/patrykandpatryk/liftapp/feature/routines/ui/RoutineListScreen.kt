package com.patrykandpatryk.liftapp.feature.routines.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppFAB
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.routine.RoutineCard
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.feature.routines.model.Action
import com.patrykandpatryk.liftapp.feature.routines.model.RoutineItem

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
    val dimensPadding = LocalDimens.current.padding

    LiftAppScaffold(
        modifier = modifier.fillMaxHeight(),
        floatingActionButton = {
            LiftAppFAB(
                content = {
                    Icon(painterResource(id = R.drawable.ic_add), null)
                    Text(stringResource(id = R.string.action_new_routine))
                },
                onClick = { onAction(Action.AddNewRoutine) },
            )
        },
        topBar = {
            loadableState.Unfold { state ->
                CompactTopAppBar(
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
                                    painter = painterResource(id = R.drawable.ic_close),
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
            LazyVerticalStaggeredGrid(
                modifier = Modifier.padding(internalPadding),
                columns =
                    StaggeredGridCells.Adaptive(minSize = LocalDimens.current.routine.minCardWidth),
                contentPadding =
                    PaddingValues(
                        horizontal = dimensPadding.contentHorizontalSmall,
                        vertical = dimensPadding.contentVertical,
                    ),
                verticalItemSpacing = dimensPadding.itemVerticalSmall,
                horizontalArrangement = Arrangement.spacedBy(dimensPadding.itemHorizontalSmall),
            ) {
                items(items = state, key = { it.id }) { routine ->
                    LiftAppCard(onClick = { onAction(Action.RoutineClicked(routine.id)) }) {
                        RoutineCard(routineName = routine.name, exerciseNames = routine.exercises)
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
