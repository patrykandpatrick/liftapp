package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BodyMeasurementValue {

    abstract val unit: ValueUnit

    @Serializable
    @SerialName("single")
    data class Single(
        val value: Float,
        override val unit: ValueUnit,
    ) : BodyMeasurementValue()

    @Serializable
    @SerialName("double")
    data class Double(
        val left: Float,
        val right: Float,
        override val unit: ValueUnit,
    ) : BodyMeasurementValue()
}
