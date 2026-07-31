package com.patrykandpatrick.liftapp.functionality.backup.work

import android.Manifest
import android.app.Application
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

/**
 * Tells the user when a scheduled backup did not happen.
 *
 * Automatic backups fail silently otherwise — usually because the folder they were pointed at is
 * gone, or because the app lost the permission the user granted — and the whole point of them is
 * that nobody is watching.
 */
@Singleton
class AutoBackupNotifier
@Inject
constructor(private val application: Application, private val stringProvider: StringProvider) {

    private val notificationManager = NotificationManagerCompat.from(application)

    fun notifyFailed() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    application,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        createChannel()
        val notification =
            NotificationCompat.Builder(application, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_notify_error)
                .setContentTitle(stringProvider.backupFailedNotificationTitle)
                .setContentText(stringProvider.backupFailedNotificationBody)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(stringProvider.backupFailedNotificationBody)
                )
                .setAutoCancel(true)
                .build()

        // Without the notification permission this is dropped by the system, which is the right
        // outcome: the failure is also visible on the Back up & restore screen.
        runCatching { notificationManager.notify(NOTIFICATION_ID, notification) }
            .onFailure { Timber.w(it, "Could not post the backup failure notification.") }
    }

    private fun createChannel() {
        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT)
                .setName(stringProvider.backupNotificationChannelName)
                .build()
        )
    }

    private companion object {
        const val CHANNEL_ID = "auto_backup"
        const val NOTIFICATION_ID = 1_001
    }
}
