package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.compose.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.compose.cartesian.axis.Axis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatryk.liftapp.core.format.LocalFormatter
import com.patrykandpatryk.liftapp.domain.format.Formatter

@Composable
fun rememberStartAxisValueFormatter(unit: String): CartesianValueFormatter =
    remember(unit) { CartesianValueFormatter.decimal(decimalCount = 2, suffix = unit) }

class ValueUnitCartesianValueFormatter(private val formatter: Formatter) : CartesianValueFormatter {
    private val delegate = CartesianValueFormatter.decimal(2)

    override fun format(
        context: CartesianMeasuringContext,
        value: Double,
        verticalAxisPosition: Axis.Position.Vertical?,
    ): CharSequence {
        val valueUnit = context.model.extraStore.getOrNull(ExtraStoreKey.ValueUnit)
        return if (valueUnit != null) {
            formatter.formatValue(value, valueUnit)
        } else {
            delegate.format(context, value, verticalAxisPosition)
        }
    }
}

@Composable
fun rememberValueUnitCartesianValueFormatter(): CartesianValueFormatter {
    val formatter = LocalFormatter.current
    return remember(formatter) { ValueUnitCartesianValueFormatter(formatter) }
}
