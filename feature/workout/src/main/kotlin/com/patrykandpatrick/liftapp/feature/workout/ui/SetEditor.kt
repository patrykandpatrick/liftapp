package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.feature.workout.model.EditableExerciseSet
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.exercise.prettyString
import com.patrykandpatryk.liftapp.core.extension.prettyString
import com.patrykandpatryk.liftapp.core.preview.PreviewResource.textFieldStateManager
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.text.DoubleTextFieldState
import com.patrykandpatryk.liftapp.core.text.IntTextFieldState
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.text.LongTextFieldState
import com.patrykandpatryk.liftapp.core.text.updateValueBy
import com.patrykandpatryk.liftapp.core.ui.InfoCard
import com.patrykandpatryk.liftapp.core.ui.InputFieldLayout
import com.patrykandpatryk.liftapp.core.ui.SupportingText
import com.patrykandpatryk.liftapp.core.ui.input.InputSuggestion
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.core.ui.wheel.DurationPicker
import com.patrykandpatryk.liftapp.domain.Constants.Input.Increment
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun <T : ExerciseSet> ColumnScope.SetEditorContent(
    editableExerciseSet: EditableExerciseSet<T>
) {
    if (editableExerciseSet is EditableExerciseSet.Calisthenics) {
        BodyWeightInfo(bodyWeight = editableExerciseSet.formattedBodyWeight)
    }

    if (editableExerciseSet.suggestions.isNotEmpty()) {
        Suggestions(editableExerciseSet.suggestions, editableExerciseSet::applySet)
    }

    when (editableExerciseSet) {
        is EditableExerciseSet.Weight -> {
            WeightInput(
                textFieldState = editableExerciseSet.weightInput,
                unit = editableExerciseSet.weightUnit,
            )
            RepsInput(textFieldState = editableExerciseSet.repsInput)
        }

        is EditableExerciseSet.Calisthenics -> {
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
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
    )
}

@Composable
private fun <T : ExerciseSet> Suggestions(
    suggestions: List<EditableExerciseSet.SetSuggestion<T>>,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_suggestion),
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )

            Text(text = "Smart suggestions", style = MaterialTheme.typography.titleSmall)
        }

        Row(
            modifier =
                Modifier.padding(top = dimens.padding.itemVerticalSmall)
                    .horizontalScroll(rememberScrollState()),
            horizontalArrangement =
                Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontalSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            suggestions.forEach { suggestion ->
                InputSuggestion(suggestion = suggestion, onClick = onClick)
            }
        }
    }
}

@Composable
private fun <T : ExerciseSet> InputSuggestion(
    suggestion: EditableExerciseSet.SetSuggestion<T>,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    InputSuggestion(
        text = suggestion.set.prettyString(),
        label =
            when (suggestion.type) {
                EditableExerciseSet.SetSuggestion.Type.PreviousSet ->
                    stringResource(R.string.workout_set_suggestion_previous_set)

                EditableExerciseSet.SetSuggestion.Type.PreviousWorkout ->
                    stringResource(R.string.workout_set_suggestion_previous_workout)
            },
        onClick = { onClick(suggestion.set) },
        modifier = modifier,
    )
}

@Composable
private fun BodyWeightInfo(bodyWeight: String, modifier: Modifier = Modifier) {
    InfoCard(
        text =
            LocalMarkupProcessor.current.toAnnotatedString(
                stringResource(R.string.workout_calisthenics_set_body_weight, bodyWeight)
            ),
        modifier = modifier,
    )
}

@Composable
private fun RepsInput(textFieldState: IntTextFieldState, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    NumberInput(
        textFieldState = textFieldState,
        onPlusClick = { long -> textFieldState.updateValueBy(Increment.getReps(long)) },
        onMinusClick = { long -> textFieldState.updateValueBy(-Increment.getReps(long)) },
        hint = stringResource(R.string.exercise_set_input_reps),
        modifier = modifier,
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
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
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
    )
}

@Composable
private fun CaloriesInput(textFieldState: DoubleTextFieldState, modifier: Modifier = Modifier) {
    val focusManager = LocalFocusManager.current
    NumberInput(
        textFieldState = textFieldState,
        onPlusClick = { long -> textFieldState.updateValueBy(Increment.getCalories(long)) },
        onMinusClick = { long -> textFieldState.updateValueBy(-Increment.getCalories(long)) },
        hint = stringResource(R.string.exercise_set_input_calories),
        suffix = stringResource(R.string.energy_unit_kcal),
        modifier = modifier,
        keyboardOptions =
            KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
    )
}

@Composable
private fun <T : ExerciseSet> SetEditorPreview(editableExerciseSet: EditableExerciseSet<T>) {
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
    PreviewTheme {
        val textFieldStateManager = textFieldStateManager()
        SetEditorPreview(
            editableExerciseSet =
                EditableExerciseSet.Weight(
                    weight = 100.0,
                    reps = 10,
                    weightInput = textFieldStateManager.doubleTextField(initialValue = "100"),
                    repsInput = textFieldStateManager.intTextField(initialValue = "10"),
                    weightUnit = MassUnit.Kilograms,
                    suggestions =
                        listOf(
                            EditableExerciseSet.SetSuggestion(
                                ExerciseSet.Weight(100.0, 12, MassUnit.Kilograms),
                                EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                            )
                        ),
                )
        )
    }
}

@LightAndDarkThemePreview
@Composable
private fun SetEditorCalisthenicsExercisePreview() {
    PreviewTheme {
        val textFieldStateManager = textFieldStateManager()
        SetEditorPreview(
            editableExerciseSet =
                EditableExerciseSet.Calisthenics(
                    bodyWeight = 80.0,
                    weight = 20.0,
                    reps = 10,
                    formattedBodyWeight = "80 kg",
                    weightInput = textFieldStateManager.doubleTextField(initialValue = "20"),
                    repsInput = textFieldStateManager.intTextField(initialValue = "10"),
                    weightUnit = MassUnit.Kilograms,
                    suggestions =
                        listOf(
                            EditableExerciseSet.SetSuggestion(
                                ExerciseSet.Calisthenics(20.0, 80.0, 10, MassUnit.Kilograms),
                                EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                            )
                        ),
                )
        )
    }
}

@LightAndDarkThemePreview
@Composable
private fun SetEditorTimeExercisePreview() {
    PreviewTheme {
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
                    suggestions =
                        listOf(
                            EditableExerciseSet.SetSuggestion(
                                ExerciseSet.Cardio(
                                    1.hours + 30.minutes + 10.seconds,
                                    4.75,
                                    432.0,
                                    LongDistanceUnit.Kilometer,
                                ),
                                EditableExerciseSet.SetSuggestion.Type.PreviousSet,
                            )
                        ),
                )
        )
    }
}
