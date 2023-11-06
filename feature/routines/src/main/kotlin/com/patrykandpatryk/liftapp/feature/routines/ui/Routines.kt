package com.patrykandpatryk.liftapp.feature.routines.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.calculateEndPadding
import com.patrykandpatryk.liftapp.core.extension.calculateStartPadding
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.ExtendedFloatingActionButton
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExerciseNames

@Suppress("UnusedPrivateMember")
@Composable
fun Routines(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    navigate: (String) -> Unit,
) {
    val viewModel: RoutinesViewModel = hiltViewModel()
    val routines by viewModel.routines.collectAsState()

    Routines(
        modifier = modifier,
        padding = padding,
        routines = routines,
        navigate = navigate,
    )
}

@Suppress("UnusedPrivateMember")
@Composable
private fun Routines(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    routines: List<RoutineWithExerciseNames>,
    navigate: (String) -> Unit,
) {
    val dimensPadding = LocalDimens.current.padding
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxHeight()
            .padding(bottom = padding.calculateBottomPadding()),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = stringResource(id = R.string.action_new_routine),
                icon = painterResource(id = R.drawable.ic_add),
                onClick = { navigate(Routes.NewRoutine.create()) },
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
                .padding(
                    top = internalPadding.calculateTopPadding(),
                    start = internalPadding.calculateStartPadding(),
                    end = internalPadding.calculateEndPadding(),
                ),
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
                    onClick = { navigate(Routes.Routine.create(routine.id)) },
                )
            }

            // Temporary workaround for bug with StaggeredGrid cutting-off bottom-most items.
            items(count = 2) {
                Spacer(
                    modifier = Modifier.padding(padding.calculateBottomPadding()),
                )
            }
        }
    }
}

@MultiDevicePreview
@Composable
fun RoutinesPreview() {
    LiftAppTheme {
        Routines(
            padding = PaddingValues(bottom = 56.dp),
            routines = listOf(
                RoutineWithExerciseNames(
                    id = 0L,
                    name = "Routine I",
                    exercises = listOf("First Exercise", "Second Exercise", "Third Exercise"),
                ),
                RoutineWithExerciseNames(
                    id = 1L,
                    name = "Routine II",
                    exercises = listOf("First Exercise", "Second Exercise", "Third Exercise", "Fourth Exercise"),
                ),
                RoutineWithExerciseNames(
                    id = 2L,
                    name = "Routine III",
                    exercises = listOf("First Exercise", "Second Exercise", "Third Exercise", "Fourth Exercise"),
                ),
                RoutineWithExerciseNames(
                    id = 3L,
                    name = "Routine IV",
                    exercises = listOf("First Exercise", "Second Exercise", "Third Exercise"),
                ),
            ),
            navigate = {},
        )
    }
}
