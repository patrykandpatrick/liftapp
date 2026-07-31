package com.patrykandpatrick.liftapp.functionality.backup.work

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupScheduler
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupSettings
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Turns [AutoBackupSettings] into WorkManager work.
 *
 * The first run is delayed to the coming midnight, as the published app did: a backup taken while
 * the user is mid-workout is both noisier and less useful than one taken after the day is over.
 */
@Singleton
class BackupScheduler @Inject constructor(application: Application) : AutoBackupScheduler {

    private val workManager = WorkManager.getInstance(application)

    override fun reschedule(settings: AutoBackupSettings) {
        if (!settings.isRunnable) {
            Timber.i("Cancelling automatic backups.")
            workManager.cancelUniqueWork(AutoBackupWorker.WORK_NAME)
            return
        }

        val interval = Duration.ofDays(settings.interval.days.toLong())
        val request =
            PeriodicWorkRequestBuilder<AutoBackupWorker>(interval)
                .setInitialDelay(delayUntilMidnight())
                .build()

        // REPLACE rather than KEEP: this is called because something about the schedule changed.
        workManager.enqueueUniquePeriodicWork(
            AutoBackupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            request,
        )
        Timber.i("Automatic backups scheduled every ${settings.interval.days} day(s).")
    }

    private fun delayUntilMidnight(): Duration =
        Duration.between(
            LocalDateTime.now(),
            LocalDate.now().plusDays(1).atTime(LocalTime.MIDNIGHT),
        )
}
