package com.patrykandpatrick.liftapp.feature.backup.restore.model

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import kotlinx.collections.immutable.ImmutableSet

@Immutable
sealed interface BackupRestoreState {

    @Immutable data object Reading : BackupRestoreState

    @Immutable
    data class Configuring(
        val available: ImmutableSet<BackupDataType>,
        val selected: ImmutableSet<BackupDataType>,
        val required: ImmutableSet<BackupDataType>,
    ) : BackupRestoreState {
        val canRestore: Boolean
            get() = selected.isNotEmpty()
    }

    @Immutable data object Restoring : BackupRestoreState
}

sealed interface Action {
    data object PopBackStack : Action

    data class Toggle(val type: BackupDataType) : Action

    data object Restore : Action
}
