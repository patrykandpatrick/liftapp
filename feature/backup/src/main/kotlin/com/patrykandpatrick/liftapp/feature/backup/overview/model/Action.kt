package com.patrykandpatrick.liftapp.feature.backup.overview.model

import com.patrykandpatrick.liftapp.domain.backup.BackupLocation

sealed interface Action {
    data object PopBackStack : Action

    data object BackUp : Action

    data object Automatic : Action

    /** The file the user picked to restore from. */
    data class Restore(val location: BackupLocation) : Action
}
