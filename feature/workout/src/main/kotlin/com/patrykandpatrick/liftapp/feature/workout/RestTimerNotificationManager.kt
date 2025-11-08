package com.patrykandpatrick.liftapp.feature.workout

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.patrykandpatrick.liftapp.feature.workout.model.RestTimerState
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.deeplink.DeepLink
import com.patrykandpatryk.liftapp.core.time.formattedRemainingTime
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import javax.inject.Inject

class RestTimerNotificationManager
@Inject
constructor(
    private val context: Context,
    private val notificationManager: NotificationManagerCompat,
) {
    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(
                    TIMER_RUNNING_CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_LOW,
                )
                .setName(context.getString(R.string.rest_timer_notification_channel_name))
                .build()
        )

        notificationManager.createNotificationChannel(
            NotificationChannelCompat.Builder(
                    TIMER_FINISHED_CHANNEL_ID,
                    NotificationManagerCompat.IMPORTANCE_MAX,
                )
                .setName(context.getString(R.string.rest_timer_finished_notification_channel_name))
                .setSound(
                    getRawResourceUri(R.raw.notif_rest_timer_end),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT,
                )
                .setVibrationEnabled(true)
                .build()
        )
    }

    private fun getRawResourceUri(resId: Int): Uri =
        Uri.parse("android.resource://${context.packageName}/$resId")

    fun getTimerNotification(timerState: RestTimerState): Notification {
        val remainingTime = timerState.remainingDuration
        val channelId =
            if (timerState.isFinished) TIMER_FINISHED_CHANNEL_ID else TIMER_RUNNING_CHANNEL_ID
        val contentTextRes =
            if (timerState.isFinished) R.string.rest_timer_finished_notification_body
            else R.string.rest_timer_notification_body
        return NotificationCompat.Builder(context, channelId)
            .setContentText(context.getString(contentTextRes))
            .setContentTitle(remainingTime.formattedRemainingTime)
            .setSmallIcon(R.drawable.ic_hourglass_empty)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addActions(timerState.isFinished, timerState.isPaused, timerState.workoutID)
            .build()
    }

    private fun NotificationCompat.Builder.addActions(
        timerIsFinished: Boolean,
        timerIsPaused: Boolean,
        workoutID: Long,
    ): NotificationCompat.Builder {
        addAction(
            -1,
            context.getString(
                if (timerIsFinished) R.string.rest_timer_action_dismiss
                else R.string.rest_timer_action_cancel
            ),
            getPendingIntent(RestTimerService.ACTION_CANCEL_TIMER),
        )
        if (!timerIsFinished) {
            addAction(
                -1,
                context.getString(
                    if (timerIsPaused) R.string.rest_timer_action_resume
                    else R.string.rest_timer_action_pause
                ),
                getPendingIntent(RestTimerService.ACTION_TOGGLE_TIMER),
            )
        }
        addAction(
            -1,
            context.getString(
                R.string.rest_timer_action_add_seconds,
                Constants.UPDATE_TIMER_BY_SECONDS,
            ),
            getPendingIntent(RestTimerService.ACTION_INCREASE_TIMER_SECONDS) {
                putExtra(RestTimerService.EXTRA_SECONDS, Constants.UPDATE_TIMER_BY_SECONDS)
            },
        )
        setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                Intent(Intent.ACTION_VIEW, DeepLink.WorkoutRoute.createLink(ID_NOT_SET, workoutID)),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        )
        return this
    }

    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    fun showTimerNotification(timerState: RestTimerState) {
        notificationManager.notify(NOTIFICATION_ID, getTimerNotification(timerState))
    }

    fun cancelTimerNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private inline fun getPendingIntent(
        action: String,
        intent: Intent.() -> Unit = {},
    ): PendingIntent {
        val intent = Intent(context, RestTimerService::class.java).setAction(action).apply(intent)
        return PendingIntent.getService(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val NOTIFICATION_ID = 2025
        private const val TIMER_RUNNING_CHANNEL_ID = "rest_timer_running"
        private const val TIMER_FINISHED_CHANNEL_ID = "rest_timer_finished"
    }
}
