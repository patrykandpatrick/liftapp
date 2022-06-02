package com.patrykandpatryk.liftapp.domain.repository

import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    val massUnit: Flow<MassUnit>

    val distanceUnit: Flow<DistanceUnit>

    suspend fun setMassUnit(massUnit: MassUnit)

    suspend fun setDistanceUnit(distanceUnit: DistanceUnit)
}
