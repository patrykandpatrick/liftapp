package com.patrykandpatrick.liftapp.functionality.backup.file

import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class LegacyBackupTest {

    @Test
    fun `recognizes original full backup directories`() {
        val backup =
            LegacyBackup.read(
                archive(
                    "version.txt" to "2",
                    "routines_data/routines.csv" to
                        "id,name,exercise_ids,order_number,strategy\n1,Push,1,0,NORMAL\n",
                    "workouts_data/routine_records.csv" to
                        "id,end_time,routine_id,name,exercise_ids,record_ids\n10,20,1,Push,1,30\n",
                    "body_data/body_records.csv" to
                        "r_id,r_m_id,r_value_left,r_value_right,r_unit\n10,1,80,0,kg\n",
                )
            )

        assertEquals(
            setOf(
                BackupDataType.Routines,
                BackupDataType.Workouts,
                BackupDataType.BodyMeasurements,
            ),
            backup?.contents,
        )
    }

    @Test
    fun `recognizes version one backup without version entry`() {
        val backup =
            LegacyBackup.read(
                archive(
                    "Training-Plans/Plans.csv" to
                        "id,name,exercise_ids,order_number\n1,Push,1 2,0\n"
                )
            )

        assertEquals(setOf(BackupDataType.Routines), backup?.contents)
    }

    @Test
    fun `recognizes old plan and settings stored in shared preferences`() {
        val backup =
            LegacyBackup.read(
                archive(
                    "routines_data/routines.csv" to
                        "id,name,exercise_ids,order_number,strategy\n1,Push,1,0,NORMAL\n",
                    "shared_prefs/pl.patrykgoworowski.mintlift_preferences.xml" to
                        """
                        <map>
                            <string name="plan_ids">1 -1 2</string>
                            <string name="list_preference_weight">1</string>
                            <string name="list_preference_distance">1</string>
                            <string name="key_app_theme">3</string>
                            <string name="list_preference_first_day_of_week">2</string>
                            <boolean name="key_auto_backup_enabled" value="true" />
                            <string name="key_auto_backup_frequency">3</string>
                            <string name="key_auto_backup_delete">30</string>
                        </map>
                        """
                            .trimIndent(),
                )
            )

        assertEquals(
            setOf(
                BackupDataType.Routines,
                BackupDataType.TrainingPlans,
                BackupDataType.Settings,
            ),
            backup?.contents,
        )

        val preferences = mutablePreferencesOf()
        backup?.preferenceEdit(Json)?.invoke(preferences)
        assertEquals("Pounds", preferences[stringPreferencesKey("mass_unit")])
        assertEquals("Mile", preferences[stringPreferencesKey("distance_unit")])
        assertEquals("FollowSystem", preferences[stringPreferencesKey("theme")])
        assertEquals("MONDAY", preferences[stringPreferencesKey("first_day_of_week")])
        assertEquals(
            """{"interval":"EveryThreeDays","retention":"OneMonth"}""",
            preferences[stringPreferencesKey("auto_backup")],
        )
    }

    @Test
    fun `recognizes a settings-only old backup`() {
        val backup =
            LegacyBackup.read(
                archive(
                    "shared_prefs/pl.patrykgoworowski.mintlift_preferences.xml" to
                        "<map><string name=\"key_app_theme\">2</string></map>"
                )
            )

        assertEquals(setOf(BackupDataType.Settings), backup?.contents)
    }

    @Test
    fun `does not accept a zip that only resembles a legacy backup`() {
        val backup =
            LegacyBackup.read(archive("routines_data/routines.csv" to "name,value\nNope,1\n"))

        assertNull(backup)
    }

    @Test
    fun `legacy csv reads commas quotes and line breaks in quoted values`() {
        val rows =
            LegacyCsv.read("id,name,notes\n1,\"Press, dumbbell\",\"one\n\"\"two\"\"\"\n".reader())
                .toList()

        assertEquals(
            listOf(
                listOf("id", "name", "notes"),
                listOf("1", "Press, dumbbell", "one\n\"two\""),
            ),
            rows,
        )
    }

    private fun archive(vararg entries: Pair<String, String>): ByteArrayInputStream {
        val bytes = ByteArrayOutputStream()
        ZipOutputStream(bytes).use { zip ->
            entries.forEach { (name, value) ->
                zip.putNextEntry(ZipEntry(name))
                zip.write(value.toByteArray())
                zip.closeEntry()
            }
        }
        return ByteArrayInputStream(bytes.toByteArray())
    }
}
