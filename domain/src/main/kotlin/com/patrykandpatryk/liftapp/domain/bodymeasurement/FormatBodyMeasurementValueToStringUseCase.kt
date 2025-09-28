package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.markup.MarkupType
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import javax.inject.Inject

class FormatBodyMeasurementValueToStringUseCase
@Inject
constructor(
    private val unitConverter: UnitConverter,
    private val getBodyMeasurementValueDeltaUseCase: GetBodyMeasurementValueDeltaUseCase,
) {
    suspend operator fun invoke(
        bodyMeasurementValue: BodyMeasurementValue,
        previousBodyMeasurementValue: BodyMeasurementValue? = null,
    ): String {
        val change =
            previousBodyMeasurementValue?.let {
                getBodyMeasurementValueDeltaUseCase(bodyMeasurementValue, it)
            }

        return when (bodyMeasurementValue) {
            is BodyMeasurementValue.DoubleValue -> {
                val change = change as? BodyMeasurementValue.DoubleValue
                val left =
                    formatValues(
                        unit = bodyMeasurementValue.unit,
                        value = bodyMeasurementValue.left,
                        changeUnit = change?.unit,
                        changeValue = change?.left,
                    )
                val right =
                    formatValues(
                        unit = bodyMeasurementValue.unit,
                        value = bodyMeasurementValue.right,
                        changeUnit = change?.unit,
                        changeValue = change?.right,
                        changeMarkupColor = MarkupType.Color.Yellow,
                    )

                "$left | $right"
            }

            is BodyMeasurementValue.SingleValue -> {
                val change = change as? BodyMeasurementValue.SingleValue
                formatValues(
                    unit = bodyMeasurementValue.unit,
                    value = bodyMeasurementValue.value,
                    changeUnit = change?.unit,
                    changeValue = change?.value,
                )
            }
        }
    }

    private suspend fun formatValues(
        unit: ValueUnit,
        value: Double,
        changeUnit: ValueUnit?,
        changeValue: Double?,
        changeMarkupColor: MarkupType = MarkupType.Color.Green,
    ): String =
        unitConverter.convertToPreferredUnitAndFormat(from = unit, value = value) +
            if (changeUnit != null && changeValue != null) {
                " ${getChange(changeUnit, changeValue, changeMarkupColor)}"
            } else {
                ""
            }

    private suspend fun getChange(unit: ValueUnit, value: Double, changeMarkupColor: MarkupType) =
        buildString {
                append("(")
                if (value > 0) {
                    append("+")
                }
                append(unitConverter.convertToPreferredUnitAndFormat(unit, value))
                append(")")
            }
            .let { text ->
                MarkupType.wrap(
                    text,
                    changeMarkupColor,
                    MarkupType.Size.Medium,
                    MarkupType.Style.Bold,
                )
            }
}
