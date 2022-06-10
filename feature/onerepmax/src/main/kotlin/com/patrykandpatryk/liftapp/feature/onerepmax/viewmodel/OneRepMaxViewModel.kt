package com.patrykandpatryk.liftapp.feature.onerepmax.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.delegate.SavedStateHandleDelegate
import com.patrykandpatryk.liftapp.core.viewmodel.SavedStateHandleViewModel
import com.patrykandpatryk.liftapp.core.extension.smartToFloatOrNull
import com.patrykandpatryk.liftapp.core.extension.smartToIntOrNull
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import com.patrykandpatryk.liftapp.feature.onerepmax.model.HistoryEntryModel
import com.patrykandpatryk.liftapp.feature.onerepmax.model.OneRepMaxUiState
import com.patrykmichalik.opto.core.onEach
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OneRepMaxViewModel @Inject constructor(
    preferenceRepository: PreferenceRepository,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SavedStateHandleViewModel {

    val oneRepMaxUiStateStateFlow = savedStateHandle.getStateFlow(
        key = ONE_REP_MAX_UI_STATE_KEY,
        initialValue = OneRepMaxUiState(),
    )

    var oneRepMaxUiState by SavedStateHandleDelegate(
        key = ONE_REP_MAX_UI_STATE_KEY,
        defaultValue = OneRepMaxUiState(),
    )

    var historyUpdateJob: Job? = null

    init {
        preferenceRepository.massUnit.onEach(launchIn = viewModelScope) {
            oneRepMaxUiState = oneRepMaxUiState.copy(massUnit = it)
        }
    }

    fun updateRepsInput(repsInput: String) {

        val reps = if (repsInput.isEmpty()) 0 else repsInput.smartToIntOrNull()

        val oneRepMax =
            if (reps != null) calculateOneRepMax(reps = reps, mass = oneRepMaxUiState.mass)
            else oneRepMaxUiState.oneRepMax

        oneRepMaxUiState = oneRepMaxUiState.copy(
            repsInput = repsInput,
            reps = reps ?: 0,
            repsInputValid = reps != null,
            oneRepMax = oneRepMax,
        )

        if (reps != null && oneRepMaxUiState.massInputValid) {
            updateHistory(reps = reps, mass = oneRepMaxUiState.mass, oneRepMax = oneRepMax)
        }
    }

    fun updateMassInput(massInput: String) {

        val mass = if (massInput.isEmpty()) 0f else massInput.smartToFloatOrNull()

        val oneRepMax =
            if (mass != null) calculateOneRepMax(reps = oneRepMaxUiState.reps, mass = mass)
            else oneRepMaxUiState.oneRepMax

        oneRepMaxUiState = oneRepMaxUiState.copy(
            massInput = massInput,
            mass = mass ?: 0f,
            massInputValid = mass != null,
            oneRepMax = oneRepMax,
        )

        if (mass != null && oneRepMaxUiState.repsInputValid) {
            updateHistory(reps = oneRepMaxUiState.reps, mass = mass, oneRepMax = oneRepMax)
        }
    }

    private fun updateHistory(
        reps: Int,
        mass: Float,
        oneRepMax: Float,
    ) {
        historyUpdateJob?.cancel()
        if (oneRepMax == 0f) return

        historyUpdateJob = viewModelScope.launch {
            delay(HISTORY_UPDATE_DELAY)
            HistoryEntryModel(
                reps = reps,
                mass = mass,
                oneRepMax = oneRepMax,
            ).let {
                if (oneRepMaxUiState.history.firstOrNull() == it) return@launch
                oneRepMaxUiState = oneRepMaxUiState
                    .copy(history = listOf(it) + oneRepMaxUiState.history)
            }
        }
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
        private const val ONE_REP_MAX_UI_STATE_KEY = "one_rep_max_ui_state"
        private const val HISTORY_UPDATE_DELAY = 3000L
    }
}
