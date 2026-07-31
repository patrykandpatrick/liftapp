package com.patrykandpatrick.liftapp.feature.backup.auto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupScheduler
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupSettings
import com.patrykandpatrick.liftapp.domain.backup.BackupInterval
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.domain.backup.BackupPreferenceRepository
import com.patrykandpatrick.liftapp.domain.backup.BackupRetention
import com.patrykandpatrick.liftapp.domain.backup.GetDirectoryNameUseCase
import com.patrykandpatrick.liftapp.domain.backup.PersistDirectoryAccessUseCase
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** The screen shows [AutoBackupSettings] plus the display name of the folder it points at. */
data class AutoBackupState(val settings: AutoBackupSettings, val destinationName: String?)

sealed interface Action {
    data object PopBackStack : Action

    data class SetEnabled(val enabled: Boolean) : Action

    data class SetDestination(val location: BackupLocation) : Action

    data class SetInterval(val interval: BackupInterval) : Action

    data class SetRetention(val retention: BackupRetention) : Action
}

@HiltViewModel
class AutoBackupViewModel
@Inject
constructor(
    private val preferences: BackupPreferenceRepository,
    private val scheduler: AutoBackupScheduler,
    private val getDirectoryName: GetDirectoryNameUseCase,
    private val persistDirectoryAccess: PersistDirectoryAccessUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    val state =
        preferences.autoBackup
            .get()
            .map { settings ->
                AutoBackupState(
                    settings = settings,
                    destinationName =
                        settings.destination?.let { getDirectoryName.getDirectoryName(it) },
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = null,
            )

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> viewModelScope.launch { navigationCommander.popBackStack() }
            is Action.SetEnabled -> update { it.copy(enabled = action.enabled) }
            is Action.SetInterval -> update { it.copy(interval = action.interval) }
            is Action.SetRetention -> update { it.copy(retention = action.retention) }
            is Action.SetDestination -> {
                persistDirectoryAccess.persistDirectoryAccess(action.location)
                update { it.copy(destination = action.location) }
            }
        }
    }

    /**
     * Every change goes through here, so the schedule and the settings can never disagree: the work
     * is re-enqueued from whatever was just written.
     */
    private fun update(change: (AutoBackupSettings) -> AutoBackupSettings) {
        viewModelScope.launch {
            var updated: AutoBackupSettings? = null
            preferences.autoBackup.update { current -> change(current).also { updated = it } }
            updated?.let(scheduler::reschedule)
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
