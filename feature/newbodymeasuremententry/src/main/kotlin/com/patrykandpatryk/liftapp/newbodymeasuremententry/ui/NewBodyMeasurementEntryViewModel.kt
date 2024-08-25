package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = NewBodyMeasurementEntryViewModel.Factory::class)
class NewBodyMeasurementEntryViewModel @AssistedInject constructor(
    @Assisted id: Long,
    @Assisted entryId: Long?,
    stateFactory: NewBodyMeasurementState.Factory,
) : ViewModel() {

    val state: NewBodyMeasurementState = stateFactory.create(
        id = id,
        entryId = entryId,
        coroutineScope = viewModelScope,
    )

    @AssistedFactory
    interface Factory {
        fun create(id: Long, entryId: Long?): NewBodyMeasurementEntryViewModel
    }
}
