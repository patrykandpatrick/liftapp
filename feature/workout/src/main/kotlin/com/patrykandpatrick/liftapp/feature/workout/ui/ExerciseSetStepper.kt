package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.vico.core.extension.forEachIndexedExtended
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.AddStep
import com.patrykandpatryk.liftapp.core.ui.StepConnector
import com.patrykandpatryk.liftapp.core.ui.StepperItem
import com.patrykandpatryk.liftapp.core.ui.StepperItemLabel
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet

@Composable
fun ExerciseSetStepper(
    sets: List<EditableExerciseSet<ExerciseSet>>,
    selectedSet: Int,
    onSelectSet: (Int) -> Unit,
    onAddSetClick: () -> Unit,
    onRemoveSetClick: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    val scrollState = rememberLazyListState()
    val (editMode, setEditMode) = rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(selectedSet) {
        val index = (selectedSet - 1).coerceAtLeast(0) * 2
        scrollState.animateScrollToItem(index)
    }

    LaunchedEffect(editMode) {
        if (editMode) scrollState.animateScrollToItem(scrollState.layoutInfo.totalItemsCount - 1)
    }

    Row(
        modifier = modifier.fillMaxWidth().padding(end = dimens.padding.itemHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontal),
    ) {
        LazyRow(
            state = scrollState,
            modifier = modifier.weight(1f),
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(dimens.stepper.spacing),
        ) {
            sets.forEachIndexedExtended { index, isFirst, isLast, set ->
                item(key = index, contentType = "step") {
                    StepperItem(
                        setIndex = index,
                        selected = index == selectedSet,
                        completed = set.isCompleted,
                        onClick = { onSelectSet(index) },
                        enabled = isFirst || sets[index - 1].isCompleted,
                        label = stepLabel,
                        removeEnabled = isLast && editMode,
                        onRemoveClick = onRemoveSetClick,
                        modifier = Modifier.animateItem(),
                    )
                }

                if (!isLast || editMode) {
                    item(key = index + .5f, contentType = "connector") {
                        StepConnector(
                            isCompleted = if (isLast) false else sets[index + 1].isCompleted,
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }

            if (editMode) {
                item(key = "add_set") {
                    AddStep(
                        onClick = onAddSetClick,
                        label = { StepperItemLabel(stringResource(R.string.action_add_set)) },
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }

        EditButton(editMode = editMode, onChangeEditMode = setEditMode)
    }
}

private val stepLabel: @Composable ColumnScope.(Int, Boolean) -> Unit = { index, enabled ->
    StepperItemLabel(
        text = stringResource(R.string.exercise_set_set_index, index + 1),
        enabled = enabled,
    )
}

@Composable
private fun EditButton(
    editMode: Boolean,
    onChangeEditMode: (editMode: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    AnimatedContent(
        targetState = editMode,
        label = "edit_mode",
        modifier =
            modifier
                .clip(MaterialTheme.shapes.small)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.small)
                .clickable(onClick = { onChangeEditMode(!editMode) })
                .padding(dimens.button.horizontalPadding, dimens.button.verticalPadding)
                .animateContentSize(
                    spring(
                        Spring.DampingRatioHighBouncy,
                        Spring.StiffnessHigh,
                        IntSize.VisibilityThreshold,
                    )
                ),
    ) { editMode ->
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter =
                    if (editMode) painterResource(R.drawable.ic_check)
                    else painterResource(R.drawable.ic_edit),
                contentDescription = stringResource(R.string.action_edit),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier =
                    Modifier.padding(
                            vertical = (dimens.stepper.stepSize - dimens.stepper.stepIconSize) / 2
                        )
                        .size(dimens.stepper.stepIconSize),
            )

            Text(
                text = stringResource(if (editMode) R.string.action_done else R.string.action_edit),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = dimens.stepper.stepLabelPadding),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun EditButtonPreview() {
    LiftAppTheme {
        Surface {
            val (editMode, setEditMode) = remember { mutableStateOf(false) }
            EditButton(
                editMode = editMode,
                onChangeEditMode = setEditMode,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
