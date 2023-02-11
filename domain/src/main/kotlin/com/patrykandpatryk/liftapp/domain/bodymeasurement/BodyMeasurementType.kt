package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_LENGTH_IMPERIAL
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_LENGTH_METRIC
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_PERCENTAGE
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_WEIGHT_IMPERIAL
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MAX_WEIGHT_METRIC
import com.patrykandpatryk.liftapp.domain.Constants.Input.BODY_MIN_VALUE
import kotlinx.serialization.Serializable

@Serializable
enum class BodyMeasurementType(
    val fields: Int,
    val metricValueRange: ClosedFloatingPointRange<Float>,
    val imperialValueRange: ClosedFloatingPointRange<Float>,
) {
    Weight(
        fields = 1,
        metricValueRange = BODY_MIN_VALUE..BODY_MAX_WEIGHT_METRIC,
        imperialValueRange = BODY_MIN_VALUE..BODY_MAX_WEIGHT_IMPERIAL,
    ),
    Length(
        fields = 1,
        metricValueRange = BODY_MIN_VALUE..BODY_MAX_LENGTH_METRIC,
        imperialValueRange = BODY_MIN_VALUE..BODY_MAX_LENGTH_IMPERIAL,
    ),
    LengthTwoSides(
        fields = 2,
        metricValueRange = BODY_MIN_VALUE..BODY_MAX_LENGTH_METRIC,
        imperialValueRange = BODY_MIN_VALUE..BODY_MAX_LENGTH_IMPERIAL,
    ),
    Percentage(
        fields = 1,
        metricValueRange = BODY_MIN_VALUE..BODY_MAX_PERCENTAGE,
        imperialValueRange = BODY_MIN_VALUE..BODY_MAX_PERCENTAGE,
    ),
}
