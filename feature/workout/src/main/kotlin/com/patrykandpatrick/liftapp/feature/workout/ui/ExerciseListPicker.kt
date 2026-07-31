package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.model.getDisplayName
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.core.ui.ListItemDefaults
import com.patrykandpatrick.liftapp.domain.extension.moved
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.appendCompletedIcon
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.CircleMinus
import com.patrykandpatrick.liftapp.ui.icons.DragHandle
import com.patrykandpatrick.liftapp.ui.icons.FinishFlag
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Open
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import kotlinx.coroutines.flow.distinctUntilChanged
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

internal val WorkoutPickerPeekHeight = 80.dp
private const val FadeThroughProgress = .5f

internal fun workoutPickerExpandedAlpha(revealOffset: Float) =
    ((revealOffset - FadeThroughProgress) / (1f - FadeThroughProgress)).coerceIn(0f, 1f)

@Composable
fun ExerciseListPicker(
    workout: EditableWorkout,
    selectedPage: Int,
    revealOffset: Float,
    selectPage: (Int) -> Unit,
    reorderItems: (List<Long>) -> Unit,
    removeItem: (Long) -> Unit,
    bottomContentPadding: Dp,
    onListScrolledChange: (Boolean) -> Unit,
    openExercise: (ExerciseItemData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items =
        workout.items.mapIndexed { pageIndex, item ->
            WorkoutItemData(
                id = item.id,
                pageIndex = pageIndex,
                exercises =
                    item.exercises.map { exercise ->
                        ExerciseItemData(
                            id = exercise.id,
                            name = exercise.name.getDisplayName(),
                            setCount = exercise.sets.size,
                            completedSetCount = exercise.completedSetCount,
                        )
                    },
                isSuperset = item.isSuperset,
                setCount = item.setCount,
                completedSetCount = item.completedSetCount,
            )
        }

    val collapsedAlpha = (1f - revealOffset / FadeThroughProgress).coerceIn(0f, 1f)
    val expandedAlpha = workoutPickerExpandedAlpha(revealOffset)

    Box(modifier = modifier.fillMaxSize()) {
        if (expandedAlpha > 0f) {
            ReorderableExerciseList(
                items = items,
                selectedPage = selectedPage,
                selectPage = selectPage,
                reorderItems = reorderItems,
                removeItem = removeItem,
                bottomContentPadding = bottomContentPadding,
                onListScrolledChange = onListScrolledChange,
                modifier = Modifier.graphicsLayer { alpha = expandedAlpha },
            )
        }

        if (collapsedAlpha > 0f) {
            CollapsedWorkoutItem(
                items = items,
                selectedPage = selectedPage,
                openExercise = openExercise,
                modifier = Modifier.graphicsLayer { alpha = collapsedAlpha },
            )
        }
    }
}

@Composable
private fun ReorderableExerciseList(
    items: List<WorkoutItemData>,
    selectedPage: Int,
    selectPage: (Int) -> Unit,
    reorderItems: (List<Long>) -> Unit,
    removeItem: (Long) -> Unit,
    bottomContentPadding: Dp,
    onListScrolledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedItemIndex =
        items.indexOfFirst { item -> item.pageIndex == selectedPage }.takeIf { it >= 0 }
            ?: items.size
    val listTopPadding = 8.dp
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = selectedItemIndex)
    val currentOnListScrolledChange = rememberUpdatedState(onListScrolledChange)
    LaunchedEffect(lazyListState) {
        snapshotFlow {
            lazyListState.firstVisibleItemIndex > 0 ||
                lazyListState.firstVisibleItemScrollOffset > 0
        }
            .distinctUntilChanged()
            .collect { currentOnListScrolledChange.value(it) }
    }
    var orderedItems by remember(items) { mutableStateOf(items) }
    val currentOrderedItems by rememberUpdatedState(orderedItems)
    var orderBeforeDrag by remember { mutableStateOf(emptyList<Long>()) }
    val reorderableState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            orderedItems =
                orderedItems.moved(
                    from.index,
                    to.index.coerceAtMost(orderedItems.lastIndex),
                )
        }
    val canRemoveItem = orderedItems.size > 1

    LazyColumn(
        state = lazyListState,
        contentPadding =
            PaddingValues(
                top = listTopPadding,
                bottom = bottomContentPadding + 8.dp,
            ),
        modifier = modifier.fillMaxSize(),
    ) {
        items(items = orderedItems, key = WorkoutItemData::id) { item ->
            ReorderableItem(state = reorderableState, key = item.id) {
                val interactionSource = remember { MutableInteractionSource() }
                fun captureOrder() {
                    orderBeforeDrag = currentOrderedItems.map(WorkoutItemData::id)
                }

                fun persistOrder() {
                    val itemIDs = currentOrderedItems.map(WorkoutItemData::id)
                    if (itemIDs != orderBeforeDrag) {
                        reorderItems(itemIDs)
                    }
                }

                Column(
                    modifier =
                        Modifier.longPressDraggableHandle(
                            interactionSource = interactionSource,
                            onDragStarted = { captureOrder() },
                            onDragStopped = { persistOrder() },
                        )
                ) {
                    WorkoutItem(
                        item = item,
                        isSelected = item.pageIndex == selectedPage,
                        interactionSource = interactionSource,
                        dragHandleModifier =
                            Modifier.draggableHandle(
                                interactionSource = interactionSource,
                                onDragStarted = { captureOrder() },
                                onDragStopped = { persistOrder() },
                            ),
                        onRemove = if (canRemoveItem) ({ removeItem(item.id) }) else null,
                        selectPage = selectPage,
                        modifier = Modifier,
                    )
                }
            }
        }

        item(key = "summary") {
            SummaryItem(
                isSelected = selectedPage == items.size,
                onClick = { selectPage(items.size) },
                modifier = Modifier,
            )
        }
    }
}

