package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.extension.copy
import com.patrykandpatrick.liftapp.core.extension.getBottom
import com.patrykandpatrick.liftapp.core.model.getDisplayName
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.preview.PreviewResource
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatrick.liftapp.core.text.rememberDefaultMarkupProcessor
import com.patrykandpatrick.liftapp.core.ui.AppBars
import com.patrykandpatrick.liftapp.core.ui.Backdrop
import com.patrykandpatrick.liftapp.core.ui.BackdropState
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.animation.sharedXAxisTransition
import com.patrykandpatrick.liftapp.core.ui.rememberBackdropState
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.feature.workout.RestTimerService
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutIterator
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutPage
import com.patrykandpatrick.liftapp.feature.workout.model.getText
import com.patrykandpatrick.liftapp.feature.workout.rememberRestTimerServiceController
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppAlertDialogDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppButtonDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppDestructiveActionDialog
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.component.windowInsetsControllerCompat
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.CircleFading
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.Hourglass
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.MessageSquare
import com.patrykandpatrick.liftapp.ui.icons.MessageSquareText
import com.patrykandpatrick.liftapp.ui.modifier.topTintedEdge
import com.patrykandpatrick.liftapp.ui.theme.BottomSheetShape
import com.patrykandpatrick.liftapp.ui.theme.ButtonBorderShape
import com.patrykandpatrick.liftapp.ui.theme.ButtonShape
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.Typography
import com.patrykandpatrick.liftapp.ui.theme.bottomSheetShadow
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.liftapp.ui.theme.getLiftAppColorScheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

private const val TIMER_BOUND_ANIMATION_DURATION = 120
private const val TIMER_ENTER_ANIMATION_DURATION = 220
private const val TIMER_EXIT_ANIMATION_DURATION = 120
private const val TIMER_ANIMATION_SCALE = .92f

@Composable
fun WorkoutScreen(modifier: Modifier = Modifier, viewModel: WorkoutViewModel = hiltViewModel()) {
    when (val entryState = viewModel.entryState.collectAsStateWithLifecycle().value) {
        WorkoutEntryState.Loading -> Unit
        WorkoutEntryState.Ready -> ActiveWorkoutScreen(viewModel, modifier)
        is WorkoutEntryState.ConfirmContinue ->
            ContinueActiveWorkoutDialog(
                workout = entryState.workout,
                onDismissRequest = viewModel::cancelStartingWorkout,
                onContinue = viewModel::continueActiveWorkout,
            )
    }
}

