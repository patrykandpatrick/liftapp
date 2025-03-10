package com.patrykandpatryk.liftapp.core.ui.input

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
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.ui.OutlinedTextField
import com.patrykandpatryk.liftapp.core.ui.button.OnClick
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
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

    OutlinedTextField(
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
                        setShowDatePicker(false)
                        date.updateValue(
                            Instant.ofEpochMilli(checkNotNull(datePickerState.selectedDateMillis))
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                        onDateSelected?.invoke(date)
                    },
                    modifier = modifier.padding(end = dimens.padding.itemHorizontalSmall),
                ) {
                    Text(stringResource(R.string.workout_summary_edit_picker_confirm))
                }
            },
        ) {
            DatePicker(datePickerState)
        }
    }
}
