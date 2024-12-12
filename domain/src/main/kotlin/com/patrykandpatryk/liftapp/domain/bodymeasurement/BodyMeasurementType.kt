package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_LENGTH_IMPERIAL
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_LENGTH_METRIC
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_PERCENTAGE
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_WEIGHT_IMPERIAL
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_WEIGHT_METRIC
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MIN_VALUE
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import kotlinx.serialization.Serializable

@Serializable
enum class BodyMeasurementType(
    val metricValueRange: ClosedFloatingPointRange<Double>,
    val imperialValueRange: ClosedFloatingPointRange<Double>,
) {
    Weight(
        metricValueRange = BODY_MIN_VALUE..BODY_MAX_WEIGHT_METRIC,
        imperialValueRange = BODY_MIN_VALUE..BODY_MAX_WEIGHT_IMPERIAL,
    ),
    Length(
        metricValueRange = BODY_MIN_VALUE..BODY_MAX_LENGTH_METRIC,
        imperialValueRange = BODY_MIN_VALUE..BODY_MAX_LENGTH_IMPERIAL,
    ),
    LengthTwoSides(
        metricValueRange = BODY_MIN_VALUE..BODY_MAX_LENGTH_METRIC,
        imperialValueRange = BODY_MIN_VALUE..BODY_MAX_LENGTH_IMPERIAL,
    ),
    Percentage(
        metricValueRange = BODY_MIN_VALUE..BODY_MAX_PERCENTAGE,
        imperialValueRange = BODY_MIN_VALUE..BODY_MAX_PERCENTAGE,
    ),
}

fun BodyMeasurementType.getValueRange(unit: ValueUnit): ClosedFloatingPointRange<Double> =
    when {
        unit.isMetric -> metricValueRange
        else -> imperialValueRange
    }
