package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutIterator
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.PlainLiftAppButton
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.topTintedEdge
import com.patrykandpatrick.liftapp.ui.theme.BottomSheetShape
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.exercise.prettyString
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.ui.AppBars
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisEnterTransition
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisExitTransition
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet

@Composable
internal fun ColumnScope.ExerciseSetInput(
    workout: EditableWorkout,
    item: WorkoutIterator.Item,
    onCloseClick: () -> Unit,
    onAction: (Action) -> Unit,
) {
    val (exercise, _, setIndex, set) = item
    CompactTopAppBar(
        title = {
            AnimatedContent(targetState = item.exercise, contentKey = { it.id }) { exercise ->
                LiftAppText(text = exercise.name.getDisplayName())
            }
        },
        navigationIcon = {
            CompactTopAppBarDefaults.IconButton(
                imageVector = LiftAppIcons.Cross,
                onClick = onCloseClick,
            )
        },
        colors = AppBars.noBackgroundColors,
    )

    Column(
        modifier =
            Modifier.padding(horizontal = dimens.padding.contentHorizontal)
                .weight(1f)
                .verticalScroll(rememberScrollState())
    ) {
        LiftAppText(
            text =
                stringResource(
                    R.string.exercise_set_set_index_with_total_count,
                    setIndex + 1,
                    exercise.sets.size,
                ),
            style = MaterialTheme.typography.titleMedium,
            color = colorScheme.onSurface,
            modifier =
                Modifier.padding(bottom = dimens.padding.itemVerticalSmall)
                    .align(Alignment.CenterHorizontally),
        )
        SegmentedProgressBar(setIndex + 1, exercise.sets.size)
        Spacer(Modifier.height(dimens.verticalItemSpacing))
        AnimatedContent(
            targetState = exercise to set,
            transitionSpec = {
                sharedXAxisEnterTransition() togetherWith sharedXAxisExitTransition()
            },
        ) { (exercise, set) ->
            Column(verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall)) {
                SetEditorContent(set)

                if (exercise.completedSets.isNotEmpty()) {
                    ListSectionTitle(
                        title = stringResource(R.string.workout_set_entry_section_past_sets),
                        paddingValues = PaddingValues(vertical = dimens.padding.itemVerticalSmall),
                    )
                }

                exercise.completedSets.forEachIndexed { index, pastSet ->
                    PreviousSetItem(pastSet.exerciseSet, index, set::applySet)
                }

                if (exercise.previousWorkoutSets.isNotEmpty()) {
                    ListSectionTitle(
                        title = stringResource(R.string.workout_set_entry_section_previous_workout),
                        paddingValues = PaddingValues(vertical = dimens.padding.itemVerticalSmall),
                    )
                }

                exercise.previousWorkoutSets.forEachIndexed { index, pastSet ->
                    PreviousSetItem(pastSet, index, set::applySet)
                }
            }
        }
    }
    LiftAppButton(
        onClick = { onAction(Action.SaveSet(workout, item)) },
        enabled = set.isInputValid,
        modifier =
            Modifier.fillMaxWidth()
                .padding(
                    horizontal = dimens.padding.contentHorizontal,
                    vertical = dimens.padding.contentVertical,
                ),
    ) {
        LiftAppText(text = stringResource(R.string.action_save))
    }
}

@Composable
private fun PreviousSetItem(
    set: ExerciseSet,
    setIndex: Int,
    onSetClick: (ExerciseSet) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        title = {
            Row {
                SetIndexIcon(setIndex, set.isCompleted)
                PlainLiftAppButton(onClick = { onSetClick(set) }) {
                    LiftAppText(
                        text = set.prettyString(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        },
        paddingValues = PaddingValues(),
        modifier = modifier,
    )
}

@Composable
fun SegmentedProgressBar(progress: Int, maxValue: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
    ) {
        repeat(maxValue) { index ->
            val color =
                animateColorAsState(
                    if (index < progress) {
                        colorScheme.primary
                    } else {
                        colorScheme.divider
                    }
                )
            Spacer(
                Modifier.weight(1f).height(4.dp).background(color = color.value, shape = PillShape)
            )
        }
    }
}

@MultiDevicePreview
@Composable
private fun ExerciseSetInputPreview() {
    PreviewTheme {
        LiftAppBackground(color = Color.Black) {
            Column(
                modifier =
                    Modifier.padding(top = dimens.padding.itemVertical)
                        .topTintedEdge(BottomSheetShape)
                        .background(colorScheme.surface, BottomSheetShape)
                        .fillMaxHeight()
            ) {
                val workout = editableWorkoutPreview
                ExerciseSetInput(
                    workout = workout,
                    item = workout.nextIncompleteItem!!,
                    onCloseClick = {},
                    onAction = {},
                )
            }
        }
    }
}
