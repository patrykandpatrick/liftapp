package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutNavigator
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.model.getPrettyStringLong
import com.patrykandpatryk.liftapp.core.model.getPrettyStringShort
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.AppBars
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.VerticalDivider
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.theme.PillShape
import com.patrykandpatryk.liftapp.domain.goal.Goal
import kotlin.time.Duration.Companion.seconds

@Composable
fun WorkoutScreen(
    routineID: Long,
    workoutID: Long,
    navigator: WorkoutNavigator,
) {
    val viewModel: WorkoutViewModel = hiltViewModel(
        creationCallback = { factory: WorkoutViewModel.Factory -> factory.create(routineID, workoutID) },
    )

    WorkoutScreen(viewModel.workoutState, navigator::back)
}

@Composable
private fun WorkoutScreen(
    workoutState: WorkoutState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val workout = workoutState.workout.collectAsStateWithLifecycle().value

    BottomSheetScaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = workout?.name.orEmpty()) },
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = { AppBars.BackArrow(onClick = onBackClick) }
            )
        },
        sheetContent = {
            if (workout != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(workout.exercises, key = { it.id }) { exercise ->
                        ListItem(
                            title = { Text(text = exercise.name.getDisplayName()) },
                            description = { Text(text = exercise.goal.getPrettyStringShort()) }
                        )
                    }
                }
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        val pagerState = rememberPagerState { workout?.exercises?.size ?: 0 }
        if (workout != null) {
            HorizontalPager(
                state = pagerState,
                key = { workout.exercises[it].id },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) { page ->
                LazyColumn(
                    modifier = Modifier
                        .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                        .fillMaxSize()
                ) {
                    val exercise = workout.exercises[page]
                    stickyHeader(key = "header") {
                        Text(
                            text = exercise.name.getDisplayName(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = modifier
                                .padding(
                                    horizontal = LocalDimens.current.padding.contentHorizontal,
                                    vertical = LocalDimens.current.padding.itemVertical,
                                ),
                        )
                    }

                    item(key = "spacer") { Spacer(modifier = Modifier.height(LocalDimens.current.verticalItemSpacing)) }

                    item(key = "set_count") {
                        GoalHeader(
                            goal = exercise.goal,
                            onAddSetClick = { workoutState.increaseSetCount(exercise) },
                            onRemoveSetClick = { workoutState.decreaseSetCount(exercise) },
                        )
                    }

                    items(exercise.goal.sets, key = { it }) { set ->
                        SetItem(setIndex = set, setItemState = SetItemState.NotStarted)
                    }
                }

            }
        }
    }
}


