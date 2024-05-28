package com.patrykandpatryk.liftapp.feature.routines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf

@Composable
fun RoutineListScreen(
    navigator: RoutineListNavigator,
    modifier: Modifier = Modifier,
    padding: PaddingValues,
) {
    val viewModel: RoutineListViewModel = hiltViewModel()

    RoutineListScreen(
        navigator = navigator,
        state = viewModel,
        modifier = modifier,
        padding = padding,
    )
}

@Composable
private fun RoutineListScreen(
    navigator: RoutineListNavigator,
    state: RoutineListState,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val dimensPadding = LocalDimens.current.padding
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val routines by state.routines.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier
            .fillMaxHeight()
            .padding(bottom = padding.calculateBottomPadding()),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = stringResource(id = R.string.action_new_routine),
                icon = painterResource(id = R.drawable.ic_add),
                onClick = { navigator.newRoutine() },
            )
        },
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_routines),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets.statusBars,
    ) { internalPadding ->
        LazyVerticalStaggeredGrid(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .padding(internalPadding),
            columns = StaggeredGridCells.Adaptive(minSize = LocalDimens.current.routine.minCardWidth),
            contentPadding = PaddingValues(
                horizontal = dimensPadding.contentHorizontalSmall,
                vertical = dimensPadding.contentVertical,
            ),
            verticalItemSpacing = dimensPadding.contentVerticalSmall,
            horizontalArrangement = Arrangement.spacedBy(dimensPadding.contentHorizontalSmall),
        ) {
            items(
                items = routines,
                key = { it.id },
            ) { routine ->
                RoutineCard(
                    title = routine.name,
                    exercises = routine.exercises,
                    onClick = { navigator.routine(routine.id) },
                )
            }
        }
    }
}

@MultiDevicePreview
@Composable
fun RoutinesPreview() {
    LiftAppTheme {
        RoutineListScreen(
            navigator = interfaceStub(),
            padding = PaddingValues(),
            state = RoutineListViewModel(
                RoutineListDataSource(
                    flowOf(
                        persistentListOf(
                            RoutineItem(
                                id = 0L,
                                name = "Routine I",
                                exercises = persistentListOf("First Exercise", "Second Exercise", "Third Exercise", "Fourth Exercise"),
                            ),
                            RoutineItem(
                                id = 1L,
                                name = "Routine II",
                                exercises = persistentListOf("First Exercise", "Second Exercise", "Third Exercise", "Fourth Exercise"),
                            ),
                            RoutineItem(
                                id = 2L,
                                name = "Routine III",
                                exercises = persistentListOf("First Exercise", "Second Exercise", "Third Exercise", "Fourth Exercise"),
                            ),
                            RoutineItem(
                                id = 3L,
                                name = "Routine IV",
                                exercises = persistentListOf("First Exercise", "Second Exercise", "Third Exercise"),
                            ),
                        )
                    )
                )
            ),
        )
    }
}
