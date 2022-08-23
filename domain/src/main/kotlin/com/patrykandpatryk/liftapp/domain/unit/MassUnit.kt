package com.patrykandpatryk.liftapp.domain.unit

enum class MassUnit(
    val kilogramsConversion: Double,
    val poundsConversion: Double,
) {
    Kilograms(
        kilogramsConversion = 1.0,
        poundsConversion = 2.2046244202,
    ),
    Pounds(
        kilogramsConversion = 0.453592,
        poundsConversion = 1.0,
    );

    fun toKilograms(value: Float): Float = (value * kilogramsConversion).toFloat()

    fun toPounds(value: Float): Float = (value * poundsConversion).toFloat()
}
