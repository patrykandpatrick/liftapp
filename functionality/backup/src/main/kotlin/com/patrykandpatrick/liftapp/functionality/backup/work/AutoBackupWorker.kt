package com.patrykandpatrick.liftapp.functionality.backup.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.backup.BackupPreferenceRepository
import com.patrykandpatrick.liftapp.functionality.backup.BackupRepository
import com.patrykandpatrick.liftapp.functionality.backup.storage.DocumentStorage
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * Writes a full backup into the folder the user chose, then sweeps away the scheduled backups that
 * have outlived the retention setting.
 *
 * Dependencies come from an entry point rather than `@HiltWorker`, which keeps the androidx Hilt
 * annotation processor — and a `Configuration.Provider` on the application — out of the build for
 * the sake of one worker.
 */
class AutoBackupWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        fun backupRepository(): BackupRepository

        fun backupPreferences(): BackupPreferenceRepository

        fun documentStorage(): DocumentStorage

        fun notifier(): AutoBackupNotifier
    }

    override suspend fun doWork(): Result {
        val dependencies =
            EntryPointAccessors.fromApplication(applicationContext, Dependencies::class.java)
        val settings = dependencies.backupPreferences().autoBackup.get().first()
        val destination = settings.destination

        if (!settings.enabled || destination == null) {
            Timber.i("Automatic backups are off; nothing to do.")
            return Result.success()
        }

        if (!dependencies.documentStorage().hasAccess(destination)) {
            Timber.w("Lost access to the automatic backup folder.")
            dependencies.notifier().notifyFailed()
            return Result.failure()
        }

        return try {
            val repository = dependencies.backupRepository()
            val file =
                repository.exportBackup(
                    directory = destination,
                    types = BackupDataType.entries.toSet(),
                    automatic = true,
                )
            Timber.i("Wrote the automatic backup ${file.name}.")
            repository.deleteExpiredBackups(destination, settings.retention.days)
            Result.success()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "The automatic backup failed.")
            dependencies.notifier().notifyFailed()
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "auto_backup"
    }
}
