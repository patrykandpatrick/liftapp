package com.patrykandpatrick.liftapp.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class InteractiveBorderColors(
    val color: Color,
    val pressedColor: Color,
    val hoverForegroundColor: Color,
    val hoverBackgroundColor: Color = color,
    val draggedColor: Color = pressedColor,
    val checkedColor: Color = color,
)
