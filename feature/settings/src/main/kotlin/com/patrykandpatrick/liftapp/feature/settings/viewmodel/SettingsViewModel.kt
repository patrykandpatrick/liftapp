package com.patrykandpatrick.liftapp.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.domain.date.HourFormat
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.feature.settings.model.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val preferenceRepository: PreferenceRepository,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    val allPreferences = preferenceRepository.allPreferences

    fun onAction(action: Action) {
        when (action) {
            is Action.SetMassUnit -> setMassUnit(action.massUnit)
            is Action.SetDistanceUnit -> setDistanceUnit(action.distanceUnit)
            is Action.SetHourFormat -> setHourFormat(action.hourFormat)
            is Action.PopBackStack -> popBackStack()
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

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }
}
