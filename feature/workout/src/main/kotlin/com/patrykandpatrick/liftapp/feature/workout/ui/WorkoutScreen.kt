package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.feature.workout.RestTimerService
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutPage
import com.patrykandpatrick.liftapp.feature.workout.model.getImageVector
import com.patrykandpatrick.liftapp.feature.workout.model.getText
import com.patrykandpatrick.liftapp.feature.workout.rememberRestTimerServiceController
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.windowInsetsControllerCompat
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.modifier.topTintedEdge
import com.patrykandpatrick.liftapp.ui.theme.BottomSheetShape
import com.patrykandpatrick.liftapp.ui.theme.ButtonBorderShape
import com.patrykandpatrick.liftapp.ui.theme.ButtonShape
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.Typography
import com.patrykandpatrick.liftapp.ui.theme.bottomSheetShadow
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.copy
import com.patrykandpatryk.liftapp.core.extension.getBottom
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.AppBars
import com.patrykandpatryk.liftapp.core.ui.Backdrop
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisTransition
import com.patrykandpatryk.liftapp.core.ui.rememberBackdropState
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import com.swmansion.kmpwheelpicker.rememberWheelPickerState
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
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

    WorkoutScreen(workout, restTimerService, selectedPage, viewModel::onAction, modifier)
}

@Composable
fun WorkoutScreen(
    workout: EditableWorkout?,
    restTimerService: State<RestTimerService?>,
    selectedPage: Int,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    SetStatusAppearance()

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
                navigationIcon = { AppBars.BackArrow(onClick = { onAction(Action.PopBackStack) }) },
                colors =
                    AppBars.colors(
                        containerColor = colorScheme.bottomSheetScrim,
                        contentColor = colorScheme.onBottomSheetScrim,
                    ),
            )
        },
        bottomBar = {
            workout?.run { pages[selectedPage].also { page -> BottomBar(page, onAction) } }
        },
        modifier = modifier,
        containerColor = colorScheme.bottomSheetScrim,
        contentWindowInsets = WindowInsets.statusBars,
    ) { paddingValues ->
        if (workout != null) {
            Content(
                workout = workout,
                page = selectedPage,
                setPage = { onAction(Action.SelectPage(it)) },
                restTimerService = restTimerService.value,
                onSaveSet = { exerciseIndex, setIndex ->
                    onAction(
                        Action.SaveSet(workout, workout.iterator.getItem(exerciseIndex, setIndex))
                    )
                },
                onAction = onAction,
                modifier =
                    Modifier.padding(paddingValues.copy(bottom = WindowInsets.ime.getBottom())),
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
    onSaveSet: (Int, Int) -> Unit,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val wheelPickerState =
        rememberWheelPickerState(
            itemCount = workout.exercises.size + 1,
            initialIndex = workout.startPageIndex,
        )
    val backdropState = rememberBackdropState()

    LaunchedEffect(page) { launch { wheelPickerState.animateScrollTo(page) } }

    LaunchedEffect(wheelPickerState.index) { setPage(wheelPickerState.index) }

    Box(modifier = modifier) {
        Backdrop(
            backContent = {
                LiftAppTheme(darkTheme = true) {
                    ExerciseListPicker(workout, wheelPickerState, backdropState)
                }
            },
            backPeekHeight = { wheelPickerState.slotHeight.toDp() },
            contentPeekHeight = { 200.dp },
            state = backdropState,
            modifier = Modifier,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
                modifier =
                    Modifier.bottomSheetShadow()
                        .background(color = colorScheme.surface, shape = BottomSheetShape)
                        .topTintedEdge(BottomSheetShape)
                        .padding(top = dimens.padding.contentVertical),
            ) {
                val coroutineScope = rememberCoroutineScope()
                Spacer(
                    modifier =
                        Modifier.clip(CircleShape)
                            .clickable { coroutineScope.launch { backdropState.toggle() } }
                            .background(color = colorScheme.outline)
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
                                exerciseIndex = page.index,
                                exercise = page.exercise,
                                onSaveSet = onSaveSet,
                                onAction = onAction,
                            )

                        is WorkoutPage.Summary -> Summary(page, onAction)
                    }
                }
            }
        }

        restTimerService?.also { RestTimerContainer(it, Modifier.align(Alignment.BottomCenter)) }
    }
}

