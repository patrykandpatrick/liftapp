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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.node.Ref
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
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
import com.patrykandpatrick.liftapp.feature.workout.model.prettyString
import com.patrykandpatrick.liftapp.feature.workout.rememberRestTimerServiceController
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.LiftAppModalBottomSheet
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.appendCompletedIcon
import com.patrykandpatrick.liftapp.ui.component.windowInsetsControllerCompat
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
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
import com.patrykandpatryk.liftapp.core.exercise.prettyString
import com.patrykandpatryk.liftapp.core.extension.copy
import com.patrykandpatryk.liftapp.core.extension.getBottom
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.text.parseMarkup
import com.patrykandpatryk.liftapp.core.ui.AppBars
import com.patrykandpatryk.liftapp.core.ui.Backdrop
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
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

    WorkoutScreen2(workout, restTimerService, selectedPage, viewModel::onAction, modifier)
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
    onSaveSet: (Int, Int) -> Unit,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val wheelPickerState =
        rememberWheelPickerState(
            itemCount = workout.exercises.size + 1,
            initialIndex = workout.firstIncompleteOrLastExerciseIndex,
        )
    val backdropState = rememberBackdropState()

    LaunchedEffect(page) { launch { wheelPickerState.animateScrollTo(page) } }

    LaunchedEffect(wheelPickerState.index) { setPage(wheelPickerState.index) }

    Box(modifier = modifier.imePadding()) {
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
                        .background(
                            brush =
                                Brush.verticalGradient(
                                    listOf(colorScheme.surface, colorScheme.surface)
                                ),
                            shape = BottomSheetShape,
                        )
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
fun WorkoutScreen2(
    workout: EditableWorkout?,
    restTimerService: State<RestTimerService?>,
    selectedPage: Int,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListStateRef = remember { Ref<LazyListState?>() }

    workout?.selectedSelectedExerciseAndSet?.also { selectedExerciseInput ->
        LiftAppModalBottomSheet(
            onDismissRequest = { onAction(Action.ClearSetEditor) },
            modifier = Modifier.fillMaxHeight(),
        ) { dismiss ->
            ExerciseSetInput(workout, selectedExerciseInput, dismiss, onAction)
        }
    }
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
            )
        },
        bottomBar = {
            workout?.nextIncompleteItem?.also { nextIncompleteItem ->
                val scope = rememberCoroutineScope()
                BottomBar(
                    nextIncompleteItem = nextIncompleteItem,
                    onGoToNextIncompleteItem = {
                        scope.launch {
                            lazyListStateRef.value?.animateScrollToItemCenter(
                                workout.getNextSetScrollPosition()
                            )
                            onAction(Action.ShowSetEditor(nextIncompleteItem))
                        }
                    },
                )
            }
        },
    ) { paddingValues ->
        workout?.also { workout ->
            val lazyListState = rememberLazyListState(workout.getNextSetScrollPosition())
            lazyListStateRef.value = lazyListState

            LaunchedEffect(
                workout.nextIncompleteItem,
                lazyListState.layoutInfo.afterContentPadding,
            ) {
                if (workout.nextIncompleteItem != null) {
                    lazyListState.animateScrollToItem(workout.getNextSetScrollPosition())
                }
            }

            LazyColumn(
                state = lazyListState,
                contentPadding = paddingValues,
                modifier = modifier.fillMaxSize(),
            ) {
                workout.exercises.forEachIndexed { exerciseIndex, exercise ->
                    exerciseWithSets(workout, exerciseIndex, exercise, onAction)
                    setCountButtons(exercise, exerciseIndex < workout.exercises.lastIndex, onAction)
                }
            }
        }
    }
}

private fun EditableWorkout.getNextSetScrollPosition(): Int {
    val exerciseIndex = nextExerciseSet?.exerciseIndex ?: exercises.lastIndex
    val exerciseCount = 1
    val buttonsCount = 1
    var position = 0
    exercises.forEachIndexed { index, exercise ->
        position += exerciseCount + exercise.sets.count { it.isCompleted }
        if (index == exerciseIndex) return position
        position += buttonsCount
    }
    return position
}

private suspend fun LazyListState.animateScrollToItemCenter(index: Int) {
    animateScrollToItem(index, -layoutInfo.viewportSize.height / 2 + layoutInfo.afterContentPadding)
}

