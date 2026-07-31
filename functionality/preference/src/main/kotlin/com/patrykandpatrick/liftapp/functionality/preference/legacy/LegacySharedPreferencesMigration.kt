package com.patrykandpatrick.liftapp.functionality.preference.legacy

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import java.time.LocalDate
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Moves the published app's `SharedPreferences` into the DataStore on the first launch after the
 * update.
 *
 * Beyond the settings mapped by [LegacyPreferences], a non-empty `plan_ids` value marks the plan
 * that the database's `Migration11To12` recreates as active, starting today with the default cycle
 * count. Both migrations read the same preference and use the same fixed plan ID, so neither needs
 * the other to have run first; if the database side never produces the plan — nothing but the
 * preferences survived, say — the stale active-plan preference heals itself on first read.
 *
 * The old file is left untouched: the database migration reads `plan_ids` from it on its own
 * schedule, and a marker key makes this migration run once instead of clobbering later edits.
 */
class LegacySharedPreferencesMigration(
    private val application: Application,
    private val json: Json,
    private val startDate: () -> LocalDate = LocalDate::now,
) : DataMigration<Preferences> {

    override suspend fun shouldMigrate(currentData: Preferences): Boolean =
        currentData[MIGRATED] != true

    override suspend fun migrate(currentData: Preferences): Preferences {
        val legacyValues =
            application
                .getSharedPreferences(
                    Constants.LegacyApp.preferencesFileName(application.packageName),
                    Context.MODE_PRIVATE,
                )
                .all
                .mapNotNull { (key, value) -> value?.let { key to it.toString() } }
                .toMap()
        val legacy = LegacyPreferences(legacyValues)

        val preferences = currentData.toMutablePreferences()
        legacy.edit(json)(preferences)
        if (legacy.planIDs.isNotEmpty()) {
            preferences[ACTIVE_PLAN] =
                json.encodeToString(
                    ActivePlan(
                        planID = Constants.LegacyApp.PLAN_ID,
                        startDate = startDate(),
                        cycleCount = Constants.TrainingPlan.DEFAULT_CYCLE_COUNT,
                    )
                )
        }
        preferences[MIGRATED] = true
        return preferences.toPreferences()
    }

    override suspend fun cleanUp() = Unit

    private companion object {
        val MIGRATED = booleanPreferencesKey("legacy_preferences_migrated")

        /** Matches the key `PreferenceRepositoryImpl` reads the active plan from. */
        val ACTIVE_PLAN = stringPreferencesKey("active_plan_id")
    }
}