private fun LazyListScope.setCountButtons(
    exercise: EditableWorkout.Exercise,
    showDivider: Boolean,
    onAction: (Action) -> Unit,
) {
    item(key = "add_set_${exercise.id}") {
        LiftAppHorizontalDivider(Modifier.animateItem())

        Row(
            modifier =
                Modifier.animateItem()
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimens.padding.contentHorizontal,
                        vertical = dimens.padding.itemVertical,
                    ),
            horizontalArrangement =
                Arrangement.spacedBy(dimens.padding.itemHorizontal, Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiftAppText(
                text = stringResource(R.string.workout_change_set_count_buttons_title),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = dimens.padding.itemHorizontalSmall),
            )
            LiftAppButton(
                onClick = { onAction(Action.RemoveSet(exercise)) },
                colors = LiftAppButtonDefaults.outlinedButtonColors,
                modifier = Modifier,
            ) {
                LiftAppText(text = "-1", style = Typography.bodySmallMono)
            }

            LiftAppButton(
                onClick = { onAction(Action.AddSet(exercise)) },
                colors = LiftAppButtonDefaults.outlinedButtonColors,
                modifier = Modifier,
            ) {
                LiftAppText(text = "+1", style = Typography.bodySmallMono)
            }
        }

        if (showDivider) {
            LiftAppHorizontalDivider(
                Modifier.animateItem().padding(vertical = dimens.padding.itemVerticalSmall)
            )
        }
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
                if (workout.nextIncompleteItem != null) {
                    restTimerService.value?.startTimer(
                        workout.nextIncompleteItem.restTime,
                        workout.id,
                    )
                }
            }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun BottomBar(
    page: WorkoutPage,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val padding = dimens.padding
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.background(colorScheme.surface).fillMaxWidth().navigationBarsPadding(),
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
                    LookaheadScope {
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
                            modifier = Modifier.animateBounds(this),
                        ) {
                            AnimatedContent(
                                targetState = page.primaryAction.getText(),
                                contentAlignment = Alignment.Center,
                                transitionSpec = { elementTransitionSpec },
                            ) { text ->
                                Text(text)
                            }
                            AnimatedContent(
                                targetState = page.primaryAction.getImageVector(),
                                contentAlignment = Alignment.Center,
                                transitionSpec = { elementTransitionSpec },
                            ) { imageVector ->
                                Icon(imageVector, page.primaryAction.getText())
                            }
                        }
                    }
                }
            }
        }
    }
}

