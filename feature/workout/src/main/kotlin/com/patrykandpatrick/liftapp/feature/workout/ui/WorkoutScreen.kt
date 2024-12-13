package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.GetEditableWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertExerciseSetUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertGoalSetsUseCase
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutNavigator
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutRouteData
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.graphics.rememberTopSinShape
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.ui.AppBars
import com.patrykandpatryk.liftapp.core.ui.Backdrop
import com.patrykandpatryk.liftapp.core.ui.SinHorizontalDivider
import com.patrykandpatryk.liftapp.core.ui.rememberBackdropState
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.wheel.ScrollSyncEffect
import com.patrykandpatryk.liftapp.core.ui.wheel.rememberWheelPickerState
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun WorkoutScreen(
    navigator: WorkoutNavigator,
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel = hiltViewModel(),
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val workout = viewModel.workout.collectAsStateWithLifecycle().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = workout?.name.orEmpty()) },
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = { AppBars.BackArrow(onClick = navigator::back) },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        if (workout != null) {
            val pagerState =
                rememberPagerState(initialPage = workout.firstIncompleteExerciseIndex) {
                    workout.exercises.size
                }
            val wheelPickerState =
                rememberWheelPickerState(
                    initialSelectedIndex = workout.firstIncompleteExerciseIndex
                )
            val backdropState = rememberBackdropState()
            Backdrop(
                backContent = { ExerciseListPicker(workout, wheelPickerState, backdropState) },
                backPeekHeight =
                    with(LocalDensity.current) { wheelPickerState.maxItemHeight.toDp() },
                contentPeekHeight = 200.dp,
                state = backdropState,
                modifier = Modifier.padding(paddingValues).imePadding(),
            ) {
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier.nestedScroll(backdropState.nestedScrollConnection),
                ) {
                    ScrollSyncEffect(wheelPickerState, pagerState)

                    LaunchedEffect(workout.firstIncompleteExerciseIndex) {
                        delay(Constants.Workout.EXERCISE_CHANGE_DELAY)
                        launch {
                            pagerState.animateScrollToPage(workout.firstIncompleteExerciseIndex)
                        }
                        launch {
                            wheelPickerState.animateScrollTo(workout.firstIncompleteExerciseIndex)
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        key = { workout.exercises[it].id },
                        modifier =
                            Modifier.fillMaxSize()
                                .clip(rememberTopSinShape())
                                .background(MaterialTheme.colorScheme.background),
                    ) { page ->
                        Page(
                            exercise = workout.exercises[page],
                            onAddSetClick = viewModel::increaseSetCount,
                            onRemoveSetClick = viewModel::decreaseSetCount,
                            onSaveSet = viewModel::saveSet,
                        )
                    }

                    SinHorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun Page(
    exercise: EditableWorkout.Exercise,
    onAddSetClick: (EditableWorkout.Exercise) -> Unit,
    onRemoveSetClick: (EditableWorkout.Exercise) -> Unit,
    onSaveSet: (EditableWorkout.Exercise, EditableExerciseSet, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        GoalHeader(
            goal = exercise.goal,
            onAddSetClick = { onAddSetClick(exercise) },
            onRemoveSetClick = { onRemoveSetClick(exercise) },
        )

        exercise.sets.forEachIndexed { index, set ->
            SetItem(
                setIndex = index,
                set = set,
                isActive = exercise.isSetActive(set),
                enabled = exercise.isSetEnabled(set),
                onSave = { onSaveSet(exercise, set, index) },
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun WorkoutScreenPreview() {
    LiftAppTheme {
        val savedStateHandle = remember { SavedStateHandle() }
        val textFieldStateManager =
            PreviewResource.textFieldStateManager(savedStateHandle = savedStateHandle)

        WorkoutScreen(
            navigator = interfaceStub(),
            viewModel =
                WorkoutViewModel(
                    getEditableWorkoutUseCase =
                        GetEditableWorkoutUseCase(
                            contract = { _, _ ->
                                flowOf(
                                    Workout(
                                        id = 1,
                                        name = "Push",
                                        date = LocalDateTime.now(),
                                        duration = 45.minutes,
                                        notes = "",
                                        exercises =
                                            listOf(
                                                Workout.Exercise(
                                                    id = 1,
                                                    name = Name.Raw("Bench Press"),
                                                    exerciseType = ExerciseType.Weight,
                                                    mainMuscles = listOf(Muscle.Chest),
                                                    secondaryMuscles = listOf(Muscle.Triceps),
                                                    tertiaryMuscles = emptyList(),
                                                    goal = Goal.Default,
                                                    sets =
                                                        listOf(
                                                            ExerciseSet.Weight(
                                                                100.0,
                                                                10,
                                                                MassUnit.Kilograms,
                                                            ),
                                                            ExerciseSet.Weight(
                                                                0.0,
                                                                0,
                                                                MassUnit.Kilograms,
                                                            ),
                                                            ExerciseSet.Weight(
                                                                0.0,
                                                                0,
                                                                MassUnit.Kilograms,
                                                            ),
                                                        ),
                                                ),
                                                Workout.Exercise(
                                                    id = 2,
                                                    name = Name.Raw("Squat"),
                                                    exerciseType = ExerciseType.Weight,
                                                    mainMuscles = listOf(Muscle.Quadriceps),
                                                    secondaryMuscles = listOf(Muscle.Glutes),
                                                    tertiaryMuscles = emptyList(),
                                                    goal = Goal.Default,
                                                    sets =
                                                        listOf(
                                                            ExerciseSet.Weight(
                                                                0.0,
                                                                0,
                                                                MassUnit.Kilograms,
                                                            ),
                                                            ExerciseSet.Weight(
                                                                0.0,
                                                                0,
                                                                MassUnit.Kilograms,
                                                            ),
                                                            ExerciseSet.Weight(
                                                                0.0,
                                                                0,
                                                                MassUnit.Kilograms,
                                                            ),
                                                        ),
                                                ),
                                            ),
                                    )
                                )
                            },
                            textFieldStateManager = textFieldStateManager,
                            formatter = PreviewResource.formatter(),
                            workoutRouteData = WorkoutRouteData(),
                            savedStateHandle = savedStateHandle,
                        ),
                    upsertGoalSets = UpsertGoalSetsUseCase { _, _, _, _, _, _ -> },
                    upsertExerciseSet = UpsertExerciseSetUseCase { _, _, _, _ -> },
                    coroutineScope = rememberCoroutineScope(),
                ),
        )
    }
}
