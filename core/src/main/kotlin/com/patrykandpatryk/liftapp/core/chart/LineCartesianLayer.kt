package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent

@Composable
fun LineCartesianLayer.Companion.rememberLine(
    color: Color = colorScheme.primary
): LineCartesianLayer.Line =
    LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(color)),
        areaFill =
            LineCartesianLayer.AreaFill.single(
                Fill(
                    Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = .5f), color.copy(alpha = 0f))
                    )
                )
            ),
        pointProvider =
            LineCartesianLayer.PointProvider.single(
                LineCartesianLayer.Point(
                    component = rememberLineCartesianLayerPointComponent(strokeColor = color),
                    size = 10.dp,
                )
            ),
        pointConnector = LineCartesianLayer.PointConnector.cubic(),
    )

@Composable
fun rememberLineCartesianLayerPointComponent(
    strokeColor: Color = colorScheme.primary,
    innerColor: Color = colorScheme.surface,
    strokeThickness: Dp = 2.dp,
): ShapeComponent =
    rememberShapeComponent(
        fill = Fill(innerColor),
        shape = CircleShape,
        strokeThickness = strokeThickness,
        strokeFill = Fill(strokeColor),
    )
