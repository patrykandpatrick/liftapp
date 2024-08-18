package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.unit.GetUnitForBodyMeasurementTypeUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first

@HiltViewModel(assistedFactory = NewBodyMeasurementEntryViewModel.Factory::class)
class NewBodyMeasurementEntryViewModel @AssistedInject constructor(
    @Assisted id: Long,
    @Assisted entryId: Long?,
    formatter: Formatter,
    repository: BodyMeasurementRepository,
    textFieldStateManager: TextFieldStateManager,
    getUnitForBodyMeasurementType: GetUnitForBodyMeasurementTypeUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val state: NewBodyMeasurementState = NewBodyMeasurementState(
        getFormattedDate = { formatter.getFormattedDate(it) },
        getBodyMeasurementWithLatestEntry = { repository.getBodyMeasurementWithLatestEntry(id).first() },
        getBodyMeasurementEntry = { entryId?.let { repository.getBodyMeasurementEntry(it) } },
        upsertBodyMeasurementEntry = { value, time ->
            if (entryId != null) {
                repository.updateBodyMeasurementEntry(entryId, id, value, time)
            } else {
                repository.insertBodyMeasurementEntry(id, value, time)
            }
        },
        textFieldStateManager = textFieldStateManager,
        getUnitForBodyMeasurementType = { getUnitForBodyMeasurementType(it) },
        coroutineScope = viewModelScope,
        savedStateHandle = savedStateHandle,
    )

    @AssistedFactory
    interface Factory {
        fun create(id: Long, entryId: Long?): NewBodyMeasurementEntryViewModel
    }
}
