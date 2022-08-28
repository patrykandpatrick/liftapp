package com.patrykandpatryk.liftapp.domain.unit

interface UnitConverter {

    suspend fun convertToPreferredUnit(from: LongDistanceUnit, value: Float): Float

    suspend fun convertToPreferredUnit(from: MediumDistanceUnit, value: Float): Float

    suspend fun convertToPreferredUnit(from: ShortDistanceUnit, value: Float): Float

    suspend fun convertToPreferredUnit(from: MassUnit, value: Float): Float

    suspend fun convertToPreferredUnitAndFormat(from: ValueUnit, vararg values: Float): String
}
