package com.patrykandpatrick.liftapp.domain.preference

import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.model.AllPreferences
import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.ShortDistanceUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PreferenceRepository {

    val massUnit: Preference<MassUnit>
    val longDistanceUnit: Preference<LongDistanceUnit>
    val mediumDistanceUnit: Flow<MediumDistanceUnit>
    val shortDistanceUnit: Flow<ShortDistanceUnit>
    val hourFormat: Preference<HourFormat>
    val goalInfoVisible: Preference<Boolean>

    val activePlan: Preference<ActivePlan?>

    val is24H: StateFlow<Boolean>

    val allPreferences: Flow<AllPreferences>
}
