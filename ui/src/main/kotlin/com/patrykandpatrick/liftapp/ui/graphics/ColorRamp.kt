package com.patrykandpatrick.liftapp.ui.graphics

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.util.lerp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Blends two colors around the rim of the color wheel — lerping lightness and chroma, rotating hue
 * the shorter way — rather than straight across it.
 *
 * A straight line between colors far apart in hue passes near the middle of the wheel, where there
 * is no color left: green into orange comes out olive. Walking the rim keeps the chroma, and can be
 * worth half as much again at the midpoint.
 *
 * Interpolated colors outside the display gamut are clipped.
 */
fun lerpOklch(start: Color, stop: Color, fraction: Float): Color {
    val from = start.convert(ColorSpaces.Oklab)
    val to = stop.convert(ColorSpaces.Oklab)

    // In Oklab the components named red, green and blue are L, a and b.
    val startHue = atan2(from.blue.toDouble(), from.green.toDouble())
    val hueDelta =
        (atan2(to.blue.toDouble(), to.green.toDouble()) - startHue).let { delta ->
            when {
                delta > PI -> delta - 2 * PI
                delta < -PI -> delta + 2 * PI
                else -> delta
            }
        }
    val hue = startHue + hueDelta * fraction
    val chroma =
        lerp(
            hypot(from.green.toDouble(), from.blue.toDouble()).toFloat(),
            hypot(to.green.toDouble(), to.blue.toDouble()).toFloat(),
            fraction,
        )

    return Color(
            red = lerp(from.red, to.red, fraction),
            green = (chroma * cos(hue)).toFloat(),
            blue = (chroma * sin(hue)).toFloat(),
            alpha = lerp(start.alpha, stop.alpha, fraction),
            colorSpace = ColorSpaces.Oklab,
        )
        .convert(ColorSpaces.Srgb)
}

/**
 * The same hue backed off [amount] of the way toward [lightness], holding [chroma] of its color.
 *
 * Not the same as reduced alpha, which takes lightness and chroma away together: over a dark
 * background that turns every warm color brown, because a dark amber *is* brown.
 */
fun Color.muted(lightness: Float, amount: Float, chroma: Float): Color {
    val oklab = convert(ColorSpaces.Oklab)
    val hue = atan2(oklab.blue.toDouble(), oklab.green.toDouble())
    val mutedChroma = hypot(oklab.green.toDouble(), oklab.blue.toDouble()) * chroma

    return Color(
            red = lerp(oklab.red, lightness, amount),
            green = (mutedChroma * cos(hue)).toFloat(),
            blue = (mutedChroma * sin(hue)).toFloat(),
            alpha = alpha,
            colorSpace = ColorSpaces.Oklab,
        )
        .convert(ColorSpaces.Srgb)
}

/**
 * Color stops for a track of solid zones, each crossfading into the next over [blend] either side
 * of its boundary. There is one fewer [boundaries] than [zones].
 *
 * Each crossfade is filled with [steps] stops rather than left to the gradient, which would
 * interpolate a wide hue arc in a straight line — see [lerpOklch].
 */
fun zoneColorStops(
    zones: List<Color>,
    boundaries: List<Float>,
    blend: Float,
    steps: Int = 8,
): List<Pair<Float, Color>> {
    require(boundaries.size == zones.size - 1) {
        "${zones.size} zones need ${zones.size - 1} boundaries, not ${boundaries.size}."
    }

    return buildList {
        add(0f to zones.first())
        boundaries.forEachIndexed { index, boundary ->
            val from = zones[index]
            val to = zones[index + 1]
            val start = boundary - blend
            val end = boundary + blend
            add(start to from)
            for (step in 1 until steps) {
                val fraction = step.toFloat() / steps
                add(lerp(start, end, fraction) to lerpOklch(from, to, fraction))
            }
            add(end to to)
        }
        add(1f to zones.last())
    }
}
