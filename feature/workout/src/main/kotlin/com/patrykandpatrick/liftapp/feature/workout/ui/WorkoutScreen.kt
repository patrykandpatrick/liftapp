package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.feature.workout.RestTimerService
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.GetEditableWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpdateWorkoutUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertExerciseSetUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.UpsertGoalSetsUseCase
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutPage
import com.patrykandpatrick.liftapp.feature.workout.model.getImageVector
import com.patrykandpatrick.liftapp.feature.workout.model.getText
import com.patrykandpatrick.liftapp.feature.workout.rememberRestTimerServiceController
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.theme.ButtonBorderShape
import com.patrykandpatrick.liftapp.ui.theme.ButtonShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.copy
import com.patrykandpatryk.liftapp.core.extension.getBottom
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.AppBars
import com.patrykandpatryk.liftapp.core.ui.Backdrop
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisTransition
import com.patrykandpatryk.liftapp.core.ui.rememberBackdropState
import com.patrykandpatryk.liftapp.core.ui.wheel.rememberWheelPickerState
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.time.LocalDateTime
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private const val TIMER_BOUND_ANIMATION_DURATION = 120
private const val TIMER_ENTER_ANIMATION_DURATION = 220
private const val TIMER_EXIT_ANIMATION_DURATION = 120
private const val TIMER_ANIMATION_SCALE = .92f

@Composable
fun WorkoutScreen(modifier: Modifier = Modifier, viewModel: WorkoutViewModel = hiltViewModel()) {
    val workout = viewModel.workout.collectAsStateWithLifecycle().value
    val restTimerService =
        rememberRestTimerServiceController().restTimerService.collectAsStateWithLifecycle(null)
    val selectedPage = viewModel.selectedPage.collectAsStateWithLifecycle().value

    RestTimerEffect(viewModel, restTimerService)

    LiftAppScaffold(
        topBar = {
            CompactTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.route_workout),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    AppBars.BackArrow(onClick = { viewModel.onAction(Action.PopBackStack) })
                },
            )
        },
        bottomBar = {
            workout?.run {
                pages[selectedPage].also { page -> BottomBar(page, viewModel::onAction) }
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        if (workout != null) {
            Content(
                workout = workout,
                page = selectedPage,
                setPage = viewModel::selectPage,
                restTimerService = restTimerService.value,
                onSaveSet = viewModel::saveSet,
                onAction = viewModel::onAction,
                modifier =
                    Modifier.padding(
                        paddingValues.copy(
                            bottom =
                                if (
                                    WindowInsets.ime.getBottom() >
                                        paddingValues.calculateBottomPadding()
                                ) {
                                    0.dp
                                } else {
                                    paddingValues.calculateBottomPadding()
                                }
                        )
                    ),
            )
        }
    }
}

