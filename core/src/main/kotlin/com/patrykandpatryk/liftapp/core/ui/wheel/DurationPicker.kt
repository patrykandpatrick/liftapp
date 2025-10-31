package com.patrykandpatryk.liftapp.core.ui.wheel

import android.icu.text.DecimalFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.swmansion.kmpwheelpicker.WheelPicker
import com.swmansion.kmpwheelpicker.rememberWheelPickerState
import kotlin.math.round
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

private val windowHeight = 44.dp

private const val BUFFER_SIZE = 2

@Composable
fun DurationPicker(
    duration: Duration,
    onDurationChange: (Duration) -> Unit,
    includeHours: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val timeFormat = remember { DecimalFormat("00") }
    val (hours, minutes, seconds) =
        duration.toComponents { _, hours, minutes, seconds, _ -> Triple(hours, minutes, seconds) }
    val hourState = rememberWheelPickerState(itemCount = 60, initialIndex = hours)
    val minuteState = rememberWheelPickerState(itemCount = 60, initialIndex = minutes)
    val secondState = rememberWheelPickerState(itemCount = 60, initialIndex = seconds)
    val onTimeChangeState = rememberUpdatedState(onDurationChange)

    LaunchedEffect(hours, minutes, seconds) {
        if (hours != hourState.index) {
            launch(NonCancellable) { hourState.animateScrollTo(hours) }
        }
        if (minutes != minuteState.index) {
            launch(NonCancellable) { minuteState.animateScrollTo(minutes) }
        }
        if (seconds != secondState.index) {
            launch(NonCancellable) { secondState.animateScrollTo(seconds) }
        }
    }

    LaunchedEffect(round(hourState.value), round(minuteState.value), round(secondState.value)) {
        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    LaunchedEffect(hourState.index, minuteState.index, secondState.index) {
        val newDuration = calculateDuration(hourState.index, minuteState.index, secondState.index)
        if (newDuration != duration) onTimeChangeState.value(newDuration)
    }

    Box(modifier.height((2 * BUFFER_SIZE - 1) * windowHeight)) {
        WheelPickerWindow(Modifier.fillMaxWidth().height(windowHeight).align(Alignment.Center))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (includeHours) {
                WheelPicker(state = hourState, bufferSize = BUFFER_SIZE) { index ->
                    WheelPickerItem(timeFormat.format(index), index, hourState.value, BUFFER_SIZE)
                }

                Text(
                    text = stringResource(R.string.time_hours_medium),
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.offset(y = (-1).dp),
                )
            }

            WheelPicker(state = minuteState, bufferSize = BUFFER_SIZE) { index ->
                WheelPickerItem(timeFormat.format(index), index, minuteState.value, BUFFER_SIZE)
            }

            Text(
                text = stringResource(R.string.time_minutes_medium),
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.offset(y = (-1).dp),
            )

            WheelPicker(state = secondState, bufferSize = BUFFER_SIZE) { index ->
                WheelPickerItem(timeFormat.format(index), index, secondState.value, BUFFER_SIZE)
            }

            Text(
                text = stringResource(R.string.time_seconds_medium),
                style = MaterialTheme.typography.bodyLarge,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.offset(y = (-1).dp),
            )
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
                LiftAppHorizontalDivider()
                DurationPicker(
                    duration = duration.value,
                    onDurationChange = { duration.value = it },
                )
            }
        }
    }
}
