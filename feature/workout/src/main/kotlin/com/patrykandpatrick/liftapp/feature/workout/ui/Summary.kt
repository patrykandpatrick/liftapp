package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.model.getDisplayName
import com.patrykandpatrick.liftapp.core.text.LocalMarkupProcessor
import com.patrykandpatrick.liftapp.core.text.TextFieldState
import com.patrykandpatrick.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitle
import com.patrykandpatrick.liftapp.core.ui.ListSectionTitleDefaults
import com.patrykandpatrick.liftapp.core.ui.button.OnFocusChanged
import com.patrykandpatrick.liftapp.core.ui.input.DateInput
import com.patrykandpatrick.liftapp.core.ui.input.TimeInput
import com.patrykandpatrick.liftapp.feature.workout.model.Action
import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout
import com.patrykandpatrick.liftapp.feature.workout.model.WorkoutPage
import com.patrykandpatrick.liftapp.feature.workout.model.prettyString
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
internal fun Summary(
    summary: WorkoutPage.Summary,
    onAction: (Action) -> Unit,
    listState: LazyListState,
    isRestTimerVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        state = listState,
        contentPadding =
            PaddingValues(
                top = 8.dp,
                bottom =
                    dimens.screen.padding +
                        if (isRestTimerVisible) RestTimerContainerHeight else 0.dp,
            ),
        modifier = modifier,
    ) {
        item(contentType = "details") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Name(
                    name = summary.name,
                    onNameSelected = { onAction(Action.UpdateWorkoutName(it)) },
                )
                StartDateTime(
                    startDate = summary.startDate,
                    startTime = summary.startTime,
                    is24H = summary.is24H,
                    onDateTimeSelected = { startDate, startTime ->
                        onAction(Action.UpdateWorkoutStartDateTime(startDate, startTime))
                    },
                )
                EndDateTime(
                    endDate = summary.endDate,
                    endTime = summary.endTime,
                    is24H = summary.is24H,
                    onDateTimeSelected = { endDate, endTime ->
                        onAction(Action.UpdateWorkoutEndDateTime(endDate, endTime))
                    },
                )
                Notes(
                    notes = summary.notes,
                    onNotesSelected = { onAction(Action.UpdateWorkoutNotes(it)) },
                )
            }
        }

        item(contentType = "section_title") {
            ListSectionTitle(
                title = stringResource(R.string.workout_summary_exercises),
                inset = ListSectionTitleDefaults.Inset.Screen,
                spacing = ListSectionTitleDefaults.Spacing.Section,
            )
        }

        itemsIndexed(
            items = summary.exercises,
            key = { _, it -> it.id },
            contentType = { _, _ -> "exercise" },
        ) { index, exercise ->
            Exercise(
                index = index,
                exercise = exercise,
            )
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
        modifier = modifier.padding(horizontal = 16.dp),
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        DateInput(
            date = startDate,
            label = stringResource(R.string.workout_summary_edit_workout_start_date),
            onDateSelected = { onDateTimeSelected(it, startTime) },
            modifier = Modifier.weight(2f),
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
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        DateInput(
            date = endDate,
            label = stringResource(R.string.workout_summary_edit_workout_end_date),
            onDateSelected = { onDateTimeSelected(it, endTime) },
            modifier = Modifier.weight(2f),
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
        label = { Text(stringResource(R.string.generic_notes)) },
        minLines = 3,
        maxLines = 5,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        interactionSource = interactionSource,
        modifier = modifier.padding(horizontal = 16.dp),
    )

    interactionSource.OnFocusChanged { isFocused -> if (!isFocused) onNotesSelected(notes) }
}

@Composable
private fun Exercise(
    index: Int,
    exercise: EditableWorkout.Exercise,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current.density
    val titleHeight = remember { mutableIntStateOf(0) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top,
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.height((titleHeight.intValue / density).toInt().dp),
        ) {
            Text(text = "${index + 1}", style = MaterialTheme.typography.titleSmall)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name.getDisplayName(),
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
                color = colorScheme.foreground,
                modifier = Modifier.onGloballyPositioned { titleHeight.intValue = it.size.height },
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
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
                        color = colorScheme.foregroundVariant,
                    )
                }
            }
        }
    }
}