@Composable
private fun Content(
    workout: EditableWorkout,
    page: Int,
    setPage: (Int) -> Unit,
    restTimerService: RestTimerService?,
    onSaveSet: (EditableWorkout.Exercise, EditableExerciseSet<ExerciseSet>, Int) -> Unit,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val wheelPickerState = rememberWheelPickerState(workout.firstIncompleteExerciseIndex)
    val backdropState = rememberBackdropState()

    LaunchedEffect(page) { launch { wheelPickerState.animateScrollTo(page) } }

    LaunchedEffect(wheelPickerState) {
        wheelPickerState.interactionSource.interactions
            .filter { it is DragInteraction.Stop || it is PressInteraction.Release }
            .collect { setPage(wheelPickerState.getTargetScrollItem()) }
    }

    Box(modifier = modifier.imePadding()) {
        Backdrop(
            backContent = { ExerciseListPicker(workout, wheelPickerState, backdropState) },
            backPeekHeight = { wheelPickerState.maxItemHeight.toDp() },
            contentPeekHeight = { 200.dp },
            state = backdropState,
            modifier = Modifier,
        ) {
            val outline = colorScheme.outline
            val shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
                modifier =
                    Modifier.background(
                            brush =
                                Brush.verticalGradient(
                                    listOf(colorScheme.surface, colorScheme.background)
                                ),
                            shape = shape,
                        )
                        .innerShadow(shape) {
                            spread = 0.5.dp.toPx()
                            color = outline
                            offset = Offset(0f, 1.dp.toPx())
                            blendMode = BlendMode.SrcOver
                        }
                        .padding(top = dimens.padding.contentVertical),
            ) {
                Spacer(
                    modifier =
                        Modifier.background(color = colorScheme.outline, shape = CircleShape)
                            .width(32.dp)
                            .height(6.dp)
                            .align(Alignment.CenterHorizontally)
                )

                AnimatedContent(
                    targetState = workout.pages[page],
                    transitionSpec = sharedXAxisTransition(),
                    contentKey = { it.index },
                    modifier = Modifier.fillMaxSize(),
                    label = "page",
                ) { page ->
                    when (page) {
                        is WorkoutPage.Exercise ->
                            Page(
                                exercise = page.exercise,
                                onAddSet = { onAction(Action.AddSet(page.exercise)) },
                                onRemoveSet = { onAction(Action.RemoveSet(page.exercise)) },
                                onSaveSet = onSaveSet,
                            )

                        is WorkoutPage.Summary -> Summary(page, onAction)
                    }
                }
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
    page: WorkoutPage,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = LocalDimens.current.padding
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier =
            modifier.background(colorScheme.background).fillMaxWidth().navigationBarsPadding(),
    ) {
        Column {
            LiftAppHorizontalDivider()

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(padding.itemHorizontalSmall, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth().padding(vertical = padding.itemVerticalSmall),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    LiftAppButton(
                        onClick = { onAction(page.secondaryAction) },
                        colors = LiftAppButtonDefaults.outlinedButtonColors,
                        shape =
                            ButtonShape.copy(
                                topEnd = CornerSize(6.dp),
                                bottomEnd = CornerSize(6.dp),
                            ),
                        borderShape =
                            ButtonBorderShape.copy(
                                topEnd = CornerSize(5.dp),
                                bottomEnd = CornerSize(5.dp),
                            ),
                    ) {
                        Icon(page.secondaryAction.getImageVector(), page.secondaryAction.getText())
                    }
                    LiftAppButton(
                        onClick = { onAction(page.primaryAction) },
                        colors = LiftAppButtonDefaults.outlinedButtonColors,
                        shape =
                            ButtonShape.copy(
                                topStart = CornerSize(6.dp),
                                bottomStart = CornerSize(6.dp),
                            ),
                        borderShape =
                            ButtonBorderShape.copy(
                                topStart = CornerSize(5.dp),
                                bottomStart = CornerSize(5.dp),
                            ),
                        modifier = Modifier,
                    ) {
                        Text(page.primaryAction.getText())
                        Icon(page.primaryAction.getImageVector(), page.primaryAction.getText())
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
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
    onSaveSet: (EditableWorkout.Exercise, EditableExerciseSet<ExerciseSet>, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = LocalDimens.current.padding.contentVertical)
    ) {
        val (selectedSet, onSelectSet) =
            remember(exercise.firstIncompleteOrLastSetIndex) {
                mutableIntStateOf(exercise.firstIncompleteOrLastSetIndex)
            }

        ExerciseSetStepper(
            sets = exercise.sets,
            selectedSet = selectedSet,
            onSelectSet = onSelectSet,
            onAddSet = onAddSet,
            onRemoveSet = onRemoveSet,
            contentPadding = PaddingValues(start = dimens.padding.contentHorizontal),
            modifier = Modifier.padding(end = dimens.padding.contentHorizontalSmall),
        )

        AnimatedContent(
            targetState = selectedSet,
            transitionSpec = sharedXAxisTransition(),
            modifier = Modifier,
            label = "set",
        ) { setIndex ->
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(LocalDimens.current.padding.itemVertical),
                modifier =
                    Modifier.fillMaxSize()
                        .padding(
                            horizontal = LocalDimens.current.padding.contentHorizontal,
                            vertical = LocalDimens.current.padding.itemVertical,
                        ),
            ) {
                val set = exercise.sets.getOrNull(setIndex) ?: return@Column

                SetEditorContent(set)

                LiftAppButton(
                    onClick = { onSaveSet(exercise, set, setIndex) },
                    enabled = set.isInputValid,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.action_save))
                }
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun WorkoutScreenPreview() {
    PreviewTheme {
        val savedStateHandle = remember { SavedStateHandle() }
        val textFieldStateManager =
            PreviewResource.textFieldStateManager(savedStateHandle = savedStateHandle)

        WorkoutScreen(
            viewModel =
                WorkoutViewModel(
                    getEditableWorkoutUseCase =
                        GetEditableWorkoutUseCase(
                            contract = { _, _ ->
                                flowOf(
                                    Workout(
                                        id = 1,
                                        routineID = 1,
                                        name = "Push",
                                        startDate = LocalDateTime.now(),
                                        endDate = null,
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
                            stringProvider = PreviewResource.stringProvider,
                            workoutRouteData = Routes.Workout.new(0L),
                            savedStateHandle = savedStateHandle,
                        ),
                    upsertGoalSets = UpsertGoalSetsUseCase(interfaceStub()),
                    upsertExerciseSet = UpsertExerciseSetUseCase(interfaceStub()),
                    updateWorkoutUseCase = UpdateWorkoutUseCase(interfaceStub()),
                    navigationCommander = NavigationCommander(),
                    coroutineScope = rememberCoroutineScope(),
                )
        )
    }
}
