package com.patrykandpatryk.liftapp.core.mapper

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import javax.inject.Inject

class BodyMeasurementEntryToChartEntryMapper
@Inject
constructor(private val unitConverter: UnitConverter) {

    suspend operator fun invoke(
        input: List<BodyMeasurementEntry>
    ): List<Pair<List<Double>, List<Double>>> {
        val x = mutableListOf<Double>()
        val y1 = mutableListOf<Double>()
        val y2 = mutableListOf<Double>()

        input.forEachIndexed { index, entry ->
            x += entry.formattedDate.localDateTime.toLocalDate().toEpochDay().toDouble()
            when (val value = entry.value) {
                is BodyMeasurementValue.DoubleValue -> {
                    y1 += unitConverter.convertToPreferredUnit(value.unit, value.left)
                    y2 += unitConverter.convertToPreferredUnit(value.unit, value.right)
                }

                is BodyMeasurementValue.SingleValue -> {
                    y1 += unitConverter.convertToPreferredUnit(value.unit, value.value)
                }
            }
        }

        return if (y2.isEmpty()) {
            listOf(x to y1)
        } else {
            listOf(x to y1, x to y2)
        }
    }
}
