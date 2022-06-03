package com.patrykandpatryk.liftapp.functionality.preference.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykmichalik.preferencemanager.core.PreferenceManager
import com.patrykmichalik.preferencemanager.domain.Preference
import javax.inject.Inject

private const val KEY_MASS_UNIT = "mass_unit"
private const val KEY_DISTANCE_UNIT = "distance_unit"

class PreferenceRepositoryImpl @Inject constructor(
    override val preferencesDataStore: DataStore<Preferences>,
) : PreferenceRepository, PreferenceManager {

    override val massUnit = enumPreference(KEY_MASS_UNIT, MassUnit.Kilograms)
    override val distanceUnit = enumPreference(KEY_DISTANCE_UNIT, DistanceUnit.Kilometers)
}

private inline fun <reified E : Enum<E>> PreferenceManager.enumPreference(
    key: String,
    defaultValue: E,
): Preference<E, String, *> = preference(
    stringPreferencesKey(key),
    defaultValue = defaultValue,
    parse = { E::class.java.getMethod("valueOf", String::class.java).invoke(null, it) as E },
    save = { it.toString() },
)
