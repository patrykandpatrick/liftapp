package com.patrykandpatrick.liftapp.core.ui.volume

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.patrykandpatrick.liftapp.domain.muscle.VolumeBand
import com.patrykandpatrick.liftapp.ui.graphics.muted
import com.patrykandpatrick.liftapp.ui.theme.TailwindColors
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

/**
 * The four bands as one cool-to-warm sweep, drawn from the same Tailwind ramps as the rest of the
 * theme rather than from its semantic green/yellow/red.
 *
 * In band order the semantic colors run grey, yellow, green, red, which reverses direction around
 * the color wheel twice, so the crossfades between them come out olive and brown. Slate into sky
 * into emerald into amber only ever turns one way, which keeps every blend on the rim.
 */
object VolumeScaleColors {

    val below: Color
        @Composable
        get() =
            if (colorScheme.isDarkColorScheme) TailwindColors.slate400 else TailwindColors.slate500

    val maintenance: Color
        @Composable
        get() = if (colorScheme.isDarkColorScheme) TailwindColors.sky400 else TailwindColors.sky600

    val adaptive: Color
        @Composable
        get() =
            if (colorScheme.isDarkColorScheme) TailwindColors.emerald400
            else TailwindColors.emerald600

    val maximum: Color
        @Composable
        get() =
            if (colorScheme.isDarkColorScheme) TailwindColors.amber400 else TailwindColors.amber500

    /** In band order, which is the order the scale sweeps in. */
    val ordered: List<Color>
        @Composable get() = listOf(below, maintenance, adaptive, maximum)

    /**
     * The same sweep as [ordered], backed off far enough to sit behind the thumbs rather than
     * compete with them. Opaque rather than translucent, so a row reads the same on the dashboard's
     * card as on the screen's background — and because alpha over a dark theme turns the warm end
     * brown. See [muted].
     */
    val track: List<Color>
        @Composable
        get() {
            val dark = colorScheme.isDarkColorScheme
            return ordered.map { color ->
                color.muted(
                    lightness = if (dark) 0f else 1f,
                    amount = if (dark) .22f else .45f,
                    chroma = if (dark) .58f else .5f,
                )
            }
        }

    @Composable
    fun of(band: VolumeBand): Color =
        when (band) {
            VolumeBand.BelowMaintenance -> below
            VolumeBand.Maintenance -> maintenance
            VolumeBand.Adaptive -> adaptive
            VolumeBand.Maximum -> maximum
        }
}
