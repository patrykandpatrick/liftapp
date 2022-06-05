package com.patrykandpatryk.liftapp.domain.repository

import com.patrykandpatryk.liftapp.domain.model.AllPreferences
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykmichalik.preferencemanager.domain.Preference
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    val massUnit: Preference<MassUnit, String, *>
    val distanceUnit: Preference<DistanceUnit, String, *>
    val allPreferences: Flow<AllPreferences>
}
