package com.patrykandpatryk.liftapp.feature.bodydetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.vico.core.entry.ChartEntryModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class BodyDetailsViewModel @Inject constructor(
    screenStateHandler: ScreenStateHandler<ScreenState, Intent, Unit>,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Unit> by screenStateHandler {

    val chartModelProducer = ChartEntryModelProducer()

    override val state: StateFlow<ScreenState> = screenStateHandler
        .state
        .onEach { chartModelProducer.setEntries(it.chartEntries) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = screenStateHandler.state.value,
        )
}
