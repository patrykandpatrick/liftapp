package com.patrykandpatrick.liftapp.core.chart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.date.displayDateInterval
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.domain.date.DateInterval
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.ArrowForward
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

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
            Icon(LiftAppIcons.ArrowBack, null)
        }

        Text(
            text = dateInterval.displayDateInterval(),
            style = MaterialTheme.typography.titleSmall,
            color = colorScheme.foregroundVariant,
        )

        LiftAppIconButton(onClick = incrementDateInterval, enabled = dateInterval.isIncrementable) {
            Icon(LiftAppIcons.ArrowForward, null)
        }
    }
}

object DateIntervalControllerDefaults {
    /** The distance from the controller's touch-target bounds to its visible 24 dp icons. */
    val visibleContentVerticalInset = 12.dp
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
