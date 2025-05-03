package com.patrykandpatryk.liftapp.core.graphics

import androidx.compose.ui.graphics.Path
import kotlin.math.ceil
import kotlin.math.sin

fun Path.addSinLine(
    start: Int,
    end: Int,
    sinPeriodLength: Int,
    sinHeight: Int,
    horizontal: Boolean = true,
    verticalOffset: Float = 0f,
) {
    val adjustedSinPeriodLength = getAdjustedSinPeriodLength(end - start, sinPeriodLength)
    for (coordinate in start..end) {
        val value =
            sin(coordinate.toFloat() / adjustedSinPeriodLength) * (sinHeight / 2) +
                sinHeight / 2 +
                verticalOffset
        if (coordinate == start) {
            if (horizontal) {
                moveTo(start.toFloat(), value)
            } else {
                moveTo(value, start.toFloat())
            }
        } else {
            if (horizontal) {
                lineTo(coordinate.toFloat(), value)
            } else {
                lineTo(value, coordinate.toFloat())
            }
        }
    }
}

private fun getAdjustedSinPeriodLength(totalWidth: Int, target: Int): Float {
    return if (totalWidth % target == 0) {
        target.toFloat()
    } else {
        totalWidth * (target / (ceil(totalWidth / target.toDouble()) * target).toFloat())
    }
}
