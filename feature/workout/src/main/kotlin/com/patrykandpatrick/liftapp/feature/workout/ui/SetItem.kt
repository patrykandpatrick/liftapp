package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.exercise.prettyString
import com.patrykandpatrick.liftapp.core.model.getDisplayName
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.prettyString
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Edit
import com.patrykandpatrick.liftapp.ui.icons.History
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.MessageSquareText
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlinx.coroutines.delay

private const val AUTOMATIC_SELECTION_PRESS_DURATION_MILLIS = 120L
private val setItemContentPadding =
    PaddingValues(start = 16.dp, top = 10.dp, end = 4.dp, bottom = 10.dp)

@Composable
internal fun SetItem(
    exercise: EditableWorkout.Exercise,
    set: EditableExerciseSet<*>,
    index: Int,
    onSelectSet: (Int) -> Unit,
    position: LiftAppListItemPosition,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val previousWorkoutSet = exercise.previousWorkoutSets.getOrNull(index)
    val interactionSource = rememberAutomaticSelectionInteractionSource(isSelected)

    LiftAppListItem(
        modifier = modifier,
        position = position,
        icon = { SetIndexIcon(setIndex = index, isCompleted = set.isCompleted) },
        title = {
            SetTitle(
                text =
                    if (set.isCompleted) {
                        set.prettyString()
                    } else {
                        stringResource(R.string.exercise_set_set_index, index + 1)
                    },
                hasNotes = set.notesInput.value.isNotBlank(),
            )
        },
        description = { HistoryChip(previousWorkoutSet.prettyString()) },
        actions = {
            if (set.isCompleted) {
                LiftAppIconButton(onClick = { onSelectSet(index) }) {
                    Icon(
                        imageVector = LiftAppIcons.Edit,
                        contentDescription = stringResource(R.string.action_edit),
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        },
        contentPadding = setItemContentPadding,
        selected = isSelected,
        onClick = { onSelectSet(index) },
        interactionSource = interactionSource,
    )
}

@Composable
internal fun SupersetSetItem(
    exercise: EditableWorkout.Exercise,
    set: EditableExerciseSet<*>,
    setIndex: Int,
    exerciseIndex: Int,
    isSelected: Boolean,
    onSelect: () -> Unit,
    position: LiftAppListItemPosition,
    modifier: Modifier = Modifier,
) {
    val previousWorkoutSet = exercise.previousWorkoutSets.getOrNull(setIndex)
    val interactionSource = rememberAutomaticSelectionInteractionSource(isSelected)

    LiftAppListItem(
        modifier = modifier,
        position = position,
        icon = {
            SetIndexIcon(
                setIndex = setIndex,
                isCompleted = set.isCompleted,
                label = ('A'.code + exerciseIndex).toChar().toString(),
            )
        },
        title = {
            SetTitle(
                text = exercise.name.getDisplayName(),
                hasNotes = set.notesInput.value.isNotBlank(),
            )
        },
        description = {
            if (set.isCompleted) {
                LiftAppText(
                    text = set.prettyString(),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                HistoryChip(previousWorkoutSet.prettyString())
            }
        },
        actions = {
            if (set.isCompleted) {
                LiftAppIconButton(onClick = onSelect) {
                    Icon(
                        imageVector = LiftAppIcons.Edit,
                        contentDescription = stringResource(R.string.action_edit),
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        },
        contentPadding = setItemContentPadding,
        selected = isSelected,
        onClick = onSelect,
        interactionSource = interactionSource,
    )
}

@Composable
private fun SetTitle(text: String, hasNotes: Boolean) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiftAppText(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (hasNotes) {
            Icon(
                imageVector = LiftAppIcons.MessageSquareText,
                contentDescription = stringResource(R.string.generic_notes),
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun rememberAutomaticSelectionInteractionSource(
    isSelected: Boolean
): MutableInteractionSource {
    val interactionSource = remember { MutableInteractionSource() }
    var wasSelected by remember { mutableStateOf(isSelected) }

    LaunchedEffect(isSelected) {
        val becameSelected = isSelected && !wasSelected
        wasSelected = isSelected
        if (!becameSelected) return@LaunchedEffect

        val press = PressInteraction.Press(Offset.Zero)
        var released = false
        interactionSource.emit(press)
        try {
            delay(AUTOMATIC_SELECTION_PRESS_DURATION_MILLIS)
            interactionSource.emit(PressInteraction.Release(press))
            released = true
        } finally {
            if (!released) interactionSource.tryEmit(PressInteraction.Cancel(press))
        }
    }

    return interactionSource
}

@Composable
private fun HistoryChip(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier =
            Modifier.padding(top = 4.dp)
                .background(colorScheme.secondary, RoundedCornerShape(8.dp))
                .padding(dimens.chip.horizontalPadding, 4.dp),
    ) {
        Icon(
            imageVector = LiftAppIcons.History,
            contentDescription = null,
            tint = colorScheme.onSecondary,
            modifier = Modifier.size(16.dp),
        )
        LiftAppText(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = colorScheme.onSecondary,
        )
    }
}