@Composable
private fun ActiveWorkoutScreen(viewModel: WorkoutViewModel, modifier: Modifier = Modifier) {
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
    val backdropState = rememberBackdropState()
    val coroutineScope = rememberCoroutineScope()
    var backdropListScrolled by remember { mutableStateOf(false) }
    var editedItem by remember(workout?.id) { mutableStateOf<WorkoutIterator.Item?>(null) }
    var showCloseDialog by remember(workout?.id) { mutableStateOf(false) }
    var workoutItemIDToRemove by remember(workout?.id) { mutableStateOf<Long?>(null) }
    val isBackdropClosed = backdropState.offsetFraction <= 0f
    val timerState = restTimerService.value?.timer?.collectAsStateWithLifecycle(null)?.value
    val isTimerActive = timerState != null && !timerState.isFinished
    val manualTimerDuration = workout?.restTimerDuration(selectedPage)
    val backdropBackground = getLiftAppColorScheme(isDarkTheme = true).background

    LaunchedEffect(isBackdropClosed) {
        if (isBackdropClosed) backdropListScrolled = false
    }

    fun requestClose() {
        if (workout?.endDate == null) {
            showCloseDialog = true
        } else {
            onAction(Action.PopBackStack)
        }
    }

    BackHandler(enabled = workout != null, onBack = ::requestClose)

    LiftAppScaffold(
        topBar = {
            LiftAppTheme(darkTheme = true) {
                CompactTopAppBar(
                    title = {
                        Text(
                            text = workout?.name.orEmpty(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    navigationIcon = {
                        LiftAppIconButton(onClick = ::requestClose) {
                            Icon(
                                imageVector = LiftAppIcons.Cross,
                                contentDescription = stringResource(R.string.action_close),
                            )
                        }
                    },
                    actions = {
                        LiftAppIconButton(
                            onClick = {
                                coroutineScope.launch { backdropState.close() }
                                if (isTimerActive) {
                                    restTimerService.value?.cancelTimer()
                                } else if (workout != null && manualTimerDuration != null) {
                                    restTimerService.value?.startTimer(
                                        manualTimerDuration,
                                        workout.id,
                                    )
                                }
                            },
                            enabled =
                                restTimerService.value != null &&
                                    (isTimerActive || manualTimerDuration != null),
                        ) {
                            Icon(
                                imageVector = LiftAppIcons.Hourglass,
                                contentDescription =
                                    stringResource(
                                        if (isTimerActive) {
                                            R.string.rest_timer_action_stop
                                        } else {
                                            R.string.rest_timer_action_start
                                        }
                                    ),
                            )
                        }
                    },
                    colors =
                        AppBars.colors(
                            containerColor = colorScheme.background,
                            scrolledContainerColor = colorScheme.background,
                            contentColor = colorScheme.foreground,
                        ),
                    alwaysShowChrome = backdropState.offsetFraction > 0f && backdropListScrolled,
                )
            }
        },
        bottomBar = {
            workout?.run {
                pages[selectedPage.coerceIn(pages.indices)].also { page ->
                    val recordItem =
                        (page as? WorkoutPage.Exercise)?.let { activeIncompleteItem(it.item) }
                    val primaryText: String
                    val onPrimaryClick: () -> Unit

                    when {
                        isTimerActive -> {
                            primaryText = stringResource(R.string.workout_action_skip_break)
                            onPrimaryClick = { restTimerService.value?.cancelTimer() }
                        }
                        recordItem != null -> {
                            primaryText = stringResource(R.string.workout_action_record_set_results)
                            onPrimaryClick = { editedItem = recordItem }
                        }
                        else -> {
                            primaryText = page.primaryAction.getText()
                            onPrimaryClick = {
                                if (page.primaryAction == Action.FinishWorkout) {
                                    restTimerService.value?.cancelTimer()
                                }
                                onAction(page.primaryAction)
                            }
                        }
                    }

                    BottomBar(
                        primaryText = primaryText,
                        onPrimaryClick = onPrimaryClick,
                        addExercisesRevealFraction = backdropState.offsetFraction,
                        onAddExercisesClick = {
                            onAction(Action.PickExercises(exercises.map { it.id }))
                        },
                    )
                }
            }
        },
        modifier = modifier,
        containerColor = backdropBackground,
        contentWindowInsets = WindowInsets.statusBars,
    ) { paddingValues ->
        if (workout != null) {
            val displayedPage = selectedPage.coerceIn(workout.pages.indices)
            Content(
                workout = workout,
                page = displayedPage,
                setPage = { onAction(Action.SelectPage(it)) },
                restTimerService = restTimerService.value,
                onEditSet = { editedItem = it },
                onRemoveItem = { workoutItemIDToRemove = it },
                onAction = onAction,
                backdropState = backdropState,
                isRestTimerVisible = isTimerActive && isBackdropClosed,
                onBackdropListScrolledChange = { backdropListScrolled = it },
                modifier =
                    Modifier.padding(
                        paddingValues.copy(
                            bottom =
                                WindowInsets.ime
                                    .getBottom()
                                    .coerceAtLeast(paddingValues.calculateBottomPadding())
                        )
                    ),
            )
        }
    }

    if (workout != null) {
        editedItem?.let { itemToEdit ->
            SetInputBottomSheet(
                exercise = itemToEdit.exercise,
                set = itemToEdit.set,
                setIndex = itemToEdit.setIndex,
                onDismissRequest = { editedItem = null },
                onSave = { onAction(Action.SaveSet(workout, itemToEdit)) },
            )
        }

        if (showCloseDialog) {
            CloseActiveWorkoutDialog(
                workoutName = workout.name,
                onContinue = { showCloseDialog = false },
                onPause = {
                    showCloseDialog = false
                    onAction(Action.PopBackStack)
                },
                onEnd = {
                    showCloseDialog = false
                    restTimerService.value?.cancelTimer()
                    onAction(Action.FinishWorkout)
                },
            )
        }

        workoutItemIDToRemove?.let { workoutItemID ->
            workout.items
                .firstOrNull { it.id == workoutItemID }
                ?.let { item ->
                    val itemName =
                        if (item.isSuperset) {
                            stringResource(R.string.title_superset)
                        } else {
                            item.exercises.single().name.getDisplayName()
                        }
                    LiftAppDestructiveActionDialog(
                        title = stringResource(R.string.generic_remove_something, itemName),
                        text = stringResource(R.string.workout_item_remove_message),
                        confirmText = stringResource(R.string.action_remove),
                        dismissText = stringResource(android.R.string.cancel),
                        onDismissRequest = { workoutItemIDToRemove = null },
                        onConfirm = {
                            workoutItemIDToRemove = null
                            onAction(Action.RemoveItem(workoutItemID))
                        },
                    )
                }
        }
    }
}

@Composable
fun ContinueActiveWorkoutDialog(
    workout: Workout,
    onDismissRequest: () -> Unit,
    onContinue: () -> Unit,
) {
    LiftAppAlertDialog(
        icon = { Icon(imageVector = LiftAppIcons.CircleFading, contentDescription = null) },
        title = { Text(stringResource(R.string.workout_ongoing_title)) },
        text = { Text(stringResource(R.string.workout_ongoing_start_message, workout.name)) },
        onDismissRequest = onDismissRequest,
        dismissButton = {
            LiftAppAlertDialogDefaults.DismissButton(
                onClick = onDismissRequest,
                text = stringResource(android.R.string.cancel),
            )
        },
        confirmButton = {
            PlainLiftAppButton(onClick = onContinue) {
                Text(stringResource(R.string.action_continue))
            }
        },
    )
}

@Composable
private fun CloseActiveWorkoutDialog(
    workoutName: String,
    onContinue: () -> Unit,
    onPause: () -> Unit,
    onEnd: () -> Unit,
) {
    LiftAppAlertDialog(
        icon = { Icon(imageVector = LiftAppIcons.CircleFading, contentDescription = null) },
        title = { Text(stringResource(R.string.workout_ongoing_title)) },
        text = { Text(stringResource(R.string.workout_ongoing_close_message, workoutName)) },
        onDismissRequest = onContinue,
        dismissButton = {
            PlainLiftAppButton(onClick = onEnd, showDivider = false) {
                Text(stringResource(R.string.workout_ongoing_action_end))
            }
            LiftAppAlertDialogDefaults.DismissButton(
                onClick = onPause,
                text = stringResource(R.string.workout_ongoing_action_pause),
            )
        },
        confirmButton = {
            PlainLiftAppButton(onClick = onContinue) {
                Text(stringResource(R.string.action_continue))
            }
        },
    )
}

@Composable
private fun Content(
    workout: EditableWorkout,
    page: Int,
    setPage: (Int) -> Unit,
    restTimerService: RestTimerService?,
    onEditSet: (WorkoutIterator.Item) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onAction: (Action) -> Unit,
    backdropState: BackdropState,
    isRestTimerVisible: Boolean,
    onBackdropListScrolledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var sheetContentScrolled by remember(workout.id) { mutableStateOf(false) }
    val handleDividerAlpha by animateFloatAsState(if (sheetContentScrolled) 1f else 0f)
    val targetPage = workout.pages[page]

    LaunchedEffect(page) { sheetContentScrolled = false }

    Box(modifier = modifier) {
        Backdrop(
            backContent = {
                LiftAppTheme(darkTheme = true) {
                    CompositionLocalProvider(
                        LocalMarkupProcessor provides rememberDefaultMarkupProcessor()
                    ) {
                        ExerciseListPicker(
                            workout = workout,
                            selectedPage = page,
                            revealOffset = backdropState.offsetFraction,
                            selectPage = setPage,
                            reorderItems = { itemIDs ->
                                val selectedItem =
                                    (workout.pages.getOrNull(page) as? WorkoutPage.Exercise)?.item
                                onAction(
                                    Action.ReorderItems(
                                        workoutItemIDs = itemIDs,
                                        selectedWorkoutItemID = selectedItem?.id,
                                    )
                                )
                            },
                            removeItem = onRemoveItem,
                            bottomContentPadding =
                                with(LocalDensity.current) {
                                    backdropState.handleHeight.intValue.toDp()
                                },
                            onListScrolledChange = onBackdropListScrolledChange,
                            openExercise = { onAction(Action.GoToExerciseDetails(it.id)) },
                        )
                    }
                }
            },
            backPeekHeight = { WorkoutPickerPeekHeight },
            state = backdropState,
            modifier = Modifier,
        ) {
            Column(
                modifier =
                    Modifier.bottomSheetShadow()
                        .background(color = colorScheme.background, shape = BottomSheetShape)
                        .topTintedEdge(BottomSheetShape)
            ) {
                val coroutineScope = rememberCoroutineScope()
                Spacer(
                    modifier =
                        Modifier.run { with(this@Backdrop) { revealHandle() } }
                            .padding(vertical = 16.dp)
                            .clip(CircleShape)
                            .clickable { coroutineScope.launch { backdropState.toggle() } }
                            .background(color = colorScheme.outline)
                            .width(32.dp)
                            .height(6.dp)
                            .align(Alignment.CenterHorizontally)
                )
                LiftAppHorizontalDivider(Modifier.graphicsLayer { alpha = handleDividerAlpha })

                AnimatedContent(
                    targetState = targetPage,
                    transitionSpec = sharedXAxisTransition(),
                    contentKey = { it.index },
                    modifier = Modifier.fillMaxSize(),
                    label = "page",
                ) { displayedPage ->
                    val displayedPageListState = rememberLazyListState()
                    val displayedPageScrolled by
                        remember(displayedPageListState) {
                            derivedStateOf { displayedPageListState.canScrollBackward }
                        }
                    val isTargetPage = displayedPage.index == targetPage.index

                    LaunchedEffect(isTargetPage, displayedPageScrolled) {
                        if (isTargetPage) sheetContentScrolled = displayedPageScrolled
                    }

                    when (displayedPage) {
                        is WorkoutPage.Exercise ->
                            Page(
                                workout = workout,
                                item = displayedPage.item,
                                listState = displayedPageListState,
                                onEditSet = onEditSet,
                                onAction = onAction,
                                isRestTimerVisible = isRestTimerVisible,
                            )

                        is WorkoutPage.Summary ->
                            Summary(
                                displayedPage,
                                onAction,
                                listState = displayedPageListState,
                                isRestTimerVisible = isRestTimerVisible,
                            )
                    }
                }
            }
        }

        restTimerService?.also {
            RestTimerContainer(
                restTimerService = it,
                isVisible = backdropState.offsetFraction <= 0f,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

private fun LazyListScope.setCountButtons(
    exercises: List<EditableWorkout.Exercise>,
    notesExercise: EditableWorkout.Exercise,
    onAction: (Action) -> Unit,
    onEditNotes: () -> Unit,
) {
    item(key = "set_actions_divider_${exercises.first().workoutItemID}") {
        LiftAppHorizontalDivider(Modifier.animateItem().padding(top = 16.dp))
    }

    item(key = "add_set_${exercises.first().workoutItemID}") {
        Row(
            modifier =
                Modifier.animateItem()
                    .fillMaxWidth()
                    .padding(
                        start = dimens.screen.padding,
                        top = 16.dp,
                        end = dimens.screen.padding,
                    ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiftAppButton(
                onClick = onEditNotes,
                colors = LiftAppButtonDefaults.outlinedButtonColors,
            ) {
                Icon(
                    imageVector =
                        if (notesExercise.notes.isBlank()) {
                            LiftAppIcons.MessageSquare
                        } else {
                            LiftAppIcons.MessageSquareText
                        },
                    contentDescription = null,
                )
                LiftAppText(text = stringResource(R.string.generic_notes))
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiftAppText(
                    text = stringResource(R.string.workout_change_set_count_buttons_title),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    LiftAppButton(
                        onClick = { onAction(Action.RemoveSet(exercises)) },
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
                        LiftAppText(text = "-1", style = Typography.bodySmallMono)
                    }

                    LiftAppButton(
                        onClick = { onAction(Action.AddSet(exercises)) },
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
                    ) {
                        LiftAppText(text = "+1", style = Typography.bodySmallMono)
                    }
                }
            }
        }
    }
}

@Composable
private fun RestTimerEffect(
    viewModel: WorkoutViewModel,
    restTimerService: State<RestTimerService?>,
) {
    LaunchedEffect(viewModel, restTimerService) {
        var previousCompletedSetCount: Int? = null
        viewModel.workout.filterNotNull().collect { workout ->
            val completedSetCount = workout.completedSetCount
            if (
                previousCompletedSetCount?.let { completedSetCount > it } == true &&
                    workout.nextIncompleteItem?.restBefore?.isPositive() == true
            ) {
                restTimerService.value?.startTimer(
                    workout.nextIncompleteItem.restBefore,
                    workout.id,
                )
            }
            previousCompletedSetCount = completedSetCount
        }
    }
}

@Composable
private fun BottomBar(
    primaryText: String,
    onPrimaryClick: () -> Unit,
    addExercisesRevealFraction: Float,
    onAddExercisesClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val revealFraction = addExercisesRevealFraction.takeIf { it.isFinite() }?.coerceIn(0f, 1f) ?: 0f

    Column(modifier = modifier.fillMaxWidth().background(colorScheme.background)) {
        LiftAppHorizontalDivider()
        BoxWithConstraints(
            modifier =
                Modifier.fillMaxWidth()
                    .clipToBounds()
                    .navigationBarsPadding()
                    .padding(vertical = 16.dp)
        ) {
            val pageWidthPixels = constraints.maxWidth
            val horizontalPaddingPixels =
                with(LocalDensity.current) { dimens.screen.padding.roundToPx() }
            val buttonGapPixels = with(LocalDensity.current) { 16.dp.roundToPx() }
            val slideStridePixels =
                (pageWidthPixels - horizontalPaddingPixels * 2 + buttonGapPixels).coerceAtLeast(0)
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .offset {
                            IntOffset(
                                x = -(slideStridePixels * revealFraction).roundToInt(),
                                y = 0,
                            )
                        }
                        .padding(horizontal = dimens.screen.padding)
            ) {
                LiftAppButton(
                    onClick = onPrimaryClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    AnimatedContent(
                        targetState = primaryText,
                        contentAlignment = Alignment.Center,
                        transitionSpec = { elementTransitionSpec },
                    ) { text ->
                        Text(text)
                    }
                }
            }
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .offset {
                            IntOffset(
                                x = (slideStridePixels * (1f - revealFraction)).roundToInt(),
                                y = 0,
                            )
                        }
                        .padding(horizontal = dimens.screen.padding)
            ) {
                LiftAppButton(
                    onClick = onAddExercisesClick,
                    colors = LiftAppButtonDefaults.outlinedButtonColors,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.action_add_exercises))
                }
            }
        }
    }
}

val elementTransitionSpec =
    (fadeIn(tween(300, 100)) + scaleIn(tween(300, 100))).togetherWith(fadeOut(tween(150)))

@Composable
private fun RestTimerContainer(
    restTimerService: RestTimerService,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val timer = restTimerService.timer.collectAsStateWithLifecycle(null).value

    AnimatedContent(
        targetState = timer.takeIf { isVisible },
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
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.fillMaxWidth()
                        .height(RestTimerContainerHeight)
                        .padding(horizontal = 16.dp),
            ) {
                RestTimer(
                    remainingDuration = state.remainingDuration,
                    isPaused = state.isPaused,
                    onToggleIsPaused = restTimerService::toggleTimer,
                    onUpdateTimerBy = restTimerService::updateTimerBy,
                    onCancel = restTimerService::cancelTimer,
                )
            }
        }
    }
}

@Composable
private fun Page(
    workout: EditableWorkout,
    item: EditableWorkout.Item,
    listState: LazyListState,
    onEditSet: (WorkoutIterator.Item) -> Unit,
    onAction: (Action) -> Unit,
    isRestTimerVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    val selectedItem = workout.activeIncompleteItem(item)
    val notesExercise =
        selectedItem?.exercise
            ?: workout.iterator.lastOrNull { it.exercise.workoutItemID == item.id }?.exercise
            ?: item.exercises.first()
    var showExerciseNotes by remember(item.id, notesExercise.id) { mutableStateOf(false) }

    LazyColumn(
        state = listState,
        contentPadding =
            PaddingValues(
                bottom =
                    dimens.screen.padding +
                        if (isRestTimerVisible) RestTimerContainerHeight else 0.dp
            ),
        modifier = modifier.fillMaxSize(),
    ) {
        val regularExercise = item.exercises.singleOrNull()

        if (item.isSuperset) {
            itemsIndexed(
                items = List(item.setCount) { it },
                key = { _, setIndex -> "superset_set_$setIndex" },
            ) { _, setIndex ->
                Column(modifier = Modifier.animateItem().fillMaxWidth()) {
                    ListSectionTitle(
                        title = stringResource(R.string.exercise_set_set_index, setIndex + 1)
                    )
                    item.exercises.forEachIndexed { supersetExerciseIndex, exercise ->
                        val set = exercise.sets.getOrNull(setIndex) ?: return@forEachIndexed
                        val workoutExerciseIndex =
                            workout.exercises.indexOfFirst { it.id == exercise.id }
                        val iteratorItem = workout.iterator.getItem(workoutExerciseIndex, setIndex)
                        SupersetSetItem(
                            exercise = exercise,
                            set = set,
                            setIndex = setIndex,
                            exerciseIndex = supersetExerciseIndex,
                            isSelected =
                                selectedItem?.exerciseIndex == workoutExerciseIndex &&
                                    selectedItem.setIndex == setIndex,
                            onSelect = { onEditSet(iteratorItem) },
                            position =
                                LiftAppListItemPosition(
                                    supersetExerciseIndex,
                                    item.exercises.size,
                                ),
                            modifier =
                                Modifier.fillMaxWidth()
                                    .padding(
                                        start = dimens.screen.padding,
                                        end = dimens.screen.padding,
                                    ),
                        )
                    }
                }
            }
        } else if (regularExercise != null) {
            val exerciseIndex = workout.exercises.indexOfFirst { it.id == regularExercise.id }
            itemsIndexed(
                regularExercise.sets,
                key = { index, _ -> "set_$index" },
            ) { index, set ->
                val iteratorItem = workout.iterator.getItem(exerciseIndex, index)
                val isActiveSet =
                    selectedItem?.exerciseIndex == exerciseIndex && selectedItem.setIndex == index
                SetItem(
                    exercise = regularExercise,
                    set = set,
                    index = index,
                    isSelected = isActiveSet,
                    onSelectSet = { onEditSet(iteratorItem) },
                    position = LiftAppListItemPosition(index, regularExercise.sets.size),
                    modifier =
                        Modifier.animateItem()
                            .fillMaxWidth()
                            .padding(
                                start = dimens.screen.padding,
                                end = dimens.screen.padding,
                            ),
                )
            }
        }

        setCountButtons(
            exercises = item.exercises,
            notesExercise = notesExercise,
            onAction = onAction,
            onEditNotes = { showExerciseNotes = true },
        )
    }

    if (showExerciseNotes) {
        ExerciseNotesBottomSheet(
            exercise = notesExercise,
            onDismissRequest = { showExerciseNotes = false },
            onSave = { notes -> onAction(Action.UpdateExerciseNotes(notesExercise, notes)) },
        )
    }
}

private fun EditableWorkout.activeIncompleteItem(item: EditableWorkout.Item) =
    selectedExerciseAndSet?.takeIf {
        it.exercise.workoutItemID == item.id && !it.isCompleted
    }
        ?: iterator.firstOrNull {
            it.exercise.workoutItemID == item.id && !it.isCompleted
        }

private fun EditableWorkout.restTimerDuration(pageIndex: Int) =
    ((pages.getOrNull(pageIndex) as? WorkoutPage.Exercise)?.let { activeIncompleteItem(it.item) }
            ?: nextIncompleteItem)
        ?.exercise
        ?.goal
        ?.restTime
        ?.takeIf { it.isPositive() }

/**
 * Keeps the status bar icons light for as long as the dark header is on screen, and hands them back
 * to whatever the app's own theme calls for on the way out. That is the theme the user chose rather
 * than the system's, which the two can disagree about.
 */
@Composable
fun SetStatusAppearance() {
    val windowInsetsController = windowInsetsControllerCompat
    val isInDarkTheme = colorScheme.isDarkColorScheme

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
                    notes = "Keep shoulder blades retracted.",
                    sets =
                        listOf(
                            EditableExerciseSet.Weight(
                                weight = 100.0,
                                reps = 10,
                                weightInput = textFieldStateManager.doubleTextField("100.0"),
                                repsInput = textFieldStateManager.intTextField("10"),
                                weightUnit = MassUnit.Kilograms,
                                notesInput = textFieldStateManager.stringTextField(),
                            ),
                            EditableExerciseSet.Weight(
                                weight = 100.0,
                                reps = 9,
                                weightInput = textFieldStateManager.doubleTextField("100"),
                                repsInput = textFieldStateManager.intTextField("9"),
                                weightUnit = MassUnit.Kilograms,
                                notesInput = textFieldStateManager.stringTextField(),
                            ),
                            EditableExerciseSet.Weight(
                                weight = 100.0,
                                reps = 8,
                                weightInput = textFieldStateManager.doubleTextField("100"),
                                repsInput = textFieldStateManager.intTextField("8"),
                                weightUnit = MassUnit.Kilograms,
                                notesInput = textFieldStateManager.stringTextField(),
                            ),
                        ),
                    previousWorkoutSets = emptyList(),
                    workoutItemID = 1,
                    workoutItemType = RoutineItemType.Superset,
                    exerciseOrder = 0,
                ),
                EditableWorkout.Exercise(
                    id = 2,
                    name = Name.Raw("Squat"),
                    exerciseType = ExerciseType.Weight,
                    mainMuscles = listOf(Muscle.Quadriceps),
                    secondaryMuscles = listOf(Muscle.Glutes),
                    tertiaryMuscles = emptyList(),
                    goal = Workout.Goal.default,
                    notes = "",
                    sets =
                        listOf(
                            EditableExerciseSet.Weight(
                                weight = 110.0,
                                reps = 10,
                                weightInput = textFieldStateManager.doubleTextField("110"),
                                repsInput = textFieldStateManager.intTextField("10"),
                                weightUnit = MassUnit.Kilograms,
                                notesInput = textFieldStateManager.stringTextField(),
                            ),
                            EditableExerciseSet.Weight(
                                weight = 0.0,
                                reps = 0,
                                weightInput = textFieldStateManager.doubleTextField("0"),
                                repsInput = textFieldStateManager.intTextField("0"),
                                weightUnit = MassUnit.Kilograms,
                                notesInput = textFieldStateManager.stringTextField(),
                            ),
                            EditableExerciseSet.Weight(
                                weight = 0.0,
                                reps = 0,
                                weightInput = textFieldStateManager.doubleTextField("0"),
                                repsInput = textFieldStateManager.intTextField("0"),
                                weightUnit = MassUnit.Kilograms,
                                notesInput = textFieldStateManager.stringTextField(),
                            ),
                        ),
                    previousWorkoutSets =
                        listOf(
                            ExerciseSet.Weight(110.0, 10, MassUnit.Kilograms),
                            ExerciseSet.Weight(107.5, 10, MassUnit.Kilograms),
                            ExerciseSet.Weight(105.0, 10, MassUnit.Kilograms),
                        ),
                    workoutItemID = 1,
                    workoutItemType = RoutineItemType.Superset,
                    exerciseOrder = 1,
                ),
            )
        val workoutItems = EditableWorkout.groupExercises(exercises)
        return EditableWorkout(
            id = 1,
            name = "Push",
            startDate = LocalDateTime.now(),
            endDate = null,
            notes = "",
            exercises = exercises,
            pages =
                workoutItems.mapIndexed { index, item ->
                    WorkoutPage.Exercise(
                        item = item,
                        index = index,
                        isLast = index == workoutItems.lastIndex,
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
                        index = workoutItems.size,
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
