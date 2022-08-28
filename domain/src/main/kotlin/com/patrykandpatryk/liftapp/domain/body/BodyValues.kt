package com.patrykandpatryk.liftapp.domain.body

import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class BodyValues {

    abstract val unit: ValueUnit

    @Serializable
    @SerialName("single")
    data class Single(
        val value: Float,
        override val unit: ValueUnit,
    ) : BodyValues()

    @Serializable
    @SerialName("double")
    data class Double(
        val left: Float,
        val right: Float,
        override val unit: ValueUnit,
    ) : BodyValues()
}
