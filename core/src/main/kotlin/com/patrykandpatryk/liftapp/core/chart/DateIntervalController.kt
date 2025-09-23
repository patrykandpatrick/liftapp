package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.date.displayDateInterval
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.domain.date.DateInterval

@Composable
fun DateIntervalController(
    dateInterval: DateInterval,
    incrementDateInterval: () -> Unit,
    decrementDateInterval: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth(),
    ) {
        LiftAppIconButton(onClick = decrementDateInterval) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
        }

        Text(
            text = dateInterval.displayDateInterval(),
            style = MaterialTheme.typography.titleSmall,
            color = colorScheme.onSurfaceVariant,
        )

        LiftAppIconButton(onClick = incrementDateInterval, enabled = dateInterval.isIncrementable) {
            Icon(Icons.AutoMirrored.Rounded.ArrowForward, null)
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun DateIntervalControllerPreview() {
    PreviewTheme {
        DateIntervalController(
            dateInterval = DateInterval.RollingWeek(),
            incrementDateInterval = {},
            decrementDateInterval = {},
        )
    }
}