@Composable
private fun CollapsedWorkoutItem(
    items: List<WorkoutItemData>,
    selectedPage: Int,
    openExercise: (ExerciseItemData) -> Unit,
    modifier: Modifier = Modifier,
) {
    val item = items.firstOrNull { it.pageIndex == selectedPage }
    if (item == null) {
        SummaryItem(isSelected = false, onClick = {}, modifier = modifier)
    } else {
        CurrentWorkoutItem(item, openExercise, modifier)
    }
}

private data class WorkoutItemData(
    val id: Long,
    val pageIndex: Int,
    val exercises: List<ExerciseItemData>,
    val isSuperset: Boolean,
    val setCount: Int,
    val completedSetCount: Int,
) {
    val allSetsCompleted = setCount > 0 && setCount == completedSetCount
}

data class ExerciseItemData(
    val id: Long,
    val name: String,
    val setCount: Int,
    val completedSetCount: Int,
) {
    val allSetsCompleted = setCount == completedSetCount
}

@Composable
private fun WorkoutItem(
    item: WorkoutItemData,
    isSelected: Boolean,
    interactionSource: MutableInteractionSource,
    dragHandleModifier: Modifier?,
    onRemove: (() -> Unit)?,
    selectPage: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        icon = { ListItemDefaults.LeadingText(text = (item.pageIndex + 1).toString()) },
        title = { WorkoutItemTitle(item) },
        description = { WorkoutItemDescription(item) },
        actions = {
            if (onRemove != null) {
                LiftAppIconButton(onClick = onRemove) {
                    Icon(
                        imageVector = LiftAppIcons.CircleMinus,
                        contentDescription = stringResource(R.string.list_remove),
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            if (dragHandleModifier != null) {
                Icon(
                    imageVector = LiftAppIcons.DragHandle,
                    contentDescription = stringResource(R.string.action_reorder_list),
                    modifier = dragHandleModifier.size(24.dp),
                )
            }
        },
        paddingValues =
            PaddingValues(
                start = dimens.screen.horizontalPadding,
                top = 16.dp,
                end = 20.dp,
                bottom = 16.dp,
            ),
        checked = isSelected,
        interactionSource = interactionSource,
        onClick = { selectPage(item.pageIndex) },
        modifier = modifier.height(WorkoutPickerPeekHeight),
    )
}

@Composable
private fun CurrentWorkoutItem(
    item: WorkoutItemData,
    openExercise: (ExerciseItemData) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        icon = { ListItemDefaults.LeadingText(text = (item.pageIndex + 1).toString()) },
        title = { WorkoutItemTitle(item) },
        description = { WorkoutItemDescription(item) },
        actions = {
            if (!item.isSuperset) {
                LiftAppIconButton(onClick = { openExercise(item.exercises.single()) }) {
                    Icon(
                        imageVector = LiftAppIcons.Open,
                        contentDescription = stringResource(R.string.action_info),
                    )
                }
            }
        },
        paddingValues = PaddingValues(start = 20.dp, top = 16.dp, end = 8.dp, bottom = 16.dp),
        modifier = modifier.height(WorkoutPickerPeekHeight),
    )
}

@Composable
private fun WorkoutItemTitle(item: WorkoutItemData) {
    LiftAppText(
        text =
            buildAnnotatedString {
                append(
                    if (item.isSuperset) {
                        stringResource(R.string.title_superset)
                    } else {
                        item.exercises.single().name
                    }
                )
                if (item.allSetsCompleted) {
                    addStyle(SpanStyle(textDecoration = TextDecoration.LineThrough), 0, length)
                    appendCompletedIcon()
                }
            },
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun WorkoutItemDescription(item: WorkoutItemData) {
    LiftAppText(
        text =
            LocalMarkupProcessor.current.toAnnotatedString(
                stringResource(
                    R.string.workout_exercise_list_set_format,
                    item.completedSetCount,
                    item.setCount,
                    pluralStringResource(R.plurals.set_count, item.setCount),
                )
            ),
        maxLines = 1,
    )
}

@Composable
private fun SummaryItem(isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    ListItem(
        imageVector = LiftAppIcons.FinishFlag,
        title = stringResource(R.string.workout_summary_title),
        checked = isSelected,
        onClick = onClick,
        modifier = modifier.height(WorkoutPickerPeekHeight),
    )
}

@LightAndDarkThemePreview
@Composable
private fun ExerciseListPickerPreview() {
    PreviewTheme {
        LiftAppBackground {
            val items =
                listOf(
                    WorkoutItemData(
                        id = 1,
                        pageIndex = 0,
                        exercises = listOf(ExerciseItemData(1, "Flat Bench Press", 3, 3)),
                        isSuperset = false,
                        setCount = 3,
                        completedSetCount = 3,
                    ),
                    WorkoutItemData(
                        id = 2,
                        pageIndex = 1,
                        exercises = listOf(ExerciseItemData(2, "Incline Dumbbell Press", 4, 2)),
                        isSuperset = false,
                        setCount = 4,
                        completedSetCount = 2,
                    ),
                    WorkoutItemData(
                        id = 3,
                        pageIndex = 2,
                        exercises = listOf(ExerciseItemData(3, "Chest Fly Machine", 3, 0)),
                        isSuperset = false,
                        setCount = 3,
                        completedSetCount = 0,
                    ),
                )
            ReorderableExerciseList(
                items = items,
                selectedPage = 1,
                selectPage = {},
                reorderItems = {},
                removeItem = {},
                bottomContentPadding = WorkoutPickerPeekHeight,
                onListScrolledChange = {},
            )
        }
    }
}
