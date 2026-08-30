package com.patrykandpatrick.liftapp.core.ui.volume

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.PreviewMuscleVolume
import com.patrykandpatrick.liftapp.core.preview.PreviewTheme
import com.patrykandpatrick.liftapp.core.ui.resource.prettyName
import com.patrykandpatrick.liftapp.core.ui.resource.rangeLabel
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.muscle.MuscleVolume
import com.patrykandpatrick.liftapp.domain.muscle.VolumeBand
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaleBar
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * One muscle's week: its name, its whole-set count, and a thumb marking where it falls on the
 * [VolumeScale]. Whole sets and a color, never a decimal — the counts come out of fractional
 * credits nobody measured, read against landmarks whose individual variation is wider than the gaps
 * between them.
 *
 * Takes the whole [volume] rather than a count and a band, so the two cannot be passed in
 * disagreeing — and so that work too slight to round up to a set reads as *less than one* rather
 * than as none.
 */
@Composable
fun MuscleVolumeRow(
    muscle: Muscle,
    volume: MuscleVolume,
    colorStops: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier,
) {
    val sets = volume.wholeSetsOf(muscle)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MuscleVolumeDefaults.barSpacing),
    ) {
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.fillMaxWidth()) {
            LiftAppText(
                text = muscle.prettyName,
                style = typography.bodyMedium,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )

            LiftAppText(
                text =
                    if (sets == 0 && volume[muscle] > 0) {
                        stringResource(R.string.muscle_volume_under_one_set)
                    } else {
                        pluralStringResource(R.plurals.muscle_volume_sets, sets, sets)
                    },
                style = typography.labelMedium,
                color = colorScheme.foregroundVariant,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(start = 8.dp),
            )
        }

        LiftAppScaleBar(
            // The rounded count, not the raw total: placed from the raw value the thumb can
            // land on the wrong side of the very landmark its label claims it has reached.
            position = VolumeScale.positionOf(sets.toDouble()),
            colorStops = colorStops,
            thumbColor = VolumeScaleColors.of(volume.bandOf(muscle)),
            marks = VolumeScale.landmarkPositions,
        )
    }
}

/**
 * What the four colors mean and where each band starts, said once for the column rather than beside
 * the dots on every bar.
 *
 * [compact] drops the band names and keeps the counts, which is what a card wants: the names would
 * be cut across a card's narrower measure, and they are on the screen the card leads to.
 */
@Composable
fun VolumeBandLegend(modifier: Modifier = Modifier, compact: Boolean = false) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // Plain Rows rather than a flow: a flow's intrinsic height depends on the width it is
        // asked about, so a card sized from it comes up short and squeezes whatever follows.
        VolumeBand.entries.chunked(LegendColumns).forEach { bands ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                bands.forEach { band ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Box(
                            Modifier.size(SwatchSize)
                                .background(VolumeScaleColors.of(band), CircleShape)
                        )

                        LiftAppText(
                            text =
                                if (compact) {
                                    band.rangeLabel
                                } else {
                                    stringResource(
                                        R.string.muscle_volume_band_legend,
                                        band.prettyName,
                                        band.rangeLabel,
                                    )
                                },
                            style = typography.labelSmall,
                            color = colorScheme.foregroundVariant,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Two to a row leaves every entry half the width, which even the longest holds at any font size.
 */
private const val LegendColumns = 2

object MuscleVolumeDefaults {
    val rowSpacing: Dp = 12.dp

    /** Between a row's heading and its bar. */
    val barSpacing: Dp = 6.dp
}

private val SwatchSize = 8.dp

@LightAndDarkThemePreview
@Composable
private fun MuscleVolumeItemsPreview() {
    PreviewTheme {
        LiftAppBackground {
            Column(
                verticalArrangement = Arrangement.spacedBy(MuscleVolumeDefaults.rowSpacing),
                modifier = Modifier.padding(16.dp),
            ) {
                val colorStops = VolumeScale.rememberColorStops()
                PreviewMuscleVolume.workedMuscles.forEach { muscle ->
                    MuscleVolumeRow(
                        muscle = muscle,
                        volume = PreviewMuscleVolume,
                        colorStops = colorStops,
                    )
                }

                VolumeBandLegend()
            }
        }
    }
}
