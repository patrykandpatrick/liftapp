package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import javax.inject.Inject

class GetBodyMeasurementValueDeltaUseCase
@Inject
constructor(private val unitConverter: UnitConverter) {
    suspend operator fun invoke(
        currentValue: BodyMeasurementValue,
        previousValue: BodyMeasurementValue,
    ) =
        when (currentValue) {
            is BodyMeasurementValue.DoubleValue -> {
                val currentLeft =
                    unitConverter.convertToPreferredUnit(currentValue.unit, currentValue.left)
                val currentRight =
                    unitConverter.convertToPreferredUnit(currentValue.unit, currentValue.right)
                previousValue as BodyMeasurementValue.DoubleValue
                val previousLeft =
                    unitConverter.convertToPreferredUnit(previousValue.unit, previousValue.left)
                val previousRight =
                    unitConverter.convertToPreferredUnit(previousValue.unit, previousValue.right)
                BodyMeasurementValue.DoubleValue(
                    currentLeft - previousLeft,
                    currentRight - previousRight,
                    unitConverter.getPreferredUnit(currentValue.unit),
                )
            }
            is BodyMeasurementValue.SingleValue -> {
                val current =
                    unitConverter.convertToPreferredUnit(currentValue.unit, currentValue.value)
                previousValue as BodyMeasurementValue.SingleValue
                val previous =
                    unitConverter.convertToPreferredUnit(previousValue.unit, previousValue.value)
                BodyMeasurementValue.SingleValue(
                    current - previous,
                    unitConverter.getPreferredUnit(currentValue.unit),
                )
            }
        }
}
