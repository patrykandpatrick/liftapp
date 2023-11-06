package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mass")
enum class MassUnit(
    val kilogramsConversion: Double,
    val poundsConversion: Double,
) : ValueUnit {

    @SerialName("kilograms")
    Kilograms(
        kilogramsConversion = 1.0,
        poundsConversion = 2.2046244202,
    ),

    @SerialName("pounds")
    Pounds(
        kilogramsConversion = 0.453592,
        poundsConversion = 1.0,
    ),
    ;

    override val hasLeadingSpace: Boolean = true

    fun toKilograms(value: Float): Float = (value * kilogramsConversion).toFloat()

    fun toPounds(value: Float): Float = (value * poundsConversion).toFloat()
}
