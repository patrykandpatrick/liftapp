package com.patrykandpatryk.liftapp.core.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.LineCartesianLayerMarkerTarget
import com.patrykandpatryk.liftapp.core.format.LocalFormatter
import com.patrykandpatryk.liftapp.domain.format.Formatter

class ValueUnitCartesianMarkerValueFormatter(private val formatter: Formatter) :
    DefaultCartesianMarker.ValueFormatter {
    private val delegate = DefaultCartesianMarker.ValueFormatter.default()

    override fun format(
        context: CartesianDrawingContext,
        targets: List<CartesianMarker.Target>,
    ): CharSequence {
        val valueUnit = context.model.extraStore.getOrNull(ExtraStoreKey.ValueUnit)
        return if (valueUnit != null) {
            targets.joinToString { target ->
                when (target) {
                    is LineCartesianLayerMarkerTarget -> {
                        target.points.joinToString { point ->
                            formatter.formatValue(point.entry.y, valueUnit)
                        }
                    }
                    is ColumnCartesianLayerMarkerTarget ->
                        target.columns.joinToString { column ->
                            formatter.formatValue(column.entry.y, valueUnit)
                        }
                    else -> ""
                }
            }
        } else {
            delegate.format(context, targets)
        }
    }
}

@Composable
fun rememberValueUnitCartesianMarkerValueFormatter(): ValueUnitCartesianMarkerValueFormatter {
    val formatter = LocalFormatter.current
    return remember(formatter) { ValueUnitCartesianMarkerValueFormatter(formatter) }
}
