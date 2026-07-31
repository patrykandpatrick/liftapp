package com.patrykandpatrick.liftapp.core.extension

import androidx.annotation.StringRes
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType

inline val BackupDataType.stringResourceId: Int
    @StringRes
    get() =
        when (this) {
            BackupDataType.Routines -> R.string.backup_data_type_routines
            BackupDataType.Workouts -> R.string.backup_data_type_workouts
            BackupDataType.TrainingPlans -> R.string.backup_data_type_training_plans
            BackupDataType.BodyMeasurements -> R.string.backup_data_type_body_measurements
            BackupDataType.Settings -> R.string.backup_data_type_settings
        }
