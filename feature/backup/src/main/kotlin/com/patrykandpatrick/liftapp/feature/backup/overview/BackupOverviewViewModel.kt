package com.patrykandpatrick.liftapp.feature.backup.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.domain.backup.BackupPreferenceRepository
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.feature.backup.overview.model.Action
import com.patrykandpatrick.liftapp.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class BackupOverviewViewModel
@Inject
constructor(
    preferences: BackupPreferenceRepository,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    /** Only there to summarize the automatic backup row; `null` until it has been read. */
    val autoBackup =
        preferences.autoBackup
            .get()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = null,
            )

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> viewModelScope.launch { navigationCommander.popBackStack() }
            Action.BackUp -> navigateTo(Routes.Backup.Export)
            Action.Automatic -> navigateTo(Routes.Backup.Automatic)
            is Action.Restore -> navigateTo(Routes.Backup.restore(action.location.value))
        }
    }

    private fun navigateTo(route: Any) {
        viewModelScope.launch { navigationCommander.navigateTo(route) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
