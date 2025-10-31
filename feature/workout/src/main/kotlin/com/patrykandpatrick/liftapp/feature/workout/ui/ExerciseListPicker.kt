package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.lerp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.BackdropState
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPickerWindow
import com.swmansion.kmpwheelpicker.WheelPicker
import com.swmansion.kmpwheelpicker.WheelPickerState

@Composable
fun ExerciseListPicker(
    workout: EditableWorkout,
    wheelPickerState: WheelPickerState,
    backdropState: BackdropState,
    modifier: Modifier = Modifier,
) {
    WheelPicker(
        state = wheelPickerState,
        modifier =
            modifier.graphicsLayer {
                scaleX = lerp(1f, 0.9f, backdropState.offsetFraction)
                scaleY = scaleX
                translationY =
                    lerp(
                        -(size.height - wheelPickerState.maxItemHeight) / 2,
                        0f,
                        backdropState.offsetFraction,
                    )
            },
        window = {
            WheelPickerWindow(Modifier.graphicsLayer { alpha = backdropState.offsetFraction })
        },
    ) { index ->
        if (index == workout.exercises.size) {
            SummaryItem(
                isSelected = index == wheelPickerState.index,
                revealOffset = backdropState.offsetFraction,
            )
        } else {
            ExerciseItem(
                index = index,
                exercise = workout.exercises[index],
                isSelected = index == wheelPickerState.index,
                revealOffset = backdropState.offsetFraction,
            )
        }
    }
}

@Composable
private fun ExerciseItem(
    index: Int,
    exercise: EditableWorkout.Exercise,
    isSelected: Boolean,
    revealOffset: Float,
    modifier: Modifier = Modifier,
) {
    ListItem(
        icon = { ListItemDefaults.LeadingText(text = (index + 1).toString()) },
        title = {
            Text(
                text = exercise.name.getDisplayName(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        description = {
            Text(
                text =
                    LocalMarkupProcessor.current.toAnnotatedString(
                        stringResource(
                            R.string.workout_exercise_list_set_format,
                            exercise.completedSetCount,
                            exercise.sets.size,
                            pluralStringResource(R.plurals.set_count, exercise.sets.size),
                        )
                    ),
                maxLines = 1,
                modifier = modifier,
            )
        },
        modifier =
            Modifier.graphicsLayer {
                if (isSelected) return@graphicsLayer
                alpha = revealOffset
            },
    )
}

@Composable
private fun SummaryItem(isSelected: Boolean, revealOffset: Float, modifier: Modifier = Modifier) {
    ListItem(
        iconPainter = painterResource(R.drawable.ic_finish),
        title = stringResource(R.string.workout_summary_title),
        modifier =
            modifier.graphicsLayer {
                if (isSelected) return@graphicsLayer
                alpha = revealOffset
            },
    )
}
