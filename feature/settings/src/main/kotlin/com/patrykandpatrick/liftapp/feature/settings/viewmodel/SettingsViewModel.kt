package com.patrykandpatrick.liftapp.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.domain.backup.BackupPreferenceRepository
import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.theme.Theme
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.feature.settings.model.Action
import com.patrykandpatrick.liftapp.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val preferenceRepository: PreferenceRepository,
    backupPreferences: BackupPreferenceRepository,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    val allPreferences = preferenceRepository.allPreferences
    val autoBackup =
        backupPreferences.autoBackup
            .get()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = null,
            )

    fun onAction(action: Action) {
        when (action) {
            is Action.SetMassUnit -> setMassUnit(action.massUnit)
            is Action.SetDistanceUnit -> setDistanceUnit(action.distanceUnit)
            is Action.SetHourFormat -> setHourFormat(action.hourFormat)
            is Action.SetFirstDayOfWeek -> setFirstDayOfWeek(action.firstDayOfWeek)
            is Action.SetTheme -> setTheme(action.theme)
            Action.AutomaticBackup -> navigateToAutomaticBackup()
            Action.OpenSourceLicenses -> navigateToOpenSourceLicenses()
            Action.PopBackStack -> popBackStack()
        }
    }

    private fun setMassUnit(value: MassUnit) {
        viewModelScope.launch { preferenceRepository.massUnit.set(value = value) }
    }

    private fun setDistanceUnit(value: LongDistanceUnit) {
        viewModelScope.launch { preferenceRepository.longDistanceUnit.set(value = value) }
    }

    private fun setHourFormat(value: HourFormat) {
        viewModelScope.launch { preferenceRepository.hourFormat.set(value = value) }
    }

    private fun setFirstDayOfWeek(value: DayOfWeek) {
        viewModelScope.launch { preferenceRepository.firstDayOfWeek.set(value = value) }
    }

    private fun setTheme(value: Theme) {
        viewModelScope.launch { preferenceRepository.theme.set(value = value) }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun navigateToAutomaticBackup() {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.Backup.Automatic) }
    }

    private fun navigateToOpenSourceLicenses() {
        viewModelScope.launch { navigationCommander.navigateTo(Routes.OpenSourceLicenses) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
