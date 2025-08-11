package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButtonDefaults
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.FilledAdd
import com.patrykandpatrick.liftapp.ui.icons.FilledRemove
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.fadingEdges
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.StepConnector
import com.patrykandpatryk.liftapp.core.ui.StepperItem
import com.patrykandpatryk.liftapp.core.ui.StepperItemLabel
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet

@Composable
fun ExerciseSetStepper(
    sets: List<EditableExerciseSet<ExerciseSet>>,
    selectedSet: Int,
    onSelectSet: (Int) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    ExerciseSetStepper(
        sets = sets.size,
        completedSets = sets.count { it.isCompleted },
        selectedSet = selectedSet,
        onSelectSet = onSelectSet,
        onAddSet = onAddSet,
        onRemoveSet = onRemoveSet,
        modifier = modifier,
        contentPadding = contentPadding,
    )
}

@Composable
fun ExerciseSetStepper(
    sets: Int,
    completedSets: Int,
    selectedSet: Int,
    onSelectSet: (Int) -> Unit,
    onAddSet: () -> Unit,
    onRemoveSet: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val dimens = LocalDimens.current
    val lazyListState = rememberLazyListState()

    LaunchedEffect(selectedSet) {
        val index = (selectedSet - 1).coerceAtLeast(0) * 2
        lazyListState.animateScrollToItem(index)
    }

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        LazyRow(
            state = lazyListState,
            modifier =
                Modifier.weight(1f)
                    .fadingEdges(horizontalEdgeLength = 24.dp, lazyListState = lazyListState),
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(dimens.stepper.spacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(sets) { index ->
                val isLast = index == sets - 1
                item(key = index, contentType = "step") {
                    StepperItem(
                        setIndex = index,
                        selected = index == selectedSet,
                        completed = index < completedSets,
                        onClick = { onSelectSet(index) },
                        enabled = index == 0 || index <= completedSets,
                        label = stepLabel,
                        modifier = Modifier.animateItem(),
                    )
                }

                if (!isLast) {
                    item(key = index + .5f, contentType = "connector") {
                        StepConnector(
                            modifier = Modifier.align(Alignment.CenterVertically).animateItem()
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = dimens.padding.contentHorizontal),
        ) {
            LiftAppIconButton(
                onClick = onRemoveSet,
                colors = LiftAppIconButtonDefaults.outlinedColors,
                maxBorderSize = 40.dp,
            ) {
                Icon(imageVector = LiftAppIcons.FilledRemove, contentDescription = null)
            }

            LiftAppIconButton(
                onClick = onAddSet,
                colors = LiftAppIconButtonDefaults.outlinedColors,
                maxBorderSize = 40.dp,
            ) {
                Icon(imageVector = LiftAppIcons.FilledAdd, contentDescription = null)
            }
        }
    }
}

private val stepLabel: @Composable ColumnScope.(Int, Boolean, Boolean) -> Unit =
    { index, completed, enabled ->
        StepperItemLabel(
            text = stringResource(R.string.exercise_set_set_index, index + 1),
            enabled = enabled,
            completed = completed,
        )
    }

@LightAndDarkThemePreview
@Composable
private fun ExerciseSetStepperPreview() {
    PreviewTheme {
        LiftAppBackground {
            Column(modifier = Modifier.fillMaxWidth()) {
                ExerciseSetStepper(
                    sets = 3,
                    completedSets = 2,
                    selectedSet = 2,
                    onSelectSet = {},
                    onAddSet = {},
                    onRemoveSet = {},
                    modifier = Modifier,
                )
                ExerciseSetStepper(
                    sets = 2,
                    completedSets = 1,
                    selectedSet = 1,
                    onSelectSet = {},
                    onAddSet = {},
                    onRemoveSet = {},
                    modifier = Modifier,
                )
            }
        }
    }
}
