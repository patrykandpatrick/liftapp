package com.patrykandpatryk.liftapp.domain.unit

interface UnitConverter {

    suspend fun convertToPreferredUnit(from: LongDistanceUnit, value: Double): Double

    suspend fun convertToPreferredUnit(from: MediumDistanceUnit, value: Double): Double

    suspend fun convertToPreferredUnit(from: ShortDistanceUnit, value: Double): Double

    suspend fun convertToPreferredUnit(from: MassUnit, value: Double): Double

    suspend fun convertToPreferredUnitAndFormat(from: ValueUnit, vararg values: Double): String
}
