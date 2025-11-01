package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BodyMeasurementValue {

    abstract val unit: ValueUnit

    @Serializable
    @SerialName("single")
    data class SingleValue(val value: Double, override val unit: ValueUnit) : BodyMeasurementValue()

    @Serializable
    @SerialName("double")
    data class DoubleValue(val left: Double, val right: Double, override val unit: ValueUnit) :
        BodyMeasurementValue()
}
