package com.patrykandpatryk.liftapp.feature.bodymeasurementlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementsWithLatestEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus

@HiltViewModel
class BodyMeasurementListViewModel @Inject constructor(
    getBodyMeasurementsWithLatestEntries: GetBodyMeasurementsWithLatestEntriesUseCase,
    exceptionHandler: CoroutineExceptionHandler,
) : ViewModel() {

    val bodyMeasurementsWithLatestEntries = getBodyMeasurementsWithLatestEntries()
        .stateIn(
            scope = viewModelScope + exceptionHandler,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )
}
