package com.patrykandpatrick.liftapp.core.ui.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.text.TextFieldState
import com.patrykandpatrick.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatrick.liftapp.core.ui.button.OnClick
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import java.time.LocalTime

@Composable
fun TimeInput(
    time: TextFieldState<LocalTime>,
    is24H: Boolean,
    label: String,
    modifier: Modifier = Modifier,
    onTimeSelected: ((TextFieldState<LocalTime>) -> Unit)? = null,
) {
    val (showTimePicker, setShowTimePicker) = remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(time.value.hour, time.value.minute, is24H)

    val interactionSource = remember { MutableInteractionSource() }

    LiftAppTextFieldWithSupportingText(
        textFieldState = time,
        readOnly = true,
        interactionSource = interactionSource,
        label = { Text(label) },
        modifier = modifier,
    )

    interactionSource.OnClick {
        timePickerState.hour = time.value.hour
        timePickerState.minute = time.value.minute
        setShowTimePicker(true)
    }

    if (showTimePicker) {
        val dimens = LocalDimens.current

        DatePickerDialog(
            onDismissRequest = { setShowTimePicker(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        setShowTimePicker(false)
                        time.updateValue(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        onTimeSelected?.invoke(time)
                    },
                    modifier = modifier.padding(end = 8.dp),
                ) {
                    Text(stringResource(R.string.workout_summary_edit_picker_confirm))
                }
            },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            ) {
                Text(
                    text = stringResource(R.string.workout_summary_edit_workout_start_time),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier =
                        Modifier.padding(
                            16.dp,
                            16.dp,
                        ),
                )

                TimePicker(timePickerState)
            }
        }
    }
}
