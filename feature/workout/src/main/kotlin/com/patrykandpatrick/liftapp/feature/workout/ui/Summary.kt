package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutPage
import com.patrykandpatrick.liftapp.feature.workout.model.prettyString
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.model.getDisplayName
import com.patrykandpatryk.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.ListSectionTitle
import com.patrykandpatryk.liftapp.core.ui.button.OnFocusChanged
import com.patrykandpatryk.liftapp.core.ui.input.DateInput
import com.patrykandpatryk.liftapp.core.ui.input.TimeInput
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun Summary(
    summary: WorkoutPage.Summary,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    LazyColumn(
        contentPadding = PaddingValues(vertical = dimens.padding.contentVertical),
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
        modifier = modifier,
    ) {
        item(contentType = "name") {
            Name(name = summary.name, onNameSelected = { onAction(Action.UpdateWorkoutName(it)) })
        }

        item(contentType = "date_time") {
            StartDateTime(
                startDate = summary.startDate,
                startTime = summary.startTime,
                is24H = summary.is24H,
                onDateTimeSelected = { startDate, startTime ->
                    onAction(Action.UpdateWorkoutStartDateTime(startDate, startTime))
                },
            )
        }

        item(contentType = "date_time") {
            EndDateTime(
                endDate = summary.endDate,
                endTime = summary.endTime,
                is24H = summary.is24H,
                onDateTimeSelected = { endDate, endTime ->
                    onAction(Action.UpdateWorkoutEndDateTime(endDate, endTime))
                },
            )
        }

        item(contentType = "notes") {
            Notes(
                notes = summary.notes,
                onNotesSelected = { onAction(Action.UpdateWorkoutNotes(it)) },
            )
        }

        item(contentType = "section_title") {
            ListSectionTitle(stringResource(R.string.workout_summary_exercises))
        }

        itemsIndexed(
            items = summary.exercises,
            key = { _, it -> it.id },
            contentType = { _, _ -> "exercise" },
        ) { index, exercise ->
            Exercise(index, exercise)
        }
    }
}

@Composable
private fun Name(
    name: TextFieldState<String>,
    onNameSelected: (TextFieldState<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    LiftAppTextFieldWithSupportingText(
        textFieldState = name,
        label = { Text(stringResource(R.string.workout_summary_edit_workout_name)) },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        interactionSource = interactionSource,
        modifier = modifier.padding(horizontal = LocalDimens.current.padding.itemHorizontal),
    )

    interactionSource.OnFocusChanged { isFocused -> if (!isFocused) onNameSelected(name) }
}

@Composable
private fun StartDateTime(
    startDate: TextFieldState<LocalDate>,
    startTime: TextFieldState<LocalTime>,
    is24H: Boolean,
    onDateTimeSelected: (TextFieldState<LocalDate>, TextFieldState<LocalTime>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontal),
        modifier = modifier.padding(horizontal = LocalDimens.current.padding.itemHorizontal),
    ) {
        DateInput(
            date = startDate,
            label = stringResource(R.string.workout_summary_edit_workout_start_date),
            onDateSelected = { onDateTimeSelected(it, startTime) },
            modifier = Modifier.weight(1f),
        )

        TimeInput(
            time = startTime,
            is24H = is24H,
            label = stringResource(R.string.workout_summary_edit_workout_start_time),
            onTimeSelected = { onDateTimeSelected(startDate, it) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun EndDateTime(
    endDate: TextFieldState<LocalDate>,
    endTime: TextFieldState<LocalTime>,
    is24H: Boolean,
    onDateTimeSelected: (TextFieldState<LocalDate>, TextFieldState<LocalTime>) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontal),
        modifier = modifier.padding(horizontal = LocalDimens.current.padding.itemHorizontal),
    ) {
        DateInput(
            date = endDate,
            label = stringResource(R.string.workout_summary_edit_workout_end_date),
            onDateSelected = { onDateTimeSelected(it, endTime) },
            modifier = Modifier.weight(1f),
        )

        TimeInput(
            time = endTime,
            is24H = is24H,
            label = stringResource(R.string.workout_summary_edit_workout_end_time),
            onTimeSelected = { onDateTimeSelected(endDate, it) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun Notes(
    notes: TextFieldState<String>,
    onNotesSelected: (TextFieldState<String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    LiftAppTextFieldWithSupportingText(
        textFieldState = notes,
        label = { Text(stringResource(R.string.workout_summary_edit_workout_notes)) },
        minLines = 3,
        maxLines = 5,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        interactionSource = interactionSource,
        modifier = modifier.padding(horizontal = LocalDimens.current.padding.itemHorizontal),
    )

    interactionSource.OnFocusChanged { isFocused -> if (!isFocused) onNotesSelected(notes) }
}

@Composable
private fun Exercise(
    index: Int,
    exercise: EditableWorkout.Exercise,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    val density = LocalDensity.current.density
    val titleHeight = remember { mutableIntStateOf(0) }
    ListItem(
        icon = {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.height((titleHeight.intValue / density).toInt().dp)
                        .align(Alignment.Top),
            ) {
                Text(text = "${index + 1}", style = MaterialTheme.typography.titleSmall)
            }
        },
        title = {
            Text(
                text = exercise.name.getDisplayName(),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                modifier = Modifier.onGloballyPositioned { titleHeight.intValue = it.size.height },
            )
        },
        description = {
            Column(
                verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVerticalSmall),
                modifier = Modifier.padding(top = 12.dp),
            ) {
                exercise.sets.forEachIndexed { setIndex, set ->
                    Text(
                        text =
                            LocalMarkupProcessor.current.toAnnotatedString(
                                stringResource(
                                    R.string.workout_exercise_set_info,
                                    setIndex + 1,
                                    set.prettyString(),
                                )
                            ),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        },
        modifier = modifier,
        paddingValues =
            PaddingValues(
                horizontal = dimens.padding.itemHorizontal,
                vertical = dimens.padding.itemVerticalSmall,
            ),
    )
}
