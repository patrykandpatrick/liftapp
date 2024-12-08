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
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun DurationPicker(
    duration: Duration,
    onDurationChange: (Duration) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val timeFormat = remember { DecimalFormat("00") }
    val allHours = remember { List(60) { it } }
    val allMinutes = remember { List(60) { it } }
    val allSeconds = remember { List(60) { it } }
    val (hours, minutes, seconds) =
        duration.toComponents { _, hours, minutes, seconds, _ -> Triple(hours, minutes, seconds) }
    val hourState = rememberWheelPickerState(initialSelectedIndex = hours)
    val minuteState = rememberWheelPickerState(initialSelectedIndex = minutes)
    val secondState = rememberWheelPickerState(initialSelectedIndex = seconds)
    val onTimeChangeState = rememberUpdatedState(onDurationChange)

    val onItemSelected = remember {
        { _: Int ->
            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
            onTimeChangeState.value(
                calculateDuration(
                    hour = allHours[hourState.currentItem],
                    minute = allMinutes[minuteState.currentItem],
                    second = allSeconds[secondState.currentItem],
                )
            )
        }
    }

    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier.fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = .4f),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(16.dp)
                    .align(Alignment.Center)
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            WheelPicker(state = hourState, onItemSelected = onItemSelected, itemExtent = 1) {
                allHours.forEach { value -> WheelPickerItem(timeFormat.format(value)) }
            }

            Text(
                text = ":",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.offset(y = (-1).dp),
            )

            WheelPicker(state = minuteState, onItemSelected = onItemSelected, itemExtent = 1) {
                allMinutes.forEach { value -> WheelPickerItem(timeFormat.format(value)) }
            }

            Text(
                text = ":",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.offset(y = (-1).dp),
            )

            WheelPicker(state = secondState, onItemSelected = onItemSelected, itemExtent = 1) {
                allSeconds.forEach { index -> WheelPickerItem(timeFormat.format(index)) }
            }
        }
    }
}

private fun calculateDuration(hour: Int?, minute: Int?, second: Int?): Duration {
    val h = (hour ?: 0).hours
    val m = (minute ?: 0).minutes
    val s = (second ?: 0).seconds
    return h + m + s
}

@LightAndDarkThemePreview
@Composable
private fun DurationPickerPreview() {
    LiftAppTheme {
        Surface {
            val duration = remember { mutableStateOf(11.hours + 30.minutes) }
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                Text(text = "Picked duration = ${duration.value}")
                HorizontalDivider()
                DurationPicker(
                    duration = duration.value,
                    onDurationChange = { duration.value = it },
                )
            }
        }
    }
}
