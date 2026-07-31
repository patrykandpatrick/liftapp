package com.patrykandpatrick.liftapp.functionality.preference.legacy

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupSettings
import com.patrykandpatrick.liftapp.domain.backup.BackupInterval
import com.patrykandpatrick.liftapp.domain.backup.BackupRetention
import com.patrykandpatrick.liftapp.domain.theme.Theme
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import java.time.DayOfWeek
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * The published app's `SharedPreferences` values, mapped onto this app's DataStore keys. The
 * preference migration reads them from the live preferences file on the first launch after the
 * update, and the backup importer reads them from the XML file inside a legacy archive.
 *
 * Every value is a string, even where it reads as a number or a boolean. The auto-backup keys keep
 * only the interval and the retention: the destination is a document-tree URI whose grant does not
 * survive a reinstall, and re-enabling backups silently — nothing schedules the work until the
 * setting is toggled — would only look like the user is covered when they are not.
 */
class LegacyPreferences(private val values: Map<String, String>) {

    /** The scheduled routine cycle: positive values are routine IDs, negatives are rest days. */
    val planIDs: List<Long> =
        Regex("-?\\d+")
            .findAll(values[Constants.LegacyApp.PLAN_IDS_KEY].orEmpty())
            .map { it.value.toLong() }
            .toList()

    val hasSettings: Boolean = values.keys.any { it in SUPPORTED_SETTING_KEYS }

    fun edit(json: Json): (MutablePreferences) -> Unit = { preferences ->
        values[OLD_MASS_UNIT]?.let { value ->
            preferences[stringPreferencesKey(NEW_MASS_UNIT)] =
                if (value == "1") MassUnit.Pounds.name else MassUnit.Kilograms.name
        }
        values[OLD_DISTANCE_UNIT]?.let { value ->
            preferences[stringPreferencesKey(NEW_DISTANCE_UNIT)] =
                if (value == "1") LongDistanceUnit.Mile.name else LongDistanceUnit.Kilometer.name
        }
        values[OLD_THEME]?.let { value ->
            preferences[stringPreferencesKey(NEW_THEME)] =
                when (value) {
                    "1" -> Theme.Light
                    "2" -> Theme.Dark
                    // "3", battery saver, was dropped by Android Q; following the system is what
                    // the published app fell back to there.
                    else -> Theme.FollowSystem
                }.name
        }
        values[OLD_FIRST_DAY]?.toIntOrNull()?.toDayOfWeek()?.let { day ->
            preferences[stringPreferencesKey(NEW_FIRST_DAY)] = day.name
        }
        if (values.keys.any { it in OLD_AUTO_BACKUP_KEYS }) {
            val intervalDays = values[OLD_BACKUP_INTERVAL]?.toIntOrNull()
            val retentionDays = values[OLD_BACKUP_RETENTION]?.toIntOrNull()
            val settings =
                AutoBackupSettings(
                    enabled = false,
                    destination = null,
                    interval =
                        BackupInterval.entries.firstOrNull { it.days == intervalDays }
                            ?: BackupInterval.Daily,
                    retention =
                        BackupRetention.entries.firstOrNull { it.days == retentionDays }
                            ?: BackupRetention.TwoWeeks,
                )
            preferences[stringPreferencesKey(NEW_AUTO_BACKUP)] = json.encodeToString(settings)
        }
    }

    companion object {
        /** Reads the entries of a `SharedPreferences` XML file. */
        fun read(xml: String): LegacyPreferences {
            if (!xml.contains("<map")) return LegacyPreferences(emptyMap())
            val values = mutableMapOf<String, String>()
            STRING_ENTRY.findAll(xml).forEach { match ->
                val name = NAME_ATTRIBUTE.find(match.groupValues[1])?.groupValues?.get(1)
                if (name != null) values[xmlDecode(name)] = xmlDecode(match.groupValues[2])
            }
            BOOLEAN_ENTRY.findAll(xml).forEach { match ->
                val attributes = match.groupValues[1]
                val name = NAME_ATTRIBUTE.find(attributes)?.groupValues?.get(1)
                val value = VALUE_ATTRIBUTE.find(attributes)?.groupValues?.get(1)
                if (name != null && value != null) values[xmlDecode(name)] = xmlDecode(value)
            }
            return LegacyPreferences(values)
        }

        private val STRING_ENTRY =
            Regex("<string\\b([^>]*)>(.*?)</string>", setOf(RegexOption.DOT_MATCHES_ALL))
        private val BOOLEAN_ENTRY = Regex("<boolean\\b([^>]*)/?>")
        private val NAME_ATTRIBUTE = Regex("\\bname=\"([^\"]*)\"")
        private val VALUE_ATTRIBUTE = Regex("\\bvalue=\"([^\"]*)\"")

        private const val OLD_MASS_UNIT = "list_preference_weight"
        private const val OLD_DISTANCE_UNIT = "list_preference_distance"
        private const val OLD_THEME = "key_app_theme"
        private const val OLD_FIRST_DAY = "list_preference_first_day_of_week"
        private const val OLD_BACKUP_ENABLED = "key_auto_backup_enabled"
        private const val OLD_BACKUP_DESTINATION = "key_auto_backup_dir"
        private const val OLD_BACKUP_INTERVAL = "key_auto_backup_frequency"
        private const val OLD_BACKUP_RETENTION = "key_auto_backup_delete"

        private const val NEW_MASS_UNIT = "mass_unit"
        private const val NEW_DISTANCE_UNIT = "distance_unit"
        private const val NEW_THEME = "theme"
        private const val NEW_FIRST_DAY = "first_day_of_week"
        private const val NEW_AUTO_BACKUP = "auto_backup"

        private val OLD_AUTO_BACKUP_KEYS =
            setOf(
                OLD_BACKUP_ENABLED,
                OLD_BACKUP_DESTINATION,
                OLD_BACKUP_INTERVAL,
                OLD_BACKUP_RETENTION,
            )
        private val SUPPORTED_SETTING_KEYS =
            OLD_AUTO_BACKUP_KEYS + setOf(OLD_MASS_UNIT, OLD_DISTANCE_UNIT, OLD_THEME, OLD_FIRST_DAY)
    }
}

private fun xmlDecode(value: String): String =
    value
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")

/** `java.util.Calendar` numbers Sunday through Saturday 1 to 7. */
private fun Int.toDayOfWeek(): DayOfWeek? =
    when (this) {
        1 -> DayOfWeek.SUNDAY
        2 -> DayOfWeek.MONDAY
        3 -> DayOfWeek.TUESDAY
        4 -> DayOfWeek.WEDNESDAY
        5 -> DayOfWeek.THURSDAY
        6 -> DayOfWeek.FRIDAY
        7 -> DayOfWeek.SATURDAY
        else -> null
    }
