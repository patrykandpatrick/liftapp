package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.util.lerp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.component.appendCompletedIcon
import com.patrykandpatrick.liftapp.ui.icons.FinishFlag
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.ui.BackdropState
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListItemDefaults
import com.patrykandpatryk.liftapp.core.ui.wheel.WheelPickerWindow
import com.swmansion.kmpwheelpicker.WheelPicker
import com.swmansion.kmpwheelpicker.WheelPickerState
import com.swmansion.kmpwheelpicker.rememberWheelPickerState

@Composable
fun ExerciseListPicker(
    workout: EditableWorkout,
    wheelPickerState: WheelPickerState,
    backdropState: BackdropState,
    modifier: Modifier = Modifier,
) {
    ExerciseListPicker(
        exercises =
            workout.exercises.map { exercise ->
                ExerciseItemData(
                    name = exercise.name.getDisplayName(),
                    setCount = exercise.sets.size,
                    completedSetCount = exercise.completedSetCount,
                )
            },
        wheelPickerState = wheelPickerState,
        revealOffset = backdropState.offsetFraction,
        modifier = modifier,
    )
}

@Composable
fun ExerciseListPicker(
    exercises: List<ExerciseItemData>,
    wheelPickerState: WheelPickerState,
    revealOffset: Float,
    modifier: Modifier = Modifier,
) {
    WheelPicker(
        state = wheelPickerState,
        modifier =
            modifier.graphicsLayer {
                scaleX = lerp(1f, 0.9f, revealOffset)
                scaleY = scaleX
                translationY =
                    lerp(
                        -(size.height - wheelPickerState.slotHeight) / 2,
                        0f,
                        revealOffset,
                    )
            },
        window = { WheelPickerWindow(Modifier.graphicsLayer { alpha = revealOffset }) },
    ) { index ->
        if (index == exercises.size) {
            SummaryItem(isSelected = index == wheelPickerState.index, revealOffset = revealOffset)
        } else {
            ExerciseItem(
                index = index,
                exercise = exercises[index],
                isSelected = index == wheelPickerState.index,
                revealOffset = revealOffset,
            )
        }
    }
}

data class ExerciseItemData(val name: String, val setCount: Int, val completedSetCount: Int) {
    val allSetsCompleted = setCount == completedSetCount
}

@Composable
private fun ExerciseItem(
    index: Int,
    exercise: ExerciseItemData,
    isSelected: Boolean,
    revealOffset: Float,
    modifier: Modifier = Modifier,
) {
    ListItem(
        icon = { ListItemDefaults.LeadingText(text = (index + 1).toString()) },
        title = {
            LiftAppText(
                text =
                    buildAnnotatedString {
                        append(exercise.name)
                        if (exercise.allSetsCompleted) {
                            addStyle(
                                SpanStyle(textDecoration = TextDecoration.LineThrough),
                                0,
                                length,
                            )
                            appendCompletedIcon()
                        }
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        description = {
            LiftAppText(
                text =
                    LocalMarkupProcessor.current.toAnnotatedString(
                        stringResource(
                            R.string.workout_exercise_list_set_format,
                            exercise.completedSetCount,
                            exercise.setCount,
                            pluralStringResource(R.plurals.set_count, exercise.setCount),
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
        imageVector = LiftAppIcons.FinishFlag,
        title = stringResource(R.string.workout_summary_title),
        modifier =
            modifier.graphicsLayer {
                if (isSelected) return@graphicsLayer
                alpha = revealOffset
            },
    )
}

@LightAndDarkThemePreview
@Composable
private fun ExerciseListPickerPreview() {
    PreviewTheme {
        LiftAppBackground {
            val exercises =
                listOf(
                    ExerciseItemData(
                        name = "Flat Bench Press",
                        setCount = 3,
                        completedSetCount = 3,
                    ),
                    ExerciseItemData(
                        name = "Incline Dumbbell Press",
                        setCount = 4,
                        completedSetCount = 2,
                    ),
                    ExerciseItemData(
                        name = "Chest Fly Machine",
                        setCount = 3,
                        completedSetCount = 0,
                    ),
                )
            ExerciseListPicker(
                exercises = exercises,
                wheelPickerState =
                    rememberWheelPickerState(itemCount = exercises.size + 1, initialIndex = 1),
                revealOffset = 1f,
            )
        }
    }
}
