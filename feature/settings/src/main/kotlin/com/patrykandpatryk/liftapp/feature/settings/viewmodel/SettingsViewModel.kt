package com.patrykandpatryk.liftapp.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.date.HourFormat
import com.patrykandpatryk.liftapp.domain.preference.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel
@Inject
constructor(private val preferenceRepository: PreferenceRepository) : ViewModel() {

    val allPreferences = preferenceRepository.allPreferences

    fun setMassUnit(value: MassUnit) {
        viewModelScope.launch { preferenceRepository.massUnit.set(value = value) }
    }

    fun setDistanceUnit(value: LongDistanceUnit) {
        viewModelScope.launch { preferenceRepository.longDistanceUnit.set(value = value) }
    }

    fun setHourFormat(value: HourFormat) {
        viewModelScope.launch { preferenceRepository.hourFormat.set(value = value) }
    }
}
