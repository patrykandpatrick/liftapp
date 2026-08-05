package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import com.patrykandpatrick.liftapp.core.ui.resource.icon
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plus
import com.patrykandpatrick.liftapp.ui.theme.PillShape
import com.patrykandpatrick.liftapp.ui.theme.Typography
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * The one measurement worth reading before any other, given the room to show its number at a size
 * that survives a glance and its trend without a tap.
 */
@Composable
internal fun BodyMeasurementHeroCard(
    item: BodyMeasurementListItem,
    onClick: () -> Unit,
    onAddEntryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(
        onClick = onClick,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding =
            PaddingValues(
                top = CardPaddingTop,
                start = CardPadding,
                bottom = CardPadding,
                end = CardPadding,
            ),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier.size(28.dp)
                        .background(color = colorScheme.primaryDisabled, shape = PillShape),
            ) {
                Icon(
                    imageVector = item.type.icon,
                    contentDescription = null,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(16.dp),
                )
            }

            LiftAppText(
                text = item.name,
                style = typography.titleMedium,
                modifier = Modifier.weight(1f),
            )

            // The button pads itself out to a touch target, and that padding is already separation
            // from the card's edge. Letting it overhang the card's own padding by the difference
            // leaves the button half that padding from the edge rather than all of it plus its own.
            val buttonPadding = (dimens.iconButton.minTouchTarget - dimens.iconButton.size) / 2
            val overhang = CardPadding - (CardPadding / 2 - buttonPadding)

            LiftAppIconButton(
                onClick = onAddEntryClick,
                modifier = Modifier.offset(x = overhang),
            ) {
                Icon(
                    imageVector = LiftAppIcons.Plus,
                    contentDescription =
                        stringResource(R.string.body_measurement_add_entry, item.name),
                )
            }
        }

        if (item.value != null) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                LiftAppText(
                    text = item.value.primary,
                    style = Typography.headlineMediumMono,
                    modifier = Modifier.alignByBaseline(),
                )

                item.value.secondary?.let { secondary ->
                    LiftAppText(
                        text = secondary,
                        style = Typography.headlineMediumMono,
                        color = colorScheme.foregroundVariant,
                        modifier = Modifier.padding(start = 2.dp).alignByBaseline(),
                    )
                }

                LiftAppText(
                    text = item.value.unit,
                    style = typography.titleSmall,
                    color = colorScheme.foregroundVariant,
                    modifier = Modifier.alignByBaseline(),
                )

                Spacer(Modifier.weight(1f))

                item.value.delta?.let { delta ->
                    DeltaChip(delta = delta, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
        } else {
            LiftAppText(
                text = stringResource(R.string.body_measurement_never_logged),
                style = typography.bodyMedium,
                color = colorScheme.foregroundVariant,
            )
        }

        item.trend?.let { trend ->
            Sparkline(
                modelProducer = trend,
                color = BodyMeasurementChartColors.leading,
                pointInnerColor = colorScheme.surface,
                showLatestPoint = true,
                modifier = Modifier.fillMaxWidth().height(56.dp),
            )
        }
    }
}

private val CardPaddingTop = 8.dp
private val CardPadding = 16.dp
