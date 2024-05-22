package com.patrykandpatryk.liftapp.feature.onerepmax

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.extension.smartToFloatOrNull
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OneRepMaxViewModel @Inject constructor(
    oneRepMaxDataSource: OneRepMaxDataSource,
) : ViewModel(), OneRepMaxState {
    override val reps: MutableState<String> = mutableStateOf("")

    override val mass: MutableState<String> = mutableStateOf("")

    override val oneRepMax: MutableState<Float> = mutableFloatStateOf(0f)

    override val massUnit: MutableState<MassUnit?> = mutableStateOf(null)

    override val history: MutableState<ImmutableList<HistoryEntryModel>> = mutableStateOf(persistentListOf())

    private var repsValue = 0

    private var massValue = 0f

    private var historyUpdateJob: Job? = null

    init {
        oneRepMaxDataSource
            .massUnit
            .take(1)
            .onEach { massUnit.value = it }
            .launchIn(viewModelScope)
    }

    override fun updateReps(reps: String) {
        repsValue = when {
            reps.isEmpty() -> 0
            reps.isDigitsOnly() -> reps.toInt()
            else -> return
        }

        this.reps.value = reps
        updateOneRepMax()
        addToHistory()
    }

    override fun updateMass(mass: String) {
        massValue = when {
            mass.isEmpty() -> 0f
            else -> mass.smartToFloatOrNull() ?: return
        }

        this.mass.value = mass
        updateOneRepMax()
        addToHistory()
    }

    private fun addToHistory() {
        historyUpdateJob?.cancel()
        if (oneRepMax.value == 0f) return

        historyUpdateJob = viewModelScope.launch {
            delay(HISTORY_UPDATE_DELAY)
            val historyEntry = HistoryEntryModel(
                reps = repsValue,
                mass = massValue,
                oneRepMax = oneRepMax.value,
            )
            if (history.value.firstOrNull() == historyEntry) return@launch
            history.value = (history.value + historyEntry).toImmutableList()
        }
    }

    private fun updateOneRepMax() {
        oneRepMax.value = calculateOneRepMax(repsValue, massValue)
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
