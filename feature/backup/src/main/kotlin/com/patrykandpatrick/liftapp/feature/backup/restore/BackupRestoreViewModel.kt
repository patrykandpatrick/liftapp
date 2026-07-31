package com.patrykandpatrick.liftapp.feature.backup.restore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.patrykandpatrick.liftapp.core.logging.LogPublisher
import com.patrykandpatrick.liftapp.core.logging.UiLogger
import com.patrykandpatrick.liftapp.core.message.ConfirmationPublisher
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.backup.BackupLocation
import com.patrykandpatrick.liftapp.domain.backup.ImportBackupUseCase
import com.patrykandpatrick.liftapp.domain.backup.ReadBackupContentsUseCase
import com.patrykandpatrick.liftapp.domain.backup.requiredWithin
import com.patrykandpatrick.liftapp.domain.backup.withDependencies
import com.patrykandpatrick.liftapp.domain.exception.DisplayableException
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.feature.backup.restore.model.Action
import com.patrykandpatrick.liftapp.feature.backup.restore.model.BackupRestoreState
import com.patrykandpatrick.liftapp.navigation.data.BackupRestoreRouteData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class BackupRestoreViewModel
@Inject
constructor(
    savedStateHandle: SavedStateHandle,
    private val readBackupContents: ReadBackupContentsUseCase,
    private val importBackup: ImportBackupUseCase,
    private val navigationCommander: NavigationCommander,
    private val stringProvider: StringProvider,
    private val confirmationPublisher: ConfirmationPublisher,
    logger: UiLogger,
) : ViewModel(), LogPublisher by logger {

    private val location =
        BackupLocation(savedStateHandle.toRoute<BackupRestoreRouteData>().location)

    private val _state = MutableStateFlow<BackupRestoreState>(BackupRestoreState.Reading)

    val state = _state.asStateFlow()

    init {
        read()
    }

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> viewModelScope.launch { navigationCommander.popBackStack() }
            is Action.Toggle -> toggle(action.type)
            Action.Restore -> restore()
        }
    }

    private fun read() {
        viewModelScope.launch {
            _state.value =
                try {
                    val available = readBackupContents.readBackupContents(location)
                    select(available = available, selection = available)
                } catch (exception: DisplayableException) {
                    reportUnreadable(exception.message ?: stringProvider.errorBackupFileUnreadable)
                    BackupRestoreState.Reading
                } catch (throwable: Throwable) {
                    Timber.w(throwable, "Could not read ${location.value}.")
                    reportUnreadable(stringProvider.errorBackupFileUnreadable)
                    BackupRestoreState.Reading
                }
        }
    }

    private suspend fun reportUnreadable(message: String) {
        navigationCommander.popBackStack()
        confirmationPublisher.publish(message)
    }

    private fun toggle(type: BackupDataType) {
        _state.update { current ->
            if (current !is BackupRestoreState.Configuring) return@update current
            val selection =
                if (type in current.selected) current.selected - type else current.selected + type
            select(current.available, selection)
        }
    }

    private fun restore() {
        val configuring = state.value as? BackupRestoreState.Configuring ?: return

        viewModelScope.launch {
            _state.value = BackupRestoreState.Restoring
            try {
                importBackup.importBackup(location, configuring.selected)
                // The screen has done its job, so it leaves and the confirmation follows the user.
                navigationCommander.popBackStack()
                confirmationPublisher.publish(stringProvider.backupImportSucceeded)
            } catch (throwable: Throwable) {
                _state.value = configuring
                Timber.tag(Constants.Logging.DISPLAYABLE_ERROR)
                    .e(throwable, stringProvider.errorBackupImportFailed)
            }
        }
    }

    private companion object {
        fun select(
            available: Set<BackupDataType>,
            selection: Set<BackupDataType>,
        ): BackupRestoreState.Configuring {
            // Only what the file actually carries can be restored, dependencies included.
            val selected = selection.withDependencies().intersect(available)
            return BackupRestoreState.Configuring(
                available = available.toImmutableSet(),
                selected = selected.toImmutableSet(),
                required = selected.requiredWithin().intersect(available).toImmutableSet(),
            )
        }
    }
}
