package com.patrykandpatryk.liftapp.core.extension

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill

fun DrawScope.drawRoundRect(
    color: Color,
    bounds: Rect,
    cornerRadius: Float,
    style: DrawStyle = Fill,
    alpha: Float = 1f,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DrawScope.DefaultBlendMode,
) = drawRoundRect(color, bounds.topLeft, bounds.size, CornerRadius(cornerRadius), style, alpha, colorFilter, blendMode)
