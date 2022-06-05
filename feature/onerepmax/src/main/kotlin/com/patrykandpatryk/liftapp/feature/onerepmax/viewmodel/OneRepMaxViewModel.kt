package com.patrykandpatryk.liftapp.feature.onerepmax.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.extension.smartToFloatOrNull
import com.patrykandpatryk.liftapp.core.extension.smartToIntOrNull
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import com.patrykandpatryk.liftapp.feature.onerepmax.model.OneRepMaxUiState
import com.patrykmichalik.opto.core.onEach
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OneRepMaxViewModel @Inject constructor(
    preferenceRepository: PreferenceRepository,
) : ViewModel() {

    var oneRepMaxUiState by mutableStateOf(
        value = OneRepMaxUiState(
            repsInput = "",
            repsInputValid = true,
            reps = 0,
            massInput = "",
            massInputValid = true,
            mass = 0f,
            oneRepMax = 0f,
            massUnit = null,
        ),
    )

    init {
        preferenceRepository.massUnit.onEach(launchIn = viewModelScope) {
            oneRepMaxUiState = oneRepMaxUiState.copy(massUnit = it)
        }
    }

    fun updateRepsInput(repsInput: String) {

        val reps = if (repsInput.isEmpty()) 0 else repsInput.smartToIntOrNull()

        oneRepMaxUiState = oneRepMaxUiState.copy(
            repsInput = repsInput,
            reps = reps ?: 0,
            repsInputValid = reps != null,
            oneRepMax = if (reps != null) {
                calculateOneRepMax(reps = reps, mass = oneRepMaxUiState.mass)
            } else oneRepMaxUiState.oneRepMax,
        )
    }

    fun updateMassInput(massInput: String) {

        val mass = if (massInput.isEmpty()) 0f else massInput.smartToFloatOrNull()

        oneRepMaxUiState = oneRepMaxUiState.copy(
            massInput = massInput,
            mass = mass ?: 0f,
            massInputValid = mass != null,
            oneRepMax = if (mass != null) {
                calculateOneRepMax(reps = oneRepMaxUiState.reps, mass = mass)
            } else oneRepMaxUiState.oneRepMax,
        )
    }

    private fun calculateOneRepMax(
        reps: Int,
        mass: Float,
    ) = when {
        reps == 0 || mass == 0f -> 0f
        reps == 1 -> mass
        else -> mass * (1f + reps.toFloat() / EPLEY_REP_COUNT_DIVISOR)
    }

    private companion object {

        private const val EPLEY_REP_COUNT_DIVISOR = 30f
    }
}
