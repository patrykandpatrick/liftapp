package com.patrykandpatrick.liftapp.feature.routine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.model.Unfold
import com.patrykandpatrick.liftapp.core.model.getPrettyStringLong
import com.patrykandpatrick.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatrick.liftapp.domain.extension.moved
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemWithExercises
import com.patrykandpatrick.liftapp.feature.routine.model.Action
import com.patrykandpatrick.liftapp.feature.routine.model.ScreenState
import com.patrykandpatrick.liftapp.ui.component.EmptyState
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.CircleMinus
import com.patrykandpatrick.liftapp.ui.icons.DragHandle
import com.patrykandpatrick.liftapp.ui.icons.Goal
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import sh.calvin.reorderable.ReorderableColumn
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/** The size [DragHandle] draws at, which is what Material gives an [Icon] by default. */
private val DragHandleSize = 24.dp

@Composable
internal fun Exercises(
    loadableState: Loadable<ScreenState>,
    onAction: (Action) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    loadableState.Unfold { state ->
        Exercises(
            state = state,
            onAction = onAction,
            bottomPadding = bottomPadding,
            modifier = modifier,
        )
    }
}

@Composable
private fun Exercises(
    state: ScreenState,
    onAction: (Action) -> Unit,
    bottomPadding: Dp,
    modifier: Modifier = Modifier,
) {
    if (state.items.isEmpty()) {
        EmptyState(
            icon = LiftAppIcons.BicepsFlexed,
            message = stringResource(R.string.state_no_exercises),
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(
                        start = LocalDimens.current.screen.padding,
                        end = LocalDimens.current.screen.padding,
                    ),
        )
        return
    }

    var items by remember(state.items) { mutableStateOf(state.items) }
    val currentItems by rememberUpdatedState(items)
    val (segmentStartIndices, segmentCount) =
        remember(items) {
            var nextIndex = 0
            val startIndices = items.associate { item ->
                item.id to
                    nextIndex.also {
                        nextIndex += if (item.isSuperset) item.exercises.size + 1 else 1
                    }
            }
            startIndices to nextIndex
        }
    var orderBeforeDrag by remember { mutableStateOf(emptyList<Long>()) }
    val lazyListState = rememberLazyListState()
    val reorderableState =
        rememberReorderableLazyListState(lazyListState) { from, to ->
            items = items.moved(from.index, to.index)
        }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize().background(colorScheme.background),
        contentPadding =
            PaddingValues(
                start = LocalDimens.current.screen.padding,
                top = LocalDimens.current.screen.padding,
                end = LocalDimens.current.screen.padding,
                bottom = bottomPadding,
            ),
    ) {
        itemsIndexed(
            items = items,
            key = { _, item -> item.id },
            contentType = { _, item -> item.type },
        ) { _, item ->
            ReorderableItem(state = reorderableState, key = item.id) {
                val interactionSource = remember { MutableInteractionSource() }
                val segmentStartIndex = checkNotNull(segmentStartIndices[item.id])
                fun captureOrder() {
                    orderBeforeDrag = currentItems.map(RoutineItemWithExercises::id)
                }

                fun persistOrder() {
                    val itemIDs = currentItems.map(RoutineItemWithExercises::id)
                    if (itemIDs != orderBeforeDrag) {
                        onAction(Action.ReorderItems(itemIDs))
                    }
                }

                Column {
                    RoutineItemRow(
                        item = item,
                        interactionSource = interactionSource,
                        dragHandleModifier =
                            Modifier.draggableHandle(
                                interactionSource = interactionSource,
                                onDragStarted = { captureOrder() },
                                onDragStopped = { persistOrder() },
                            ),
                        onAction = onAction,
                        position =
                            LiftAppListItemPosition(
                                index = segmentStartIndex,
                                count = segmentCount,
                            ),
                        modifier =
                            Modifier.longPressDraggableHandle(
                                interactionSource = interactionSource,
                                onDragStarted = { captureOrder() },
                                onDragStopped = { persistOrder() },
                            ),
                    )
                    if (item.isSuperset) {
                        SupersetMembers(
                            item = item,
                            onAction = onAction,
                            segmentStartIndex = segmentStartIndex,
                            segmentCount = segmentCount,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineItemRow(
    item: RoutineItemWithExercises,
    interactionSource: MutableInteractionSource,
    dragHandleModifier: Modifier,
    onAction: (Action) -> Unit,
    position: LiftAppListItemPosition,
    modifier: Modifier = Modifier,
) {
    val isSuperset = item.isSuperset
    val exercise = item.exercises.first()

    LiftAppListItem(
        modifier = modifier,
        position = position,
        icon = { DragHandle(dragHandleModifier) },
        title = {
            Text(
                text = if (isSuperset) stringResource(R.string.title_superset) else exercise.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        description = {
            if (isSuperset) {
                Text(
                    supersetSummary(
                        exerciseCount = item.exercises.size,
                        sets = checkNotNull(item.supersetConfig).sets,
                    )
                )
            } else {
                Text(exerciseDescription(exercise))
            }
        },
        actions = {
            GoalButton(
                onClick = {
                    if (isSuperset) {
                        onAction(Action.EditSuperset(item.id))
                    } else {
                        onAction(Action.NavigateToExerciseGoal(exercise.id))
                    }
                }
            )
            RemoveButton(onClick = { onAction(Action.RemoveItem(item.id)) })
        },
        contentPadding =
            PaddingValues(
                start = 16.dp,
                top = 10.dp,
                end = 4.dp,
                bottom = 10.dp,
            ),
        interactionSource = interactionSource,
        onClick =
            if (isSuperset) {
                { onAction(Action.EditSuperset(item.id)) }
            } else {
                { onAction(Action.NavigateToExercise(exercise.id)) }
            },
    )
}

@Composable
private fun SupersetMembers(
    item: RoutineItemWithExercises,
    onAction: (Action) -> Unit,
    segmentStartIndex: Int,
    segmentCount: Int,
    modifier: Modifier = Modifier,
) {
    // Taking any more exercises out would leave too few of them for a superset.
    val isRemovable = item.exercises.size > RoutineItem.MIN_SUPERSET_SIZE

    ReorderableColumn(
        list = item.exercises,
        onSettle = { from, to -> onAction(Action.ReorderSupersetExercise(item.id, from, to)) },
        modifier = modifier,
    ) { index, exercise, _ ->
        val interactionSource = remember { MutableInteractionSource() }

        ReorderableItem {
            SupersetMemberRow(
                exercise = exercise,
                onRemove =
                    if (isRemovable) {
                        { onAction(Action.RemoveSupersetExercise(item.id, exercise.id)) }
                    } else {
                        null
                    },
                interactionSource = interactionSource,
                dragHandleModifier =
                    Modifier.draggableHandle(interactionSource = interactionSource),
                onAction = onAction,
                position =
                    LiftAppListItemPosition(
                        index = segmentStartIndex + index + 1,
                        count = segmentCount,
                    ),
                modifier = Modifier.longPressDraggableHandle(interactionSource = interactionSource),
            )
        }
    }
}

@Composable
private fun SupersetMemberRow(
    exercise: RoutineExerciseItem,
    onRemove: (() -> Unit)?,
    interactionSource: MutableInteractionSource,
    dragHandleModifier: Modifier,
    onAction: (Action) -> Unit,
    position: LiftAppListItemPosition,
    modifier: Modifier = Modifier,
) {
    // Inset the whole row so its surface and interaction feedback communicate that the exercise
    // belongs to the superset, while keeping its content in the same visual position as before.
    val indent = DragHandleSize + 16.dp

    LiftAppListItem(
        title = { Text(exercise.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        position = position,
        description = { Text(exerciseDescription(exercise)) },
        icon = { DragHandle(dragHandleModifier) },
        actions = {
            GoalButton(onClick = { onAction(Action.NavigateToExerciseGoal(exercise.id)) })
            if (onRemove != null) RemoveButton(onClick = onRemove)
        },
        contentPadding =
            PaddingValues(
                start = 16.dp,
                top = LocalDimens.current.screen.padding,
                end = 4.dp,
                bottom = LocalDimens.current.screen.padding,
            ),
        interactionSource = interactionSource,
        onClick = { onAction(Action.NavigateToExercise(exercise.id)) },
        modifier = modifier.padding(start = indent),
    )
}

@Composable
private fun DragHandle(modifier: Modifier = Modifier) {
    Icon(
        imageVector = LiftAppIcons.DragHandle,
        contentDescription = stringResource(R.string.action_reorder_list),
        modifier = modifier,
    )
}

@Composable
private fun RemoveButton(onClick: () -> Unit) {
    LiftAppIconButton(onClick = onClick) {
        Icon(
            imageVector = LiftAppIcons.CircleMinus,
            contentDescription = stringResource(R.string.list_remove),
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun GoalButton(onClick: () -> Unit) {
    LiftAppIconButton(onClick = onClick) {
        Icon(
            imageVector = LiftAppIcons.Goal,
            contentDescription = stringResource(R.string.action_edit_goal),
            modifier = Modifier.size(24.dp),
        )
    }
}

@Composable
private fun supersetSummary(exerciseCount: Int, sets: Int): AnnotatedString {
    val text =
        stringResource(
            R.string.superset_summary_format,
            exerciseCount,
            pluralStringResource(R.plurals.exercise_count, exerciseCount),
            sets,
            pluralStringResource(R.plurals.set_count, sets),
        )
    return LocalMarkupProcessor.current.toAnnotatedString(text)
}

@Composable
private fun exerciseDescription(exercise: RoutineExerciseItem) = buildAnnotatedString {
    append(exercise.goal.getPrettyStringLong(exercise.type))
    append("\n")
    append(exercise.muscles)
}
