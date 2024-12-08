package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.prettyString
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource.textFieldStateManager
import com.patrykandpatryk.liftapp.core.text.DoubleTextFieldState
import com.patrykandpatryk.liftapp.core.text.IntTextFieldState
import com.patrykandpatryk.liftapp.core.text.LongTextFieldState
import com.patrykandpatryk.liftapp.core.text.updateValueBy
import com.patrykandpatryk.liftapp.core.ui.InputFieldLayout
import com.patrykandpatryk.liftapp.core.ui.SupportingText
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.ui.wheel.DurationPicker
import com.patrykandpatryk.liftapp.domain.Constants.Input.Increment
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun ColumnScope.SetEditorContent(editableExerciseSet: EditableExerciseSet) {
    when (editableExerciseSet) {
        is EditableExerciseSet.Weight -> {
            WeightInput(
                textFieldState = editableExerciseSet.weightInput,
                unit = editableExerciseSet.weightUnit,
            )
            RepsInput(textFieldState = editableExerciseSet.repsInput)
        }

        is EditableExerciseSet.Calisthenics -> {
            BodyWeightInput(
                textFieldState = editableExerciseSet.bodyWeightInput,
                unit = editableExerciseSet.weightUnit,
            )
            WeightInput(
                textFieldState = editableExerciseSet.weightInput,
                unit = editableExerciseSet.weightUnit,
            )
            RepsInput(textFieldState = editableExerciseSet.repsInput)
        }

        is EditableExerciseSet.Reps -> {
            RepsInput(textFieldState = editableExerciseSet.repsInput)
        }

        is EditableExerciseSet.Cardio -> {
            TimeInput(textFieldState = editableExerciseSet.durationInput)
            DistanceInput(
                textFieldState = editableExerciseSet.distanceInput,
                unit = editableExerciseSet.distanceUnit,
            )
            CaloriesInput(textFieldState = editableExerciseSet.kcalInput)
        }

        is EditableExerciseSet.Time -> {
            TimeInput(textFieldState = editableExerciseSet.timeInput)
        }
    }
}

@Composable
private fun WeightInput(
    textFieldState: DoubleTextFieldState,
    unit: ValueUnit,
    modifier: Modifier = Modifier,
) {
    NumberInput(
        textFieldState = textFieldState,
        onPlusClick = { long -> textFieldState.updateValueBy(Increment.getWeight(long)) },
        onMinusClick = { long -> textFieldState.updateValueBy(-Increment.getWeight(long)) },
        hint = stringResource(R.string.exercise_set_input_weight),
        suffix = unit.prettyString(),
        modifier = modifier,
    )
}

@Composable
private fun BodyWeightInput(
    textFieldState: DoubleTextFieldState,
    unit: ValueUnit,
    modifier: Modifier = Modifier,
) {
    NumberInput(
        textFieldState = textFieldState,
        onPlusClick = { long -> textFieldState.updateValueBy(Increment.getWeight(long)) },
        onMinusClick = { long -> textFieldState.updateValueBy(-Increment.getWeight(long)) },
        hint = stringResource(R.string.exercise_set_input_body_weight),
        suffix = unit.prettyString(),
        modifier = modifier,
    )
}

@Composable
private fun RepsInput(textFieldState: IntTextFieldState, modifier: Modifier = Modifier) {
    NumberInput(
        textFieldState = textFieldState,
        onPlusClick = { long -> textFieldState.updateValueBy(Increment.getReps(long)) },
        onMinusClick = { long -> textFieldState.updateValueBy(-Increment.getReps(long)) },
        hint = stringResource(R.string.exercise_set_input_reps),
        modifier = modifier,
    )
}

@Composable
private fun TimeInput(textFieldState: LongTextFieldState, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        InputFieldLayout(
            isError = textFieldState.hasError,
            label = { Text(text = stringResource(R.string.exercise_set_input_duration)) },
        ) {
            DurationPicker(
                duration = textFieldState.value.milliseconds,
                onDurationChange = { textFieldState.updateValue(it.inWholeMilliseconds) },
            )
        }

        SupportingText(
            value = textFieldState.text,
            errorText = textFieldState.errorMessage?.let(::AnnotatedString),
        )
    }
}

@Composable
private fun DistanceInput(
    textFieldState: DoubleTextFieldState,
    unit: ValueUnit,
    modifier: Modifier = Modifier,
) {
    NumberInput(
        textFieldState = textFieldState,
        onPlusClick = { long -> textFieldState.updateValueBy(Increment.getDistance(long)) },
        onMinusClick = { long -> textFieldState.updateValueBy(-Increment.getDistance(long)) },
        hint = stringResource(R.string.exercise_set_input_distance),
        suffix = unit.prettyString(),
        modifier = modifier,
    )
}

@Composable
private fun CaloriesInput(textFieldState: DoubleTextFieldState, modifier: Modifier = Modifier) {
    NumberInput(
        textFieldState = textFieldState,
        onPlusClick = { long -> textFieldState.updateValueBy(Increment.getCalories(long)) },
        onMinusClick = { long -> textFieldState.updateValueBy(-Increment.getCalories(long)) },
        hint = stringResource(R.string.exercise_set_input_calories),
        suffix = stringResource(R.string.energy_unit_kcal),
        modifier = modifier,
    )
}

@Composable
private fun SetEditorPreview(editableExerciseSet: EditableExerciseSet) {
    LiftAppTheme {
        Surface {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(LocalDimens.current.padding.itemVertical),
                modifier = Modifier.padding(16.dp),
            ) {
                SetEditorContent(editableExerciseSet = editableExerciseSet)
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun SetEditorWeightExercisePreview() {
    val textFieldStateManager = textFieldStateManager()
    SetEditorPreview(
        editableExerciseSet =
            EditableExerciseSet.Weight(
                weight = 100.0,
                reps = 10,
                weightInput = textFieldStateManager.doubleTextField(initialValue = "100"),
                repsInput = textFieldStateManager.intTextField(initialValue = "10"),
                weightUnit = MassUnit.Kilograms,
            )
    )
}

@LightAndDarkThemePreview
@Composable
private fun SetEditorTimeExercisePreview() {
    val textFieldStateManager = textFieldStateManager()
    SetEditorPreview(
        editableExerciseSet =
            EditableExerciseSet.Cardio(
                duration = 1.hours + 30.minutes + 15.seconds,
                durationInput = textFieldStateManager.longTextField(),
                distance = 5.0,
                distanceInput = textFieldStateManager.doubleTextField(initialValue = "5"),
                distanceUnit = LongDistanceUnit.Kilometer,
                kcal = 458.0,
                kcalInput = textFieldStateManager.doubleTextField(initialValue = "458"),
            )
    )
}
