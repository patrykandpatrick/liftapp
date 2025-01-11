package com.patrykandpatrick.liftapp.feature.workout.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.feature.workout.RestTimerService
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.GetEditableWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertExerciseSetUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertGoalSetsUseCase
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutNavigator
import com.patrykandpatrick.liftapp.feature.workout.navigation.WorkoutRouteData
import com.patrykandpatrick.liftapp.feature.workout.rememberRestTimerServiceController
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.graphics.rememberTopSinShape
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.ui.AppBars
import com.patrykandpatryk.liftapp.core.ui.Backdrop
import com.patrykandpatryk.liftapp.core.ui.SinHorizontalDivider
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisEnterTransition
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisExitTransition
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.rememberBackdropState
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.wheel.rememberWheelPickerState
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val TIMER_BOUND_ANIMATION_DURATION = 120
private const val TIMER_ENTER_ANIMATION_DURATION = 220
private const val TIMER_EXIT_ANIMATION_DURATION = 120
private const val TIMER_ANIMATION_SCALE = .92f

@Composable
fun WorkoutScreen(
    navigator: WorkoutNavigator,
    modifier: Modifier = Modifier,
    viewModel: WorkoutViewModel = hiltViewModel(),
) {
    val workout = viewModel.workout.collectAsStateWithLifecycle().value
    val restTimerService =
        rememberRestTimerServiceController().restTimerService.collectAsStateWithLifecycle(null)
    val page = viewModel.selectedPage.collectAsStateWithLifecycle().value

    RestTimerEffect(viewModel, restTimerService)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = workout?.name.orEmpty()) },
                navigationIcon = { AppBars.BackArrow(onClick = navigator::back) },
            )
        },
        bottomBar = {
            BottomBar(
                onNextPageClick = { viewModel.onPageDelta(1) },
                onPreviousPageClick = { viewModel.onPageDelta(-1) },
            )
        },
        modifier = modifier,
    ) { paddingValues ->
        if (workout != null) {
            Content(
                workout = workout,
                page = page,
                restTimerService = restTimerService.value,
                onAddSetClick = viewModel::increaseSetCount,
                onRemoveSetClick = viewModel::decreaseSetCount,
                onSaveSet = viewModel::saveSet,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun Content(
    workout: EditableWorkout,
    page: Int,
    restTimerService: RestTimerService?,
    onAddSetClick: (EditableWorkout.Exercise) -> Unit,
    onRemoveSetClick: (EditableWorkout.Exercise) -> Unit,
    onSaveSet: (EditableWorkout.Exercise, EditableExerciseSet, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val wheelPickerState = rememberWheelPickerState(workout.firstIncompleteExerciseIndex)
    val backdropState = rememberBackdropState()
    LaunchedEffect(page) { launch { wheelPickerState.animateScrollTo(page) } }
    Box(modifier = modifier) {
        Backdrop(
            backContent = { ExerciseListPicker(workout, wheelPickerState, backdropState) },
            backPeekHeight = with(LocalDensity.current) { wheelPickerState.maxItemHeight.toDp() },
            contentPeekHeight = 200.dp,
            state = backdropState,
            modifier = Modifier.imePadding(),
        ) {
            Box(contentAlignment = Alignment.TopCenter) {
                AnimatedContent(
                    targetState = page.coerceIn(0, workout.exercises.lastIndex),
                    transitionSpec = {
                        val forward = targetState > initialState
                        sharedXAxisEnterTransition(forward) togetherWith
                            sharedXAxisExitTransition(forward)
                    },
                    modifier =
                        Modifier.fillMaxSize()
                            .clip(rememberTopSinShape())
                            .background(MaterialTheme.colorScheme.background),
                ) { page ->
                    Page(
                        exercise = workout.exercises[page],
                        onAddSetClick = onAddSetClick,
                        onRemoveSetClick = onRemoveSetClick,
                        onSaveSet = onSaveSet,
                    )
                }

                SinHorizontalDivider()
            }
        }

        restTimerService?.also { RestTimerContainer(it, Modifier.align(Alignment.BottomCenter)) }
    }
}

@Composable
private fun RestTimerEffect(
    viewModel: WorkoutViewModel,
    restTimerService: State<RestTimerService?>,
) {
    LaunchedEffect(viewModel, restTimerService) {
        viewModel.workout
            .filterNotNull()
            .distinctUntilChanged { old, new -> new.completedSetCount <= old.completedSetCount }
            .drop(1)
            .collect { workout ->
                if (workout.nextExerciseSet != null) {
                    restTimerService.value?.startTimer(workout.nextExerciseSet.restTime, workout.id)
                }
            }
    }
}

@Composable
private fun BottomBar(
    onPreviousPageClick: () -> Unit,
    onNextPageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = LocalDimens.current.padding
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
                .navigationBarsPadding(),
    ) {
        Column {
            HorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(padding.itemHorizontalSmall, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth().padding(vertical = padding.itemVertical),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedButton(
                        onClick = onPreviousPageClick,
                        shape =
                            MaterialTheme.shapes.small.copy(
                                topEnd = CornerSize(4.dp),
                                bottomEnd = CornerSize(4.dp),
                            ),
                    ) {
                        Icon(
                            painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = null,
                        )
                    }
                    Button(
                        onClick = onNextPageClick,
                        shape =
                            MaterialTheme.shapes.small.copy(
                                topStart = CornerSize(4.dp),
                                bottomStart = CornerSize(4.dp),
                            ),
                    ) {
                        Text(text = stringResource(R.string.action_next_exercise))
                        Spacer(modifier = Modifier.width(padding.itemHorizontalSmall))
                        Icon(
                            painterResource(id = R.drawable.ic_arrow_forward),
                            contentDescription = stringResource(R.string.action_previous_exercise),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RestTimerContainer(restTimerService: RestTimerService, modifier: Modifier = Modifier) {
    val timer = restTimerService.timer.collectAsStateWithLifecycle(null).value

    AnimatedContent(
        targetState = timer,
        modifier = modifier.fillMaxWidth(),
        transitionSpec = {
            (fadeIn(tween(TIMER_ENTER_ANIMATION_DURATION, TIMER_BOUND_ANIMATION_DURATION)) +
                    scaleIn(
                        tween(TIMER_ENTER_ANIMATION_DURATION, TIMER_BOUND_ANIMATION_DURATION),
                        TIMER_ANIMATION_SCALE,
                    ))
                .togetherWith(
                    fadeOut(tween(TIMER_EXIT_ANIMATION_DURATION)) +
                        scaleOut(tween(TIMER_EXIT_ANIMATION_DURATION), TIMER_ANIMATION_SCALE)
                )
                .using(SizeTransform(false) { _, _ -> tween(TIMER_BOUND_ANIMATION_DURATION) })
        },
        contentAlignment = Alignment.Center,
        label = "Rest timer",
        contentKey = { it?.isFinished },
    ) { state ->
        if (state != null && !state.isFinished) {
            RestTimer(
                remainingDuration = state.remainingDuration,
                isPaused = state.isPaused,
                onToggleIsPaused = restTimerService::toggleTimer,
                onCancel = restTimerService::cancelTimer,
                modifier =
                    Modifier.padding(
                        LocalDimens.current.padding.itemHorizontal,
                        LocalDimens.current.padding.itemVertical,
                    ),
            )
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
            exerciseType = exercise.exerciseType,
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

@SuppressLint("ViewModelConstructorInComposable")
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
                                                    goal = Workout.Goal.default,
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
                                                    goal = Workout.Goal.default,
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
                    upsertGoalSets = UpsertGoalSetsUseCase(interfaceStub()),
                    upsertExerciseSet = UpsertExerciseSetUseCase(interfaceStub()),
                    coroutineScope = rememberCoroutineScope(),
                ),
        )
    }
}
