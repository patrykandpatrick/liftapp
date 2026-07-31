package com.patrykandpatrick.liftapp.feature.backup.export.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import kotlinx.collections.immutable.ImmutableSet

@Immutable
sealed interface BackupExportState {

    /**
     * [required] holds the types the selection drags in — checking Workouts also checks Routines,
     * because a workout without the routine it came from cannot be read back.
     */
    @Immutable
    data class Configuring(
        val selected: ImmutableSet<BackupDataType>,
        val required: ImmutableSet<BackupDataType>,
        val destination: BackupLocation?,
        val destinationName: String?,
    ) : BackupExportState {
        val canExport: Boolean
            get() = selected.isNotEmpty() && destination != null && destinationName != null
    }

    @Immutable data object Exporting : BackupExportState
}

sealed interface Action {
    data object PopBackStack : Action

    data class Toggle(val type: BackupDataType) : Action

    data class SetDestination(val location: BackupLocation) : Action

    data object Export : Action
}
