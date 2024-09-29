package com.patrykandpatryk.liftapp.core.ui.wheel

import android.icu.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.time.TimeOfDay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun TimePicker(
    hour: Int?,
    minute: Int?,
    second: Int?,
    is24h: Boolean,
    onTimeChange: (Duration) -> Unit,
    modifier: Modifier = Modifier,
    timeOfDay: TimeOfDay = TimeOfDay.AM,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val timeFormat = remember { DecimalFormat("00") }
    val hours = remember { List(if (is24h) 24 else 13) { it } }
    val minutes = remember { List(60) { it } }
    val seconds = remember { List(60) { it } }
    val timeOfDays = TimeOfDay.entries
    val hourState = rememberWheelPickerState(selectedIndex = hour ?: 0)
    val minuteState = rememberWheelPickerState(selectedIndex = minute ?: 0)
    val secondState = rememberWheelPickerState(selectedIndex = second ?: 0)
    val timeOfDayState = rememberWheelPickerState(selectedIndex = timeOfDays.indexOf(timeOfDay))
    val onTimeChangeState = rememberUpdatedState(onTimeChange)

    val onItemSelected = remember {
        { _: Int ->
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onTimeChangeState.value(
                calculateDuration(
                    hour = hours[hourState.selectedIndex - if (is24h || timeOfDay == TimeOfDay.AM) 0 else 12],
                    minute = minutes[minuteState.selectedIndex],
                    second = seconds[secondState.selectedIndex],
                    is24h = is24h,
                    timeOfDay = timeOfDays[timeOfDayState.selectedIndex]
                )
            )
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp),
                )
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .4f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
                .align(Alignment.Center)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (hour != null) {
                WheelPicker(
                    state = hourState,
                    onItemSelected = onItemSelected,
                ) {
                    hours.forEach { value ->
                        WheelPickerItem(timeFormat.format(value))
                    }
                }
            }

            if (hour != null && minute != null) {
                Text(
                    text = ":",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.offset(y = (-1).dp),
                )
            }

            if (minute != null) {
                WheelPicker(
                    state = minuteState,
                    onItemSelected = onItemSelected,
                ) {
                    minutes.forEach { value ->
                        WheelPickerItem(timeFormat.format(value))
                    }
                }
            }

            if (minute != null && second != null) {
                Text(
                    text = ":",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.offset(y = (-1).dp),
                )
            }

            if (second != null) {
                WheelPicker(
                    state = secondState,
                    onItemSelected = onItemSelected,
                ) {
                    seconds.forEach { index ->
                        WheelPickerItem(timeFormat.format(index))
                    }
                }
            }

            if (hour != null && !is24h) {
                WheelPicker(
                    state = timeOfDayState,
                    onItemSelected = onItemSelected,
                ) {
                    TimeOfDay.entries.forEach { timeOfDay ->
                        val text = when (timeOfDay) {
                            TimeOfDay.AM -> stringResource(R.string.picker_time_am)
                            TimeOfDay.PM -> stringResource(R.string.picker_time_pm)
                        }
                        WheelPickerItem(value = text)
                    }
                }
            }
        }
    }
}

private fun calculateDuration(
    hour: Int?,
    minute: Int?,
    second: Int?,
    is24h: Boolean,
    timeOfDay: TimeOfDay,
): Duration {
    val h = (hour?.plus(if (is24h || timeOfDay == TimeOfDay.AM) 0 else 12) ?: 0).hours
    val m = (minute ?: 0).minutes
    val s = (second ?: 0).seconds
    return h + m + s
}

@Composable
private fun TimePickerPreview(is24h: Boolean) {
    LiftAppTheme {
        Surface {
            val duration = remember { mutableStateOf(11.hours + 30.minutes) }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "Picked duration = ${duration.value}")
                HorizontalDivider()
                duration.value.toComponents { _, hours, minutes, seconds, _ ->
                    TimePicker(
                        hour = hours,
                        minute = minutes,
                        second = seconds,
                        is24h = is24h,
                        onTimeChange = { duration.value = it },
                    )
                }

            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun TimePickerPreview24H() {
    TimePickerPreview(true)
}

@LightAndDarkThemePreview
@Composable
private fun TimePickerPreview12H() {
    TimePickerPreview(false)
}