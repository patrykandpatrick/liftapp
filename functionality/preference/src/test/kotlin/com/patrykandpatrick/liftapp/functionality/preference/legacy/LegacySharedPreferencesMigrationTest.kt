package com.patrykandpatrick.liftapp.functionality.preference.legacy

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.core.app.ApplicationProvider
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class LegacySharedPreferencesMigrationTest {

    private val application = ApplicationProvider.getApplicationContext<Application>()
    private val json = DomainModule.provideJson(emptySet())
    private val migration =
        LegacySharedPreferencesMigration(application, json) { LocalDate.of(2026, 7, 31) }

    private fun writeLegacyPreferences(vararg values: Pair<String, Any>) {
        val editor =
            application
                .getSharedPreferences(
                    "${application.packageName}_preferences",
                    Context.MODE_PRIVATE,
                )
                .edit()
        values.forEach { (key, value) ->
            when (value) {
                is String -> editor.putString(key, value)
                is Boolean -> editor.putBoolean(key, value)
                else -> error("Unexpected value type for $key.")
            }
        }
        editor.apply()
    }

    @Test
    fun `maps the published settings onto the DataStore keys`() = runTest {
        writeLegacyPreferences(
            "list_preference_weight" to "1",
            "list_preference_distance" to "1",
            "key_app_theme" to "3",
            "list_preference_first_day_of_week" to "1",
            "key_auto_backup_enabled" to true,
            "key_auto_backup_frequency" to "3",
            "key_auto_backup_delete" to "30",
        )

        assertTrue(migration.shouldMigrate(emptyPreferences()))
        val preferences = migration.migrate(emptyPreferences())

        assertEquals("Pounds", preferences[stringPreferencesKey("mass_unit")])
        assertEquals("Mile", preferences[stringPreferencesKey("distance_unit")])
        // "3" was battery saver, which follows the system now.
        assertEquals("FollowSystem", preferences[stringPreferencesKey("theme")])
        assertEquals("SUNDAY", preferences[stringPreferencesKey("first_day_of_week")])
        // The destination and the enabled flag stay behind on purpose; see LegacyPreferences.
        assertEquals(
            """{"interval":"EveryThreeDays","retention":"OneMonth"}""",
            preferences[stringPreferencesKey("auto_backup")],
        )
        assertNull(preferences[stringPreferencesKey("active_plan_id")])
    }

    @Test
    fun `a scheduled plan becomes the active plan with the default cycle count`() = runTest {
        writeLegacyPreferences("plan_ids" to "100 -1 101 -2")

        val preferences = migration.migrate(emptyPreferences())

        assertEquals(
            """{"planID":1,"startDate":"2026-07-31","cycleCount":6}""",
            preferences[stringPreferencesKey("active_plan_id")],
        )
    }

    @Test
    fun `runs once and leaves the published file in place`() = runTest {
        writeLegacyPreferences("list_preference_weight" to "1")

        val preferences = migration.migrate(emptyPreferences())
        migration.cleanUp()

        assertEquals(true, preferences[booleanPreferencesKey("legacy_preferences_migrated")])
        assertFalse(migration.shouldMigrate(preferences))
        assertEquals(
            "1",
            application
                .getSharedPreferences(
                    "${application.packageName}_preferences",
                    Context.MODE_PRIVATE,
                )
                .getString("list_preference_weight", null),
        )
    }

    @Test
    fun `a fresh install migrates nothing but is marked done`() = runTest {
        val preferences = migration.migrate(emptyPreferences())

        assertEquals(true, preferences[booleanPreferencesKey("legacy_preferences_migrated")])
        assertNull(preferences[stringPreferencesKey("mass_unit")])
        assertNull(preferences[stringPreferencesKey("active_plan_id")])
    }
}
