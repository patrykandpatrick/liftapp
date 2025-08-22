package com.patrykandpatryk.liftapp.feature.onerepmax

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.extension.smartToDoubleOrNull
import com.patrykandpatryk.liftapp.core.extension.update
import com.patrykandpatryk.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.unit.GetPreferredMassUnitUseCase
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
class OneRepMaxState(
    getMassUnit: () -> Flow<MassUnit>,
    private val savedStateHandle: SavedStateHandle,
    private val formatWeight: (Double, MassUnit) -> String,
    private val coroutineScope: CoroutineScope,
) {
    private val _mass: MutableState<String> = mutableStateOf("")

    private val _reps: MutableState<String> = mutableStateOf("")

    private val massValue = MutableStateFlow(0.0)

    private val repsValue = MutableStateFlow(0)

    private val oneRepMaxValue =
        combine(massValue, repsValue, OneRepMaxCalculator::getOneRepMax)
            .stateIn(coroutineScope, SharingStarted.Eagerly, 0.0)

    private var historyUpdateJob: Job? = null

    val mass: State<String> = _mass

    val reps: State<String> = _reps

    val massUnit: StateFlow<MassUnit> =
        getMassUnit().stateIn(coroutineScope, SharingStarted.Lazily, MassUnit.Kilograms)

    val oneRepMax: StateFlow<String> =
        combine(oneRepMaxValue, massUnit, formatWeight)
            .stateIn(coroutineScope, SharingStarted.Eagerly, "")

    val history: StateFlow<ImmutableList<HistoryEntryModel>> =
        savedStateHandle
            .getStateFlow<List<HistoryEntryModel>>(HISTORY_KEY, emptyList())
            .map { it.toImmutableList() }
            .stateIn(coroutineScope, SharingStarted.Eagerly, persistentListOf())

    @AssistedInject
    constructor(
        @Assisted coroutineScope: CoroutineScope,
        @Assisted savedStateHandle: SavedStateHandle,
        getPreferredMassUnitUseCase: GetPreferredMassUnitUseCase,
        formatter: Formatter,
    ) : this(
        getMassUnit = getPreferredMassUnitUseCase::invoke,
        savedStateHandle = savedStateHandle,
        formatWeight = formatter::formatWeight,
        coroutineScope = coroutineScope,
    )

    fun updateReps(reps: String) {
        val trimmedReps = reps.take(3)
        repsValue.value =
            when {
                trimmedReps.isEmpty() -> 0
                else -> trimmedReps.toIntOrNull()
            } ?: return

        _reps.value = trimmedReps
        addToHistory()
    }

    fun updateMass(mass: String) {
        massValue.value =
            when {
                mass.isEmpty() -> 0.0
                else -> mass.smartToDoubleOrNull() ?: return
            }

        _mass.value = mass
        addToHistory()
    }

    fun clearHistory() {
        savedStateHandle.update<List<HistoryEntryModel>>(HISTORY_KEY) { emptyList() }
    }

    private fun addToHistory() {
        historyUpdateJob?.cancel()
        if (oneRepMaxValue.value == 0.0) return
        val historyEntry =
            HistoryEntryModel(
                reps = repsValue.value,
                mass = formatWeight(massValue.value, massUnit.value),
                oneRepMax = oneRepMax.value,
            )

        historyUpdateJob =
            coroutineScope.launch {
                delay(HISTORY_UPDATE_DELAY)
                if (history.value.firstOrNull() == historyEntry) return@launch
                savedStateHandle.update<List<HistoryEntryModel>>(HISTORY_KEY) { history ->
                    buildList {
                        history?.also(::addAll)
                        add(historyEntry)
                    }
                }
            }
    }

    @AssistedFactory
    interface Factory {
        fun create(
            coroutineScope: CoroutineScope,
            savedStateHandle: SavedStateHandle,
        ): OneRepMaxState
    }

    companion object {
        internal const val HISTORY_KEY = "history"
        private const val HISTORY_UPDATE_DELAY = 1000L
    }
}
