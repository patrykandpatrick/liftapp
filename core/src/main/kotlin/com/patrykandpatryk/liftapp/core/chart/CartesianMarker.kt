package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.shadow
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.MarkerCorneredShape
import java.text.DecimalFormat

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
                        fill = fill(colorScheme.surface),
                        shape = MarkerCorneredShape(CorneredShape.Pill),
                        strokeFill = fill(colorScheme.outline),
                        strokeThickness = 1.dp,
                        shadow = shadow(radius = 2.dp, y = 2.dp),
                    ),
                padding = insets(horizontal = 8.dp, vertical = 4.dp),
            ),
        indicator = { color -> shapeComponent(fill = fill(color), shape = CorneredShape.Pill) },
        indicatorSize = 8.dp,
        labelPosition = DefaultCartesianMarker.LabelPosition.AroundPoint,
        valueFormatter = valueFormatter,
    )
}

@Composable
fun rememberCartesianMarkerValueFormatter(unit: String): DefaultCartesianMarker.ValueFormatter =
    remember(unit) {
        DefaultCartesianMarker.ValueFormatter.default(
            decimalFormat = DecimalFormat("#.##'${unit}';−#.##'${unit}'")
        )
    }
