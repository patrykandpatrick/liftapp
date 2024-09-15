package com.patrykandpatryk.liftapp.feature.newroutine.ui

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope

@HiltViewModel(assistedFactory = NewRoutineViewModel.Factory::class)
class NewRoutineViewModel @AssistedInject constructor(
    @Assisted routineID: Long,
    stateFactory: NewRoutineState.Factory,
    viewModelScope: CoroutineScope,
) : ViewModel(viewModelScope) {
    val state: NewRoutineState = stateFactory.create(routineID, viewModelScope)

    @AssistedFactory
    interface Factory {
        fun create(routineID: Long): NewRoutineViewModel
    }
}
