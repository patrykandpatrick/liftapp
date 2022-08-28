package com.patrykandpatryk.liftapp.domain.repository

import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.model.AllPreferences
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykmichalik.opto.domain.Preference
import kotlinx.coroutines.flow.Flow

interface PreferenceRepository {

    val massUnit: Preference<MassUnit, String, *>
    val longDistanceUnit: Preference<LongDistanceUnit, String, *>
    val mediumDistanceUnit: Flow<MediumDistanceUnit>
    val shortDistanceUnit: Flow<ShortDistanceUnit>
    val hourFormat: Preference<HourFormat, String, *>

    val is24H: Flow<Boolean>

    val allPreferences: Flow<AllPreferences>
}
