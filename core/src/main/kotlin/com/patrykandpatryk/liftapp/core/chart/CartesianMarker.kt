package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.MarkerCornerBasedShape
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent

@Composable
fun rememberCartesianMarker(
    valueFormatter: DefaultCartesianMarker.ValueFormatter
): CartesianMarker {
    return rememberDefaultCartesianMarker(
        label =
            rememberTextComponent(
                MaterialTheme.typography.titleSmall,
                background =
                    rememberShapeComponent(
                        fill = Fill(colorScheme.surface),
                        shape = MarkerCornerBasedShape(CircleShape),
                        strokeFill = Fill(colorScheme.outline),
                        strokeThickness = 1.dp,
                        shadows = listOf(Shadow(radius = 2.dp, offset = DpOffset(0.dp, 2.dp))),
                    ),
                padding = Insets(horizontal = 8.dp, vertical = 4.dp),
            ),
        indicator = { color -> ShapeComponent(fill = Fill(color), shape = CircleShape) },
        indicatorSize = 8.dp,
        labelPosition = DefaultCartesianMarker.LabelPosition.AroundPoint,
        valueFormatter = valueFormatter,
    )
}

@Composable
fun rememberCartesianMarkerValueFormatter(unit: String): DefaultCartesianMarker.ValueFormatter =
    remember(unit) {
        DefaultCartesianMarker.ValueFormatter.default(decimalCount = 2, suffix = unit)
    }
