package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("longDistance")
enum class LongDistanceUnit(val kilometerConversion: Double, val mileConversion: Double) :
    ValueUnit {

    @SerialName("kilometer") Kilometer(kilometerConversion = 1.0, mileConversion = 0.6213688756),
    @SerialName("mile") Mile(kilometerConversion = 1.60935, mileConversion = 1.0);

    override val hasLeadingSpace: Boolean = true

    override val isMetric: Boolean
        get() = this == Kilometer

    fun toKilometers(value: Double): Double = value * kilometerConversion

    fun toMiles(value: Double): Double = value * mileConversion

    fun convert(value: Double, to: LongDistanceUnit): Double =
        when (to) {
            Kilometer -> toKilometers(value)
            Mile -> toMiles(value)
        }

    fun getCorrespondingMediumDistanceUnit(): MediumDistanceUnit =
        when (this) {
            Kilometer -> MediumDistanceUnit.Meter
            Mile -> MediumDistanceUnit.Foot
        }

    fun getCorrespondingShortDistanceUnit(): ShortDistanceUnit =
        when (this) {
            Kilometer -> ShortDistanceUnit.Centimeter
            Mile -> ShortDistanceUnit.Inch
        }
}

@Serializable
@SerialName("mediumDistance")
enum class MediumDistanceUnit(val meterConversion: Double, val footConversion: Double) : ValueUnit {

    @SerialName("meter") Meter(meterConversion = 1.0, footConversion = 3.280839895),
    @SerialName("foot") Foot(meterConversion = 0.3048, footConversion = 1.0);

    override val hasLeadingSpace: Boolean = true

    override val isMetric: Boolean
        get() = this == Meter

    fun toMeters(value: Double): Double = value * meterConversion

    fun toFeet(value: Double): Double = value * footConversion
}

@Serializable
@SerialName("shortDistance")
enum class ShortDistanceUnit(val centimeterConversion: Double, val inchConversion: Double) :
    ValueUnit {

    @SerialName("centimeter") Centimeter(centimeterConversion = 1.0, inchConversion = 0.3937007874),
    @SerialName("inch") Inch(centimeterConversion = 2.54, inchConversion = 1.0);

    override val hasLeadingSpace: Boolean = true

    override val isMetric: Boolean
        get() = this == Centimeter

    fun toCentimeters(value: Double): Double = value * centimeterConversion

    fun toInch(value: Double): Double = value * inchConversion
}
