package com.patrykandpatryk.liftapp.functionality.preference.repository

import android.app.Application
import android.text.format.DateFormat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.model.AllPreferences
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykmichalik.opto.core.PreferenceImpl
import com.patrykmichalik.opto.core.PreferenceManager
import com.patrykmichalik.opto.core.getFromPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val KEY_MASS_UNIT = "mass_unit"
private const val KEY_DISTANCE_UNIT = "distance_unit"
private const val KEY_HOUR_FORMAT = "hour_format"

class PreferenceRepositoryImpl @Inject constructor(
    override val preferencesDataStore: DataStore<Preferences>,
    private val application: Application,
) : PreferenceRepository, PreferenceManager {

    override val massUnit = enumPreference(KEY_MASS_UNIT, MassUnit.Kilograms)

    override val distanceUnit = enumPreference(KEY_DISTANCE_UNIT, DistanceUnit.Kilometers)

    override val hourFormat = enumPreference(KEY_HOUR_FORMAT, HourFormat.Auto)

    override val is24H: Flow<Boolean>
        get() = hourFormat.get()
            .map { preferenceHourFormat ->
                when (preferenceHourFormat) {
                    HourFormat.Auto -> DateFormat.is24HourFormat(application)
                    HourFormat.H12 -> false
                    HourFormat.H24 -> true
                }
            }

    override val allPreferences = preferencesDataStore.data.map { preferences ->
        AllPreferences(
            massUnit = massUnit.getFromPreferences(preferences = preferences),
            distanceUnit = distanceUnit.getFromPreferences(preferences = preferences),
            hourFormat = hourFormat.getFromPreferences(preferences = preferences),
        )
    }
}

private inline fun <reified E : Enum<E>> PreferenceManager.enumPreference(
    key: String,
    defaultValue: E,
): PreferenceImpl<E, String> = preference(
    stringPreferencesKey(key),
    defaultValue = defaultValue,
    parse = { E::class.java.getMethod("valueOf", String::class.java).invoke(null, it) as E },
    save = { it.toString() },
)
