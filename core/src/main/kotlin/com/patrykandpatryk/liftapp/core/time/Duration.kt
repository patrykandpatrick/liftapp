package com.patrykandpatryk.liftapp.core.time

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import java.text.SimpleDateFormat
import kotlin.time.Duration

@Stable
@Composable
fun Duration.getShortFormattedTime(): String = toComponents { hours, minutes, seconds, _ ->
    buildString {
        if (hours > 0) {
            append("$hours ${stringResource(R.string.time_hours_short)}")
        }
        if (minutes > 0) {
            if (!isEmpty()) append(" ")
            append("$minutes ${stringResource(R.string.time_minutes_short)}")
        }
        if (seconds > 0) {
            if (!isEmpty()) append(" ")
            append("$seconds ${stringResource(R.string.time_seconds_short)}")
        }
    }
}

@SuppressLint("SimpleDateFormat") private val formatter = SimpleDateFormat("m:ss")

val Duration.formattedRemainingTime: String
    get() = formatter.format(inWholeMilliseconds)
