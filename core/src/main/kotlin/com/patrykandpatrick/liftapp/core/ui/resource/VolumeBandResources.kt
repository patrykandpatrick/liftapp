package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.format.LocalFormatter
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.muscle.VolumeBand

val VolumeBand.stringRes: Int
    get() =
        when (this) {
            VolumeBand.BelowMaintenance -> R.string.volume_band_below_maintenance
            VolumeBand.Maintenance -> R.string.volume_band_maintenance
            VolumeBand.Adaptive -> R.string.volume_band_adaptive
            VolumeBand.Maximum -> R.string.volume_band_maximum
        }

val VolumeBand.prettyName: String
    @Composable get() = stringResource(stringRes)

/**
 * The whole sets a band spans, for the legend. Read over integers rather than over the real line,
 * because integers are what the rows show: a band running up to ten does not include it, and
 * labelling it "6–10" would put a row reading ten sets in two bands at once.
 */
val VolumeBand.rangeLabel: String
    @Composable
    get() {
        val formatter = LocalFormatter.current
        val sets = { value: Double ->
            formatter.formatNumber(value, format = Formatter.NumberFormat.Integer)
        }
        return when (this) {
            VolumeBand.BelowMaintenance ->
                stringResource(
                    R.string.volume_band_range_under,
                    sets(VolumeBand.MAINTENANCE_VOLUME),
                )
            VolumeBand.Maintenance ->
                stringResource(
                    R.string.volume_band_range_between,
                    sets(VolumeBand.MAINTENANCE_VOLUME),
                    sets(VolumeBand.ADAPTIVE_VOLUME - 1),
                )
            VolumeBand.Adaptive ->
                stringResource(
                    R.string.volume_band_range_between,
                    sets(VolumeBand.ADAPTIVE_VOLUME),
                    sets(VolumeBand.RECOVERABLE_VOLUME - 1),
                )
            VolumeBand.Maximum ->
                stringResource(
                    R.string.volume_band_range_from,
                    sets(VolumeBand.RECOVERABLE_VOLUME),
                )
        }
    }
