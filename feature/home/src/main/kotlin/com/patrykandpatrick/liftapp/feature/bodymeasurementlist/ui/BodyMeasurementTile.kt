package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui

import androidx.compose.foundation.layout.Arrangement
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
import com.patrykandpatrick.liftapp.ui.component.LiftAppLinearProgressIndicator
import com.patrykandpatrick.liftapp.ui.component.LiftAppRatioBar
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
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
        modifier = modifier,
    ) {
        // A measurement's name is the body part alone — the section heading says they are
        // circumferences — which leaves the top line room for the change beside it.
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            LiftAppText(
                text = item.name,
                style = typography.titleSmall,
                color = colorScheme.foregroundVariant,
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
            if (item.value.secondary != null) {
                typography.titleMedium
            } else {
                typography.titleLarge
            }

        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
            LiftAppText(
                text = item.value.primary,
                style = valueStyle,
                maxLines = 1,
                modifier = Modifier.alignByBaseline(),
            )

            item.value.secondary?.let { secondary ->
                LiftAppText(
                    text = SideSeparator,
                    style = typography.titleMedium,
                    color = colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 4.dp).alignByBaseline(),
                )

                LiftAppText(
                    text = secondary,
                    style = valueStyle,
                    color = colorScheme.foregroundVariant,
                    maxLines = 1,
                    modifier = Modifier.alignByBaseline(),
                )
            }

            Spacer(Modifier.weight(1f))

            LiftAppText(
                text = item.value.unit,
                style = typography.labelSmall,
                color = colorScheme.foregroundVariant,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(start = UnitMinSpacing).alignByBaseline(),
            )
        }

        val hasVisualization =
            item.sideBalance != null || item.progress != null || item.trend != null
        if (hasVisualization) {
            // The grid gives every card in a row the same height. Consume any difference here so
            // each visualization sits directly above the card's bottom padding without a
            // fixed-height slot adding invisible space around thinner bars.
            Spacer(Modifier.weight(1f))
        }

        when {
            item.sideBalance != null ->
                LiftAppRatioBar(
                    leadingFraction = item.sideBalance,
                    leadingColor = BodyMeasurementChartColors.leading,
                    trailingColor = BodyMeasurementChartColors.trailing,
                    modifier = Modifier.fillMaxWidth(),
                )
            item.progress != null ->
                LiftAppLinearProgressIndicator(
                    progress = item.progress,
                    color = BodyMeasurementChartColors.leading,
                    modifier = Modifier.fillMaxWidth(),
                )
            item.trend != null ->
                Sparkline(
                    modelProducer = item.trend,
                    color = BodyMeasurementChartColors.leading,
                    strokeWidth = 1.5.dp,
                    modifier = Modifier.fillMaxWidth().height(TileSparklineHeight),
                )
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
        // The grid stretches this card to the height of its populated neighbor. Space the two
        // rows apart so the action stays bottom-aligned without inserting another layout item
        // that would increase the row's intrinsic height.
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier,
    ) {
        LiftAppText(
            text = name,
            style = typography.titleSmall,
            color = colorScheme.foregroundVariant,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth(),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 6.dp),
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
private val TileSparklineHeight = 20.dp
private val UnitMinSpacing = 6.dp
