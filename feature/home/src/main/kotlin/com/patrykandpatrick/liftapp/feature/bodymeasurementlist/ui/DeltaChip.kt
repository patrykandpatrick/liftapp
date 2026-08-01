package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValueDisplay
import com.patrykandpatrick.liftapp.ui.component.LiftAppBadge
import com.patrykandpatrick.liftapp.ui.component.LiftAppBadgeDefaults
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.TrendingDown
import com.patrykandpatrick.liftapp.ui.icons.TrendingUp
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * The change since the previous entry. Direction is carried three ways — arrow, sign, and color —
 * because color alone excludes anyone who cannot separate the green from the red.
 */
@Composable
internal fun DeltaChip(
    delta: BodyMeasurementValueDisplay.Delta,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = LiftAppBadgeDefaults.contentPadding,
    iconSize: Dp = LiftAppBadgeDefaults.iconSize,
) {
    LiftAppBadge(
        label = delta.label,
        color = delta.direction.color,
        icon = delta.direction.icon,
        contentPadding = contentPadding,
        iconSize = iconSize,
        modifier = modifier,
    )
}

/**
 * Increases read green and decreases red, direction only. The app holds no goal for a measurement,
 * so it cannot say whether a given direction is the wanted one; the color marks which way the
 * number moved, not whether that is good news.
 */
internal val BodyMeasurementValueDisplay.Direction.color: Color
    @Composable
    get() =
        when (this) {
            BodyMeasurementValueDisplay.Direction.Up -> colorScheme.green
            BodyMeasurementValueDisplay.Direction.Down -> colorScheme.red
            BodyMeasurementValueDisplay.Direction.Unchanged -> colorScheme.onSurfaceVariant
        }

internal val BodyMeasurementValueDisplay.Direction.icon: ImageVector?
    get() =
        when (this) {
            BodyMeasurementValueDisplay.Direction.Up -> LiftAppIcons.TrendingUp
            BodyMeasurementValueDisplay.Direction.Down -> LiftAppIcons.TrendingDown
            BodyMeasurementValueDisplay.Direction.Unchanged -> null
        }
