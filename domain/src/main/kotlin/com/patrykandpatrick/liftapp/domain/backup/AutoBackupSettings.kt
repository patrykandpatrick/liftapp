package com.patrykandpatrick.liftapp.domain.backup

import kotlinx.serialization.Serializable

/** How often an automatic backup is made. The published app offered exactly these intervals. */
@Serializable
enum class BackupInterval(val days: Int) {
    Daily(1),
    EveryTwoDays(2),
    EveryThreeDays(3),
    Weekly(7),
}

/** How long an automatic backup is kept before the app deletes it. */
@Serializable
enum class BackupRetention(val days: Int) {
    OneWeek(7),
    TwoWeeks(14),
    OneMonth(30),
    TwoMonths(60),
    ThreeMonths(90),
}

@Serializable
data class AutoBackupSettings(
    val enabled: Boolean = false,
    val destination: BackupLocation? = null,
    val interval: BackupInterval = BackupInterval.Daily,
    val retention: BackupRetention = BackupRetention.TwoWeeks,
) {
    /** Automatic backups only run once there is somewhere to put them. */
    val isRunnable: Boolean
        get() = enabled && destination != null
}
