package com.patrykandpatryk.liftapp.functionality.preference.repository

import android.app.Application
import android.text.format.DateFormat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.patrykandpatrick.opto.core.PreferenceImpl
import com.patrykandpatrick.opto.core.PreferenceManager
import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.model.AllPreferences
import com.patrykandpatryk.liftapp.domain.plan.ActivePlan
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.MediumDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val KEY_MASS_UNIT = "mass_unit"
private const val KEY_DISTANCE_UNIT = "distance_unit"
private const val KEY_HOUR_FORMAT = "hour_format"
private const val KEY_GOAL_INFO_VISIBLE = "goal_info_visible"
private const val KEY_ACTIVE_PLAN_ID = "active_plan_id"

class PreferenceRepositoryImpl
@Inject
constructor(
    override val dataStore: DataStore<Preferences>,
    private val application: Application,
    private val json: Json,
) : PreferenceRepository, PreferenceManager {

    override val massUnit = enumPreference(KEY_MASS_UNIT, MassUnit.Kilograms)

    override val longDistanceUnit = enumPreference(KEY_DISTANCE_UNIT, LongDistanceUnit.Kilometer)

    override val mediumDistanceUnit: Flow<MediumDistanceUnit> =
        longDistanceUnit.get().map { longDistanceUnit ->
            longDistanceUnit.getCorrespondingMediumDistanceUnit()
        }

    override val shortDistanceUnit: Flow<ShortDistanceUnit> =
        longDistanceUnit.get().map { longDistanceUnit ->
            longDistanceUnit.getCorrespondingShortDistanceUnit()
        }

    override val hourFormat = enumPreference(KEY_HOUR_FORMAT, HourFormat.Auto)

    override val goalInfoVisible: Preference<Boolean> =
        preference(booleanPreferencesKey(KEY_GOAL_INFO_VISIBLE), true)

    override val activePlan: Preference<ActivePlan?> =
        preference(
            key = stringPreferencesKey(KEY_ACTIVE_PLAN_ID),
            defaultValue = null,
            serialize = { json.encodeToString(it) },
            deserialize = { json.decodeFromString(it) },
        )

    override val is24H: Flow<Boolean>
        get() =
            hourFormat.get().map { preferenceHourFormat ->
                when (preferenceHourFormat) {
                    HourFormat.Auto -> DateFormat.is24HourFormat(application)
                    HourFormat.H12 -> false
                    HourFormat.H24 -> true
                }
            }

    override val allPreferences =
        dataStore.data.map { preferences ->
            val longDistanceUnit = longDistanceUnit.get(preferences)

            AllPreferences(
                massUnit = massUnit.get(preferences),
                longDistanceUnit = longDistanceUnit,
                mediumDistanceUnit = longDistanceUnit.getCorrespondingMediumDistanceUnit(),
                shortDistanceUnit = longDistanceUnit.getCorrespondingShortDistanceUnit(),
                hourFormat = hourFormat.get(preferences),
            )
        }
}

private inline fun <reified E : Enum<E>> PreferenceManager.enumPreference(
    key: String,
    defaultValue: E,
): PreferenceImpl<E, String> =
    preference(
        stringPreferencesKey(key),
        defaultValue = defaultValue,
        serialize = { it.toString() },
        deserialize = {
            E::class.java.getMethod("valueOf", String::class.java).invoke(null, it) as E
        },
    )
