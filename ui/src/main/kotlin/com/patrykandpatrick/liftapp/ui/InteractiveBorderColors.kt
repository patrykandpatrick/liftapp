package com.patrykandpatrick.liftapp.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

@Immutable
data class InteractiveBorderColors(
    val color: Color,
    val pressedColor: Color,
    val hoverForegroundColor: Color,
    val hoverBackgroundColor: Color = color,
    val checkedColor: Color = color,
)

fun lerp(
    start: InteractiveBorderColors,
    stop: InteractiveBorderColors,
    fraction: Float,
): InteractiveBorderColors =
    InteractiveBorderColors(
        color = lerp(start.color, stop.color, fraction),
        pressedColor = lerp(start.pressedColor, stop.pressedColor, fraction),
        hoverForegroundColor =
            lerp(start.hoverForegroundColor, stop.hoverForegroundColor, fraction),
        hoverBackgroundColor =
            lerp(start.hoverBackgroundColor, stop.hoverBackgroundColor, fraction),
        checkedColor = lerp(start.checkedColor, stop.checkedColor, fraction),
    )
