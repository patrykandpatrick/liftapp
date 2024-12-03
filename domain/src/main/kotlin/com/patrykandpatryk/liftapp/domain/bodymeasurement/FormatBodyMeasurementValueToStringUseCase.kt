package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import javax.inject.Inject

class FormatBodyMeasurementValueToStringUseCase @Inject constructor(private val unitConverter: UnitConverter) {
    suspend operator fun invoke(bodyMeasurementValue: BodyMeasurementValue) = when (bodyMeasurementValue) {
        is BodyMeasurementValue.DoubleValue -> unitConverter.convertToPreferredUnitAndFormat(
            bodyMeasurementValue.unit,
            bodyMeasurementValue.left,
            bodyMeasurementValue.right,
        )
        is BodyMeasurementValue.SingleValue -> unitConverter.convertToPreferredUnitAndFormat(
            bodyMeasurementValue.unit,
            bodyMeasurementValue.value,
        )
    }
}
