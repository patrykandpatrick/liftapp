package com.patrykandpatrick.liftapp.core.ui.input

import android.icu.util.Calendar
import android.icu.util.TimeZone
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.text.TextFieldState
import com.patrykandpatrick.liftapp.core.ui.LiftAppTextFieldWithSupportingText
import com.patrykandpatrick.liftapp.core.ui.button.OnClick
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

object DateInputDefaults {
    val SelectableDatesNowAndPast =
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis <= Calendar.getInstance(TimeZone.GMT_ZONE).timeInMillis
        }

    val SelectableDatesNowAndFuture =
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean =
                utcTimeMillis >=
                    LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        }
}

@Composable
fun DateInput(
    date: TextFieldState<LocalDate>,
    label: String,
    modifier: Modifier = Modifier,
    selectableDates: SelectableDates = DateInputDefaults.SelectableDatesNowAndPast,
    onDateSelected: ((TextFieldState<LocalDate>) -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val (showDatePicker, setShowDatePicker) = remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis =
                date.value.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
            yearRange = 2000..LocalDate.now().year,
            selectableDates = selectableDates,
        )
    val interactionSource = remember { MutableInteractionSource() }

    LiftAppTextFieldWithSupportingText(
        textFieldState = date,
        readOnly = true,
        interactionSource = interactionSource,
        label = { Text(label) },
        trailingIcon = trailingIcon,
        modifier = modifier,
    )

    interactionSource.OnClick { setShowDatePicker(true) }

    if (showDatePicker) {
        val dimens = LocalDimens.current

        DatePickerDialog(
            onDismissRequest = { setShowDatePicker(false) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateMillis =
                            datePickerState.selectedDateMillis ?: return@TextButton
                        setShowDatePicker(false)
                        date.updateValue(
                            Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                        onDateSelected?.invoke(date)
                    },
                    enabled = datePickerState.selectedDateMillis != null,
                    modifier = modifier.padding(end = 8.dp),
                ) {
                    Text(stringResource(R.string.workout_summary_edit_picker_confirm))
                }
            },
        ) {
            DatePicker(datePickerState)
        }
    }
}
