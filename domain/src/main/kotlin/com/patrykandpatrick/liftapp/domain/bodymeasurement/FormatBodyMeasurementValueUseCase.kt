package com.patrykandpatrick.liftapp.domain.bodymeasurement

import com.patrykandpatrick.liftapp.domain.bodymeasurement.FormatBodyMeasurementValueUseCase.Companion.PLUS_SIGN
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.unit.ValueUnit
import javax.inject.Inject
import kotlin.math.abs

class FormatBodyMeasurementValueUseCase
@Inject
constructor(
    private val unitConverter: UnitConverter,
    private val formatter: Formatter,
    private val stringProvider: StringProvider,
    private val getBodyMeasurementValueDelta: GetBodyMeasurementValueDeltaUseCase,
) {
    suspend operator fun invoke(
        value: BodyMeasurementValue,
        previousValue: BodyMeasurementValue? = null,
    ): BodyMeasurementValueDisplay {
        val delta =
            previousValue
                ?.takeIf { it::class == value::class }
                ?.let { getBodyMeasurementValueDelta(value, it) }

        return when (value) {
            is BodyMeasurementValue.SingleValue ->
                BodyMeasurementValueDisplay(
                    primary = formatNumber(value.unit, value.value),
                    secondary = null,
                    unit = displayUnit(value),
                    delta =
                        (delta as? BodyMeasurementValue.SingleValue)?.let { change ->
                            delta(change.value)
                        },
                )

            is BodyMeasurementValue.DoubleValue ->
                BodyMeasurementValueDisplay(
                    primary = formatNumber(value.unit, value.left),
                    secondary = formatNumber(value.unit, value.right),
                    unit = displayUnit(value),
                    // Two sides move independently, so a single delta would have to pick one. The
                    // left is the primary, matching which value leads the display.
                    delta =
                        (delta as? BodyMeasurementValue.DoubleValue)?.let { change ->
                            delta(change.left)
                        },
                )
        }
    }

    private suspend fun formatNumber(unit: ValueUnit, value: Double): String =
        formatter.formatNumber(
            unitConverter.convertToPreferredUnit(unit, value),
            format = Formatter.NumberFormat.Decimal,
        )

    private suspend fun displayUnit(value: BodyMeasurementValue): String =
        stringProvider.getDisplayUnit(
            unitConverter.getPreferredUnit(value.unit),
            respectLeadingSpaceSetting = false,
        )

    /**
     * [value] is already in the preferred unit: [GetBodyMeasurementValueDeltaUseCase] converts both
     * readings before subtracting them, so converting here as well would scale the change twice.
     */
    private fun delta(value: Double): BodyMeasurementValueDisplay.Delta {
        // Round before reading the direction so a change too small to show does not arrive
        // labelled "0" while pointing up.
        val rounded = formatter.round(value)
        // Both signs are written here so the pair matches: a formatted negative would carry the
        // hyphen the locale's number format uses, which is narrower and sits lower than the plus
        // it alternates with. No unit either — the delta is always shown against the value it
        // belongs to, whose unit is on the same object, and repeating it costs room the
        // measurement's name needs more.
        val label =
            formatter.formatNumber(
                abs(rounded),
                format = Formatter.NumberFormat.Decimal,
                prefix =
                    when {
                        rounded > 0 -> PLUS_SIGN
                        rounded < 0 -> MINUS_SIGN
                        else -> null
                    },
            )

        return BodyMeasurementValueDisplay.Delta(
            label = label,
            direction =
                when {
                    rounded > 0 -> BodyMeasurementValueDisplay.Direction.Up
                    rounded < 0 -> BodyMeasurementValueDisplay.Direction.Down
                    else -> BodyMeasurementValueDisplay.Direction.Unchanged
                },
        )
    }

    private companion object {
        const val PLUS_SIGN = "+"

        /** U+2212, the sign that pairs with [PLUS_SIGN]; a hyphen only stands in for it. */
        const val MINUS_SIGN = "−"
    }
}
