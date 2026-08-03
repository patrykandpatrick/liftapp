package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun DayIndicator(dayIndex: Int, modifier: Modifier = Modifier, highlighted: Boolean = true) {
    val colors = colorScheme
    val selectedBackground = colors.primaryDisabled.compositeOver(colors.surface)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .then(
                    if (highlighted) {
                        Modifier.background(color = selectedBackground, shape = PillShape)
                            .border(width = 1.dp, color = colors.primary, shape = PillShape)
                    } else {
                        Modifier.border(width = 1.dp, color = colors.outline, shape = PillShape)
                    }
                )
                .padding(horizontal = 8.dp, vertical = 10.dp),
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.foreground) {
            Text(text = "${dayIndex + 1}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.training_plan_item_day_indicator_label),
                color = colors.foregroundVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun DayIndicatorPreview() {
    LiftAppTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                repeat(3) { DayIndicator(dayIndex = it) }
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun DisabledDayIndicatorPreview() {
    LiftAppTheme {
        Surface {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                repeat(3) { DayIndicator(dayIndex = it, highlighted = false) }
            }
        }
    }
}
