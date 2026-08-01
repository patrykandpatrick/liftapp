package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.chart.BodyMeasurementChartColors
import com.patrykandpatrick.liftapp.core.chart.Sparkline
import com.patrykandpatrick.liftapp.ui.component.LiftAppBadgeDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppLinearProgressIndicator
import com.patrykandpatrick.liftapp.ui.component.LiftAppRatioBar
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.theme.Typography
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * A measurement at grid size. What fills the space under the value depends on what the measurement
 * has to say: a two-sided one compares its sides, since that comparison is the reason the type
 * exists and is otherwise invisible until the details screen; a percentage shows where it sits in
 * its range; anything else gets its trend line and change.
 */
@Composable
internal fun BodyMeasurementTile(
    item: BodyMeasurementListItem,
    onClick: () -> Unit,
    onAddEntryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (item.value == null) {
        EmptyBodyMeasurementTile(name = item.name, onClick = onAddEntryClick, modifier = modifier)
        return
    }

    LiftAppCard(
        onClick = onClick,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = TileContentPadding,
        modifier = modifier,
    ) {
        // A measurement's name is the body part alone — the section heading says they are
        // circumferences — which leaves the top line room for the change beside it.
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            LiftAppText(
                text = item.name,
                style = typography.labelMedium,
                color = colorScheme.onSurfaceVariant,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )

            item.value.delta?.let { delta ->
                DeltaChip(
                    delta = delta,
                    contentPadding = LiftAppBadgeDefaults.compactContentPadding,
                    iconSize = LiftAppBadgeDefaults.compactIconSize,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }

        // Two numbers in a half-width tile need the smaller size to leave the unit its room.
        val valueStyle =
            if (item.value.secondary != null) Typography.titleMediumMono
            else Typography.titleLargeMono

        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
            LiftAppText(text = item.value.primary, style = valueStyle, maxLines = 1)

            item.value.secondary?.let { secondary ->
                LiftAppText(
                    text = SideSeparator,
                    style = typography.titleMedium,
                    color = colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )

                LiftAppText(
                    text = secondary,
                    style = valueStyle,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                )
            }

            Spacer(Modifier.weight(1f))

            LiftAppText(
                text = item.value.unit,
                style = typography.labelSmall,
                color = colorScheme.onSurfaceVariant,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(start = UnitMinSpacing, bottom = 2.dp),
            )
        }

        // With the change moved up beside the name, the footer is the measurement's own shape and
        // nothing else, and it gets the tile's full width to draw it in.
        when {
            item.sideBalance != null ->
                LiftAppRatioBar(
                    leadingFraction = item.sideBalance,
                    leadingColor = BodyMeasurementChartColors.leading,
                    trailingColor = BodyMeasurementChartColors.trailing,
                    modifier = Modifier.fillMaxWidth().height(TileFooterHeight),
                )
            item.progress != null ->
                LiftAppLinearProgressIndicator(
                    progress = item.progress,
                    color = BodyMeasurementChartColors.leading,
                    modifier = Modifier.fillMaxWidth().height(TileFooterHeight),
                )
            item.trend != null ->
                Sparkline(
                    modelProducer = item.trend,
                    color = BodyMeasurementChartColors.leading,
                    strokeWidth = 1.5.dp,
                    modifier = Modifier.fillMaxWidth().height(TileFooterHeight),
                )
            else -> Spacer(Modifier.height(TileFooterHeight))
        }
    }
}

/** An invitation rather than a silent row: the tile says what is missing and offers to add it. */
@Composable
private fun EmptyBodyMeasurementTile(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(
        onClick = onClick,
        colors = LiftAppCardDefaults.outlinedColors,
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = TileContentPadding,
        modifier = modifier,
    ) {
        LiftAppText(
            text = name,
            style = typography.labelMedium,
            color = colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = LiftAppIcons.Plus,
                contentDescription = null,
                tint = colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )

            LiftAppText(
                text = stringResource(R.string.body_measurement_first_entry),
                style = typography.labelSmall,
                color = colorScheme.primary,
            )
        }
    }
}

private const val SideSeparator = "/"
private val TileContentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
private val TileFooterHeight = 20.dp
private val UnitMinSpacing = 6.dp
