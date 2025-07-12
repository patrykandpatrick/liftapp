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
    val draggedColor: Color = pressedColor,
    val checkedColor: Color = color,
)

fun lerp(
    start: InteractiveBorderColors,
    end: InteractiveBorderColors,
    fraction: Float,
): InteractiveBorderColors =
    InteractiveBorderColors(
        color = lerp(start.color, end.color, fraction),
        pressedColor = lerp(start.pressedColor, end.pressedColor, fraction),
        hoverForegroundColor = lerp(start.hoverForegroundColor, end.hoverForegroundColor, fraction),
        hoverBackgroundColor = lerp(start.hoverBackgroundColor, end.hoverBackgroundColor, fraction),
        draggedColor = lerp(start.draggedColor, end.draggedColor, fraction),
        checkedColor = lerp(start.checkedColor, end.checkedColor, fraction),
    )
