package com.patrykandpatrick.liftapp.newplan.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun DayIndicator(dayIndex: Int, modifier: Modifier = Modifier, enabled: Boolean = true) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .then(
                    if (enabled) {
                        Modifier.background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = shape,
                        )
                    } else {
                        Modifier.border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            shape = shape,
                        )
                    }
                )
                .padding(
                    horizontal = LocalDimens.current.padding.itemHorizontalSmall,
                    vertical = LocalDimens.current.padding.itemVerticalSmall,
                ),
    ) {
        val contentColor =
            if (enabled) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurface
            }

        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Text(text = "${dayIndex + 1}", style = MaterialTheme.typography.titleMedium)
            Text(
                text = stringResource(R.string.training_plan_item_day_indicator_label),
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
                repeat(3) { DayIndicator(dayIndex = it, enabled = false) }
            }
        }
    }
}