private fun LazyListScope.exerciseWithSets(
    workout: EditableWorkout,
    exerciseIndex: Int,
    exercise: EditableWorkout.Exercise,
    onAction: (Action) -> Unit,
) {
    item(key = exercise.id) {
        ListItem(
            icon = { ListItemDefaults.LeadingText((exerciseIndex + 1).toString()) },
            title = {
                LiftAppText(
                    text =
                        buildAnnotatedString {
                            append(exercise.name.getDisplayName())
                            if (exercise.completedSetCount == exercise.sets.size) {
                                addStyle(
                                    SpanStyle(textDecoration = TextDecoration.LineThrough),
                                    0,
                                    length,
                                )
                                appendCompletedIcon()
                            }
                        },
                    maxLines = 1,
                )
            },
            description = {
                LiftAppText(
                    text =
                        parseMarkup(
                            stringResource(
                                R.string.workout_exercise_list_set_format,
                                exercise.completedSetCount,
                                exercise.sets.size,
                                pluralStringResource(R.plurals.set_count, exercise.sets.size),
                            )
                        )
                )
            },
            modifier = Modifier.animateItem().fillMaxWidth(),
        )

        ListSectionTitle(
            title = stringResource(R.string.goal_sets),
            paddingValues =
                PaddingValues(dimens.padding.contentVertical, dimens.padding.itemVerticalSmall),
            modifier = Modifier.animateItem(),
        )
    }

    exercise.sets.forEachIndexed { setIndex, set ->
        val previousWorkoutSet =
            set.suggestions.find {
                it.type == EditableExerciseSet.SetSuggestion.Type.PreviousWorkout
            } // TODO replace list with fields
        item(key = "set_${exercise.id}_$setIndex") {
            ListItem(
                onClick = {
                    onAction(
                        Action.ShowSetEditor(workout.iterator.getItem(exerciseIndex, setIndex))
                    )
                },
                icon = { SetIndexIcon(setIndex, set.isCompleted) },
                title = {
                    LiftAppText(
                        text = set.prettyString(),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                description =
                    if (previousWorkoutSet != null) {
                        {
                            LiftAppText(
                                text = previousWorkoutSet.set.prettyString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSecondary,
                                modifier =
                                    Modifier.padding(top = 4.dp)
                                        .background(colorScheme.secondary, RoundedCornerShape(6.dp))
                                        .padding(dimens.chip.horizontalPadding, 1.dp),
                            )
                        }
                    } else {
                        null
                    },
                paddingValues =
                    PaddingValues(
                        horizontal = dimens.padding.contentHorizontal,
                        vertical = dimens.padding.itemVerticalSmall,
                    ),
                modifier = Modifier.animateItem(),
            )
        }
    }
}

private fun LazyListScope.setCountButtons(
    exercise: EditableWorkout.Exercise,
    showDivider: Boolean,
    onAction: (Action) -> Unit,
) {
    item(key = "add_set_${exercise.id}") {
        Row(
            modifier =
                Modifier.animateItem()
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimens.padding.contentHorizontal,
                        vertical = dimens.padding.itemVerticalSmall,
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
    val padding = LocalDimens.current.padding
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

@Composable
private fun Page(
    exerciseIndex: Int,
    exercise: EditableWorkout.Exercise,
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
    onSaveSet: (Int, Int) -> Unit,
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
                    onClick = { onSaveSet(exerciseIndex, setIndex) },
                    enabled = set.isInputValid,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(R.string.action_save))
                }
            }
        }
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
                                suggestions =
                                    listOf(
                                        EditableExerciseSet.SetSuggestion(
                                            set = ExerciseSet.Weight(97.5, 10, MassUnit.Kilograms),
                                            type =
                                                EditableExerciseSet.SetSuggestion.Type
                                                    .PreviousWorkout,
                                        )
                                    ),
                            ),
                            EditableExerciseSet.Weight(
                                weight = 100.0,
                                reps = 9,
                                weightInput = textFieldStateManager.doubleTextField("100"),
                                repsInput = textFieldStateManager.intTextField("9"),
                                weightUnit = MassUnit.Kilograms,
                                suggestions =
                                    listOf(
                                        EditableExerciseSet.SetSuggestion(
                                            set = ExerciseSet.Weight(97.5, 9, MassUnit.Kilograms),
                                            type =
                                                EditableExerciseSet.SetSuggestion.Type
                                                    .PreviousWorkout,
                                        )
                                    ),
                            ),
                            EditableExerciseSet.Weight(
                                weight = 100.0,
                                reps = 8,
                                weightInput = textFieldStateManager.doubleTextField("100"),
                                repsInput = textFieldStateManager.intTextField("8"),
                                weightUnit = MassUnit.Kilograms,
                                suggestions =
                                    listOf(
                                        EditableExerciseSet.SetSuggestion(
                                            set = ExerciseSet.Weight(97.5, 8, MassUnit.Kilograms),
                                            type =
                                                EditableExerciseSet.SetSuggestion.Type
                                                    .PreviousWorkout,
                                        )
                                    ),
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
                                suggestions = emptyList(),
                            ),
                            EditableExerciseSet.Weight(
                                weight = 0.0,
                                reps = 0,
                                weightInput = textFieldStateManager.doubleTextField("0"),
                                repsInput = textFieldStateManager.intTextField("0"),
                                weightUnit = MassUnit.Kilograms,
                                suggestions =
                                    listOf(
                                        EditableExerciseSet.SetSuggestion(
                                            set = ExerciseSet.Weight(107.5, 10, MassUnit.Kilograms),
                                            type =
                                                EditableExerciseSet.SetSuggestion.Type
                                                    .PreviousWorkout,
                                        ),
                                        EditableExerciseSet.SetSuggestion(
                                            set = ExerciseSet.Weight(110.0, 10, MassUnit.Kilograms),
                                            type =
                                                EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                                        ),
                                    ),
                            ),
                            EditableExerciseSet.Weight(
                                weight = 0.0,
                                reps = 0,
                                weightInput = textFieldStateManager.doubleTextField("0"),
                                repsInput = textFieldStateManager.intTextField("0"),
                                weightUnit = MassUnit.Kilograms,
                                suggestions = emptyList(),
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
private fun WorkoutScreen2Preview() {
    PreviewTheme {
        WorkoutScreen2(
            workout = editableWorkoutPreview,
            restTimerService = mutableStateOf(null),
            selectedPage = 0,
            onAction = {},
        )
    }
}

@MultiDevicePreview
@Composable
private fun WorkoutScreenPreview() {
    PreviewTheme {
        WorkoutScreen(
            workout = editableWorkoutPreview,
            restTimerService = mutableStateOf(null),
            selectedPage = 0,
            onAction = {},
        )
    }
}
