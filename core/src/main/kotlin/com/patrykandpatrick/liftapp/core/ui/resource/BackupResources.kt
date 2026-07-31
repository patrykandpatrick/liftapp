package com.patrykandpatrick.liftapp.core.ui.resource

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.backup.BackupInterval
import com.patrykandpatrick.liftapp.domain.backup.BackupRetention
import com.patrykandpatrick.liftapp.ui.icons.BicepsFlexed
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Plan
import com.patrykandpatrick.liftapp.ui.icons.Routine
import com.patrykandpatrick.liftapp.ui.icons.Ruler
import com.patrykandpatrick.liftapp.ui.icons.Settings

/**
 * The form a data type takes in a backup's file name, where it modifies "backup" and so goes in the
 * singular: "Routine backup", "Body-measurement backup". The picker uses [stringResourceId]
 * instead, where the type stands on its own and stays plural.
 */
val BackupDataType.nameStringRes: Int
    @StringRes
    get() =
        when (this) {
            BackupDataType.Routines -> R.string.backup_name_type_routines
            BackupDataType.Workouts -> R.string.backup_name_type_workouts
            BackupDataType.TrainingPlans -> R.string.backup_name_type_training_plans
            BackupDataType.BodyMeasurements -> R.string.backup_name_type_body_measurements
            BackupDataType.Settings -> R.string.backup_name_type_settings
        }

/** The form that follows another entry in a list, as in "Routine, workout, and settings". */
val BackupDataType.nameListContinuationStringRes: Int
    @StringRes
    get() =
        when (this) {
            BackupDataType.Routines -> R.string.backup_name_type_routines_list_continuation
            BackupDataType.Workouts -> R.string.backup_name_type_workouts_list_continuation
            BackupDataType.TrainingPlans ->
                R.string.backup_name_type_training_plans_list_continuation
            BackupDataType.BodyMeasurements ->
                R.string.backup_name_type_body_measurements_list_continuation
            BackupDataType.Settings -> R.string.backup_name_type_settings_list_continuation
        }

val BackupDataType.imageVector: ImageVector
    get() =
        when (this) {
            BackupDataType.Routines -> LiftAppIcons.Routine
            BackupDataType.Workouts -> LiftAppIcons.BicepsFlexed
            BackupDataType.TrainingPlans -> LiftAppIcons.Plan
            BackupDataType.BodyMeasurements -> LiftAppIcons.Ruler
            BackupDataType.Settings -> LiftAppIcons.Settings
        }

/** "1 day", "7 days". */
@Composable fun BackupInterval.prettyString(): String = dayCount(days)

/** "7 days", "90 days". */
@Composable fun BackupRetention.prettyString(): String = dayCount(days)

@Composable
private fun dayCount(days: Int): String =
    LocalContext.current.resources.getQuantityString(R.plurals.backup_duration_days, days, days)