val elementTransitionSpec =
    (fadeIn(tween(300, 100)) + scaleIn(tween(300, 100))).togetherWith(fadeOut(tween(150)))

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
                onUpdateTimerBy = restTimerService::updateTimerBy,
                onCancel = restTimerService::cancelTimer,
                modifier =
                    Modifier.padding(dimens.padding.itemHorizontal, dimens.padding.itemVertical),
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Page(
    exerciseIndex: Int,
    exercise: EditableWorkout.Exercise,
    onSaveSet: (Int, Int) -> Unit,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isKeyboardVisible = WindowInsets.isImeVisible
    val focusManager = LocalFocusManager.current
    val (selectedSet, onSelectSet) =
        remember(exercise.sets) { mutableIntStateOf(exercise.firstIncompleteSetIndex) }

    LaunchedEffect(isKeyboardVisible, focusManager) {
        if (!isKeyboardVisible) {
            delay(100)
            focusManager.clearFocus()
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(top = dimens.padding.contentVerticalSmall)
    ) {
        if (exercise.formattedBodyWeight != null) {
            item {
                BodyWeightInfo(
                    bodyWeight = exercise.formattedBodyWeight,
                    modifier =
                        Modifier.padding(
                            horizontal = dimens.padding.contentHorizontal,
                            vertical = dimens.padding.itemVertical,
                        ),
                )
            }
        }

        itemsIndexed(exercise.sets, key = { index, _ -> "set_$index" }) { index, set ->
            Row(
                modifier =
                    Modifier.animateItem()
                        .padding(
                            horizontal = dimens.padding.contentHorizontal,
                            vertical = dimens.padding.itemVerticalSmall,
                        )
            ) {
                SetIndexIcon(index, set.isCompleted, modifier = Modifier.padding(top = 16.dp))

                AnimatedContent(
                    targetState = selectedSet == index,
                    modifier = Modifier.weight(1f),
                ) { isSelected ->
                    if (isSelected) {
                        Column(
                            modifier =
                                Modifier.padding(
                                    start = dimens.padding.itemHorizontal,
                                    top = dimens.padding.itemVerticalSmall,
                                    bottom = dimens.padding.itemVerticalSmall,
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
                        ) {
                            SetEditorContent(set)
                            LiftAppButton(
                                onClick = { onSaveSet(exerciseIndex, index) },
                                enabled = set.isInputValid,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text(text = stringResource(R.string.action_save))
                            }
                        }
                    } else {
                        SetItem(exercise, set, index, onSelectSet)
                    }
                }
            }
        }

        setCountButtons(exercise, false, onAction)
    }
}

@Composable
fun SetStatusAppearance() {
    val windowInsetsController = windowInsetsControllerCompat
    val isInDarkTheme = isSystemInDarkTheme()

    DisposableEffect(windowInsetsController, isInDarkTheme) {
        windowInsetsController?.let { controller ->
            val initial = !isInDarkTheme
            controller.isAppearanceLightStatusBars = false
            onDispose { controller.isAppearanceLightStatusBars = initial }
        } ?: onDispose {}
    }
}

internal val editableWorkoutPreview: EditableWorkout
    @Composable
    get() {
        val savedStateHandle = remember { SavedStateHandle() }
        val textFieldStateManager =
            PreviewResource.textFieldStateManager(savedStateHandle = savedStateHandle)
        val stringProvider = PreviewResource.stringProvider
        val formatter = PreviewResource.formatter()
        val dateFormat = DateTimeFormatter.ofPattern(stringProvider.dateFormatDayMonthYear)
        val timeFormat = formatter.getLocalTimeFormatter()
        val exercises =
            listOf(
                EditableWorkout.Exercise(
                    id = 1,
                    name = Name.Raw("Bench Press"),
                    exerciseType = ExerciseType.Weight,
                    mainMuscles = listOf(Muscle.Chest),
                    secondaryMuscles = listOf(Muscle.Triceps),
                    tertiaryMuscles = emptyList(),
                    goal = Workout.Goal.default,
                    sets =
                        listOf(
                            EditableExerciseSet.Weight(
                                weight = 100.0,
                                reps = 10,
                                weightInput = textFieldStateManager.doubleTextField("100.0"),
                                repsInput = textFieldStateManager.intTextField("10"),
                                weightUnit = MassUnit.Kilograms,
                            ),
                            EditableExerciseSet.Weight(
                                weight = 100.0,
                                reps = 9,
                                weightInput = textFieldStateManager.doubleTextField("100"),
                                repsInput = textFieldStateManager.intTextField("9"),
                                weightUnit = MassUnit.Kilograms,
                            ),
                            EditableExerciseSet.Weight(
                                weight = 100.0,
                                reps = 8,
                                weightInput = textFieldStateManager.doubleTextField("100"),
                                repsInput = textFieldStateManager.intTextField("8"),
                                weightUnit = MassUnit.Kilograms,
                            ),
                        ),
                    previousWorkoutSets = emptyList(),
                ),
                EditableWorkout.Exercise(
                    id = 2,
                    name = Name.Raw("Squat"),
                    exerciseType = ExerciseType.Weight,
                    mainMuscles = listOf(Muscle.Quadriceps),
                    secondaryMuscles = listOf(Muscle.Glutes),
                    tertiaryMuscles = emptyList(),
                    goal = Workout.Goal.default,
                    sets =
                        listOf(
                            EditableExerciseSet.Weight(
                                weight = 110.0,
                                reps = 10,
                                weightInput = textFieldStateManager.doubleTextField("110"),
                                repsInput = textFieldStateManager.intTextField("10"),
                                weightUnit = MassUnit.Kilograms,
                            ),
                            EditableExerciseSet.Weight(
                                weight = 0.0,
                                reps = 0,
                                weightInput = textFieldStateManager.doubleTextField("0"),
                                repsInput = textFieldStateManager.intTextField("0"),
                                weightUnit = MassUnit.Kilograms,
                            ),
                            EditableExerciseSet.Weight(
                                weight = 0.0,
                                reps = 0,
                                weightInput = textFieldStateManager.doubleTextField("0"),
                                repsInput = textFieldStateManager.intTextField("0"),
                                weightUnit = MassUnit.Kilograms,
                            ),
                        ),
                    previousWorkoutSets =
                        listOf(
                            ExerciseSet.Weight(110.0, 10, MassUnit.Kilograms),
                            ExerciseSet.Weight(107.5, 10, MassUnit.Kilograms),
                            ExerciseSet.Weight(105.0, 10, MassUnit.Kilograms),
                        ),
                ),
            )
        return EditableWorkout(
            id = 1,
            name = "Push",
            startDate = LocalDateTime.now(),
            endDate = null,
            notes = "",
            exercises = exercises,
            pages =
                exercises.mapIndexed { index, exercise ->
                    WorkoutPage.Exercise(
                        exercise = exercise,
                        index = index,
                        isLast = index == exercises.lastIndex,
                    )
                } +
                    WorkoutPage.Summary(
                        name = textFieldStateManager.stringTextField("Push"),
                        startDate =
                            textFieldStateManager.localDateField(dateFormat, LocalDate.now()),
                        startTime =
                            textFieldStateManager.localTimeField(timeFormat, LocalTime.now()),
                        endDate = textFieldStateManager.localDateField(dateFormat, LocalDate.now()),
                        endTime = textFieldStateManager.localTimeField(timeFormat, LocalTime.now()),
                        notes = textFieldStateManager.stringTextField(""),
                        is24H = true,
                        exercises = exercises,
                        index = exercises.size,
                    ),
        )
    }

@MultiDevicePreview
@Composable
private fun WorkoutScreenPreview() {
    PreviewTheme {
        WorkoutScreen(
            workout = editableWorkoutPreview,
            restTimerService = remember { mutableStateOf(null) },
            selectedPage = 0,
            onAction = {},
        )
    }
}
