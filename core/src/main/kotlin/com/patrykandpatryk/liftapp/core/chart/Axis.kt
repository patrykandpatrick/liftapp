package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.common.insets
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter

@Composable
fun VerticalAxis.Companion.rememberStart(
    valueFormatter: CartesianValueFormatter
): VerticalAxis<Axis.Position.Vertical.Start> =
    VerticalAxis.rememberStart(
        label =
            rememberTextComponent(
                textStyle = MaterialTheme.typography.titleSmall,
                margins = insets(end = 4.dp),
            ),
        valueFormatter = valueFormatter,
    )

@Composable
fun HorizontalAxis.Companion.rememberBottom(): HorizontalAxis<Axis.Position.Horizontal.Bottom> =
    HorizontalAxis.rememberBottom(
        valueFormatter = rememberEpochDayCartesianValueFormatter(),
        label =
            rememberTextComponent(
                textStyle = MaterialTheme.typography.titleSmall,
                margins = insets(top = 2.dp, start = 4.dp, end = 4.dp),
            ),
        guideline = null,
    )
