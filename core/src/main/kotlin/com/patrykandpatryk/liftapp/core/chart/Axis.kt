package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.axis.Axis
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.common.Insets

@Composable
fun VerticalAxis.Companion.start(
    valueFormatter: CartesianValueFormatter
): VerticalAxis<Axis.Position.Vertical.Start> =
    VerticalAxis.rememberStart(
        label =
            rememberTextComponent(
                textStyle = MaterialTheme.typography.titleSmall,
                margins = Insets(end = 4.dp),
            ),
        valueFormatter = valueFormatter,
    )

@Composable
fun HorizontalAxis.Companion.bottom(): HorizontalAxis<Axis.Position.Horizontal.Bottom> =
    HorizontalAxis.rememberBottom(
        valueFormatter = rememberEpochDayCartesianValueFormatter(),
        label =
            rememberTextComponent(
                textStyle = MaterialTheme.typography.titleSmall,
                margins = Insets(top = 2.dp, start = 4.dp, end = 4.dp),
            ),
        guideline = null,
    )
