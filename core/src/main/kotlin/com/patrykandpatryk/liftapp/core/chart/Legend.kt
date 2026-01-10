package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.compose.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.HorizontalLegend
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.LegendItem
import com.patrykandpatrick.vico.compose.common.component.ShapeComponent
import com.patrykandpatrick.vico.compose.common.component.TextComponent
import com.patrykandpatryk.liftapp.core.R

@Composable
fun bodyMeasurementLegend(
    labelComponent: TextComponent = rememberTextComponent(MaterialTheme.typography.titleSmall),
    chartColors: List<Color> = colorScheme.chartColors,
    labelLeft: String = stringResource(R.string.body_measurement_left),
    labelRight: String = stringResource(R.string.body_measurement_right),
) =
    HorizontalLegend<CartesianMeasuringContext, CartesianDrawingContext>(
        items = { extraStore ->
            if (extraStore[ExtraStoreKey.ShowLeftRightLegend]) {
                add(
                    LegendItem(
                        icon = ShapeComponent(shape = CircleShape, fill = Fill(chartColors[0])),
                        labelComponent = labelComponent,
                        label = labelLeft,
                    )
                )

                add(
                    LegendItem(
                        icon = ShapeComponent(shape = CircleShape, fill = Fill(chartColors[1])),
                        labelComponent = labelComponent,
                        label = labelRight,
                    )
                )
            }
        },
        padding = Insets(top = 12.dp),
    )
