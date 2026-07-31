package com.patrykandpatrick.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.feature.dashboard.model.DashboardStatistics
import com.patrykandpatrick.liftapp.ui.VerticalGrid
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.time.Duration.Companion.minutes

/**
 * The arrangements match [Shortcuts] so that the two grids line up: same gutters, and the same cell
 * sizing, which keeps their column counts equal at every width.
 */
@Composable
internal fun Statistics(statistics: DashboardStatistics, modifier: Modifier = Modifier) {
    val formatter = LocalFormatter.current

    VerticalGrid(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(RowSpacing),
    ) {
        Item(
            value = formatter.formatWeight(statistics.volume, statistics.volumeUnit),
            label = stringResource(R.string.dashboard_statistics_lifted),
        )
        Item(
            value =
                formatter.formatNumber(statistics.reps, format = Formatter.NumberFormat.Integer),
            label = stringResource(R.string.dashboard_statistics_reps),
        )
        Item(
            value =
                formatter.formatNumber(
                    statistics.workouts,
                    format = Formatter.NumberFormat.Integer,
                ),
            label = stringResource(R.string.dashboard_statistics_workouts),
        )
        Item(
            value = formatter.formatDurationWithUnits(statistics.timeExercised),
            label = stringResource(R.string.dashboard_statistics_time_exercised),
        )
    }
}

/**
 * The cells carry no padding of their own: they are bare text, so the room between rows is what
 * separates them, and padding as well would leave the component looking hollow.
 */
private val RowSpacing = 24.dp

/** Matches the value and caption treatment used by the 1RM calculator. */
@Composable
private fun Item(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        LiftAppText(text = value, style = typography.headlineMedium)
        LiftAppText(
            text = label,
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
        )
    }
}

@LightAndDarkThemePreview
@Composable
private fun StatisticsPreview() {
    PreviewTheme {
        LiftAppBackground {
            Statistics(
                statistics =
                    DashboardStatistics(
                        volume = 12_480.0,
                        volumeUnit = MassUnit.Kilograms,
                        reps = 412,
                        workouts = 4,
                        timeExercised = 226.minutes,
                    ),
                modifier =
                    Modifier.padding(
                        horizontal = dimens.screen.horizontalPadding,
                        vertical = dimens.screen.verticalPadding,
                    ),
            )
        }
    }
}
