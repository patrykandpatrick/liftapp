package com.patrykandpatryk.liftapp.domain.unit

enum class DistanceUnit(
    val kilometersConversion: Double,
    val milesConversion: Double,
) {
    Kilometers(
        kilometersConversion = 1.0,
        milesConversion = 0.6213688756,
    ),
    Miles(
        kilometersConversion = 1.60935,
        milesConversion = 1.0,
    );

    fun toKilometers(value: Float): Float = (value * kilometersConversion).toFloat()

    fun toMiles(value: Float): Float = (value * milesConversion).toFloat()
}
