package com.patrykandpatrick.liftapp.core.chart

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.ChartAreaFill
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent

/**
 * The app's line: [color] for the stroke, the shared [ChartAreaFill] beneath it, and a marked point
 * at every reading. [stroke] and [pointProvider] are open so a chart with less room can thin the
 * line or mark fewer points without parting with the rest of the styling — passing `null` for
 * [pointProvider] marks none.
 */
@Composable
fun LineCartesianLayer.Companion.rememberLine(
    color: Color = colorScheme.primary,
    stroke: LineCartesianLayer.LineStroke = LineCartesianLayer.LineStroke.Continuous(),
    pointProvider: LineCartesianLayer.PointProvider? =
        LineCartesianLayer.PointProvider.single(
            LineCartesianLayer.Point(
                component = rememberLineCartesianLayerPointComponent(strokeColor = color),
                size = 10.dp,
            )
        ),
): LineCartesianLayer.Line =
    LineCartesianLayer.rememberLine(
        fill = LineCartesianLayer.LineFill.single(Fill(color)),
        stroke = stroke,
        areaFill = LineCartesianLayer.AreaFill.single(Fill(ChartAreaFill.brush(color))),
        pointProvider = pointProvider,
        interpolator = LineCartesianLayer.Interpolator.cubic(),
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
