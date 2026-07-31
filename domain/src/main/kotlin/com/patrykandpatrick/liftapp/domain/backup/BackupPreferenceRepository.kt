package com.patrykandpatrick.liftapp.domain.backup

import com.patrykandpatrick.liftapp.domain.datastore.Preference

interface BackupPreferenceRepository {

    val autoBackup: Preference<AutoBackupSettings>

    /** Where the last manual export went, so the next one can offer the same place. */
    val lastExportDestination: Preference<BackupLocation?>
}

/** Applies [AutoBackupSettings] to whatever actually runs the backups. */
fun interface AutoBackupScheduler {
    /** Cancels any scheduled work and schedules it again from [settings]. */
    fun reschedule(settings: AutoBackupSettings)
}
