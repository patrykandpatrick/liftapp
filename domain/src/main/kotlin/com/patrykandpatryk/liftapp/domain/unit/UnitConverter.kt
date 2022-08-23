package com.patrykandpatryk.liftapp.domain.unit

interface UnitConverter {

    suspend fun convertToPreferredUnit(from: DistanceUnit, value: Float): Float

    suspend fun convertToPreferredUnit(from: MassUnit, value: Float): Float
}
