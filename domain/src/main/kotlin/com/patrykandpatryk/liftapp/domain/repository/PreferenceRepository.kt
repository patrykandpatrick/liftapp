package com.patrykandpatryk.liftapp.domain.repository

import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.model.AllPreferences
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykmichalik.opto.domain.Preference
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    val massUnit: Preference<MassUnit, String, *>
    val distanceUnit: Preference<DistanceUnit, String, *>
    val hourFormat: Preference<HourFormat, String, *>

    val is24H: Flow<Boolean>

    val allPreferences: Flow<AllPreferences>
}
