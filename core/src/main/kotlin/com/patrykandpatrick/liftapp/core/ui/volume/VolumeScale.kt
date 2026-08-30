package com.patrykandpatrick.liftapp.core.ui.volume

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.domain.muscle.VolumeBand
import com.patrykandpatrick.liftapp.ui.graphics.zoneColorStops
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * The volume landmarks drawn as one ruler, which every row's thumb is read against.
 *
 * A scale rather than a bar to fill, because the shape of a fill is a claim: it says more is nearer
 * to done. Weekly volume is not that quantity — the productive band is in the middle and the top of
 * the range is a limit.
 */
object VolumeScale {

    /**
     * The most sets the track can show. A quarter past the recoverable ceiling rather than at it,
     * so past the ceiling is somewhere to be rather than a point everything over it pins to.
     */
    const val MAXIMUM = VolumeBand.RECOVERABLE_VOLUME * 1.25

    /**
     * Half the width of each crossfade between one band's color and the next, in sets. The
     * boundaries are population figures with a range around them, so drawing them as knife edges
     * would claim a precision the numbers do not have.
     */
    private const val BLEND_SETS = 1.0

    /** Where [sets] falls along the track, clamped at both ends. */
    fun positionOf(sets: Double): Float = (sets / MAXIMUM).coerceIn(0.0, 1.0).toFloat()

    /**
     * The set counts the scale turns on. Neither end is one: zero needs no saying, and the far end
     * is past the last landmark rather than a landmark itself.
     */
    val landmarks: List<Double> =
        listOf(
            VolumeBand.MAINTENANCE_VOLUME,
            VolumeBand.ADAPTIVE_VOLUME,
            VolumeBand.RECOVERABLE_VOLUME,
        )

    val landmarkPositions: List<Float> = landmarks.map(::positionOf)

    /**
     * Remembered because the stops are the same for every row and building them is not cheap: four
     * colors backed off through Oklab, then three crossfades of eight interpolated steps each.
     */
    @Composable
    fun rememberColorStops(): List<Pair<Float, Color>> {
        val zones = VolumeScaleColors.track
        return remember(colorScheme.isDarkColorScheme) {
            zoneColorStops(
                zones = zones,
                boundaries = landmarkPositions,
                blend = (BLEND_SETS / MAXIMUM).toFloat(),
            )
        }
    }
}
