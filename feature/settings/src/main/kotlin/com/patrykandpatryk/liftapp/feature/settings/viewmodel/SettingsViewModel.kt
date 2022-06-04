package com.patrykandpatryk.liftapp.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
) : ViewModel() {

    val massUnit = preferenceRepository.massUnit.get()
    fun setMassUnit(value: MassUnit) {
        viewModelScope.launch {
            preferenceRepository.massUnit.set(value = value)
        }
    }

    val distanceUnit = preferenceRepository.distanceUnit.get()
    fun setDistanceUnit(value: DistanceUnit) {
        viewModelScope.launch {
            preferenceRepository.distanceUnit.set(value = value)
        }
    }
}
