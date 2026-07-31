package com.patrykandpatrick.liftapp.functionality.preference.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupSettings
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.domain.backup.BackupPreferenceRepository
import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.functionality.preference.datastore.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.json.Json

private const val KEY_AUTO_BACKUP = "auto_backup"
private const val KEY_LAST_EXPORT_DESTINATION = "last_export_destination"

@Singleton
class BackupPreferenceRepositoryImpl
@Inject
constructor(override val dataStore: DataStore<Preferences>, private val json: Json) :
    BackupPreferenceRepository, PreferenceManager {

    override val autoBackup: Preference<AutoBackupSettings> =
        preference(
            key = stringPreferencesKey(KEY_AUTO_BACKUP),
            defaultValue = AutoBackupSettings(),
            serialize = { json.encodeToString(it) },
            deserialize = { json.decodeFromString(it) },
        )

    override val lastExportDestination: Preference<BackupLocation?> =
        preference(
            key = stringPreferencesKey(KEY_LAST_EXPORT_DESTINATION),
            defaultValue = null,
            serialize = { it?.value.orEmpty() },
            deserialize = { it.takeIf(String::isNotEmpty)?.let(::BackupLocation) },
        )
}
