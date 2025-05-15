package com.patrykandpatrick.liftapp.feature.workout.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.time.formattedRemainingTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@Composable
fun RestTimer(
    remainingDuration: Duration,
    isPaused: Boolean,
    onToggleIsPaused: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current
    val shape = MaterialTheme.shapes.small

    Row(
        horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontalSmall),
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(
                    width = dimens.strokeWidth,
                    color = MaterialTheme.colorScheme.primary,
                    shape = shape,
                )
                .padding(dimens.padding.itemHorizontal, dimens.padding.itemVertical),
    ) {
        OutlinedButton(onClick = onCancel, contentPadding = PaddingValues(8.dp)) {
            Icon(painter = painterResource(R.drawable.ic_close), contentDescription = null)
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(0.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = stringResource(R.string.rest_timer_counter_title),
                style = MaterialTheme.typography.titleSmall,
            )

            Text(
                text = remainingDuration.formattedRemainingTime,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Button(onClick = onToggleIsPaused, contentPadding = PaddingValues(8.dp)) {
            Icon(
                painter =
                    painterResource(id = if (isPaused) R.drawable.ic_play else R.drawable.ic_pause),
                contentDescription = null,
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun RestTimerPreview() {
    LiftAppTheme {
        Surface {
            RestTimer(
                remainingDuration = 1.minutes + 30.seconds,
                isPaused = true,
                onToggleIsPaused = {},
                onCancel = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
