package com.patrykandpatrick.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("mass")
enum class MassUnit(val kilogramsConversion: Double, val poundsConversion: Double) : ValueUnit {

    @SerialName("kilograms") Kilograms(kilogramsConversion = 1.0, poundsConversion = 2.2046244202),
    @SerialName("pounds") Pounds(kilogramsConversion = 0.453592, poundsConversion = 1.0);

    override val hasLeadingSpace: Boolean = true

    override val isMetric: Boolean
        get() = this == Kilograms

    fun toKilograms(value: Double): Double = value * kilogramsConversion

    fun toPounds(value: Double): Double = value * poundsConversion

    /**
     * The inverse of [toKilograms]: [kilograms] expressed in this unit. Arithmetic that only holds
     * in kilograms — the one-rep max curve, whose `ln w` term is unit-dependent — needs this to
     * hand its answer back in the unit it was given.
     */
    fun fromKilograms(kilograms: Double): Double =
        when (this) {
            Kilograms -> kilograms
            Pounds -> Kilograms.toPounds(kilograms)
        }
}
