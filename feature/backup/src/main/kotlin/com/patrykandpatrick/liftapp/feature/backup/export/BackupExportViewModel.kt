package com.patrykandpatrick.liftapp.feature.backup.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.logging.LogPublisher
import com.patrykandpatrick.liftapp.core.logging.UiLogger
import com.patrykandpatrick.liftapp.core.message.ConfirmationPublisher
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.domain.backup.BackupPreferenceRepository
import com.patrykandpatrick.liftapp.domain.backup.ExportBackupUseCase
import com.patrykandpatrick.liftapp.domain.backup.GetDirectoryNameUseCase
import com.patrykandpatrick.liftapp.domain.backup.PersistDirectoryAccessUseCase
import com.patrykandpatrick.liftapp.domain.backup.requiredWithin
import com.patrykandpatrick.liftapp.domain.backup.withDependencies
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.feature.backup.export.model.Action
import com.patrykandpatrick.liftapp.feature.backup.export.model.BackupExportState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class BackupExportViewModel
@Inject
constructor(
    private val preferences: BackupPreferenceRepository,
    private val exportBackup: ExportBackupUseCase,
    private val getDirectoryName: GetDirectoryNameUseCase,
    private val persistDirectoryAccess: PersistDirectoryAccessUseCase,
    private val navigationCommander: NavigationCommander,
    private val stringProvider: StringProvider,
    private val confirmationPublisher: ConfirmationPublisher,
    logger: UiLogger,
) : ViewModel(), LogPublisher by logger {

    private val _state =
        MutableStateFlow<BackupExportState>(
            select(BackupDataType.entries.toSet(), destination = null, destinationName = null)
        )

    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // The folder the last backup went to is usually the folder the next one should go to.
            val remembered = preferences.lastExportDestination.get().first() ?: return@launch
            setDestination(remembered, persist = false)
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> viewModelScope.launch { navigationCommander.popBackStack() }
            is Action.Toggle -> toggle(action.type)
            is Action.SetDestination -> setDestination(action.location, persist = true)
            Action.Export -> export()
        }
    }

    private fun toggle(type: BackupDataType) {
        _state.update { current ->
            if (current !is BackupExportState.Configuring) return@update current
            val selection =
                if (type in current.selected) current.selected - type else current.selected + type
            select(selection, current.destination, current.destinationName)
        }
    }

    private fun setDestination(location: BackupLocation, persist: Boolean) {
        viewModelScope.launch {
            if (persist) {
                persistDirectoryAccess.persistDirectoryAccess(location)
            }
            val name = getDirectoryName.getDirectoryName(location)
            if (persist && name != null) {
                preferences.lastExportDestination.set(location)
            } else if (!persist && name == null) {
                preferences.lastExportDestination.set(null)
            }
            _state.update { current ->
                if (current is BackupExportState.Configuring) {
                    current.copy(
                        destination = location.takeIf { name != null },
                        destinationName = name,
                    )
                } else {
                    current
                }
            }
        }
    }

    private fun export() {
        val configuring = state.value as? BackupExportState.Configuring ?: return
        val destination = configuring.destination ?: return

        viewModelScope.launch {
            _state.value = BackupExportState.Exporting
            try {
                exportBackup.exportBackup(
                    directory = destination,
                    types = configuring.selected,
                    automatic = false,
                )
                // The screen has done its job, so it leaves and the confirmation follows the user.
                navigationCommander.popBackStack()
                confirmationPublisher.publish(stringProvider.backupExportSucceeded)
            } catch (throwable: Throwable) {
                _state.value = configuring
                Timber.tag(Constants.Logging.DISPLAYABLE_ERROR)
                    .e(throwable, stringProvider.errorBackupExportFailed)
            }
        }
    }

    private companion object {
        fun select(
            selection: Set<BackupDataType>,
            destination: BackupLocation?,
            destinationName: String?,
        ): BackupExportState.Configuring {
            val selected = selection.withDependencies()
            return BackupExportState.Configuring(
                selected = selected.toImmutableSet(),
                required = selected.requiredWithin().toImmutableSet(),
                destination = destination,
                destinationName = destinationName,
            )
        }
    }
}
