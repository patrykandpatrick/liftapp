package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.toShaderProvider
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

@Composable
fun LineCartesianLayer.Companion.rememberLine(
    color: Color = colorScheme.primary
): LineCartesianLayer.Line =
    LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(fill(color)),
        areaFill =
            LineCartesianLayer.AreaFill.single(
                fill(
                    Brush.verticalGradient(
                            colors = listOf(color.copy(alpha = .5f), color.copy(alpha = 0f))
                        )
                        .toShaderProvider()
                )
            ),
        pointProvider =
            LineCartesianLayer.PointProvider.single(
                LineCartesianLayer.point(
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
        fill = fill(innerColor),
        shape = CorneredShape.Pill,
        strokeThickness = strokeThickness,
        strokeFill = fill(strokeColor),
    )
