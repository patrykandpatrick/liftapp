package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import java.text.DecimalFormat

@Composable
fun rememberStartAxisValueFormatter(unit: String): CartesianValueFormatter =
    remember(unit) {
        CartesianValueFormatter.decimal(DecimalFormat("#.##'${unit}';−#.##'${unit}'"))
    }
