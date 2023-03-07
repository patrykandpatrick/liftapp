package com.patrykandpatryk.liftapp.feature.bodymeasurementlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.FormatBodyMeasurementValueToStringUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementsWithLatestEntriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class BodyMeasurementListViewModel @Inject constructor(
    getBodyMeasurementsWithLatestEntries: GetBodyMeasurementsWithLatestEntriesUseCase,
    exceptionHandler: CoroutineExceptionHandler,
    private val formatBodyMeasurementValueToString: FormatBodyMeasurementValueToStringUseCase,
) : ViewModel() {

    val items = getBodyMeasurementsWithLatestEntries()
        .map { bodyMeasurementsWithLatestEntries ->
            bodyMeasurementsWithLatestEntries.map { bodyMeasurementWithLatestEntry ->
                bodyMeasurementWithLatestEntry.toBodyMeasurementListItem()
            }
        }
        .stateIn(
            scope = viewModelScope + exceptionHandler,
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    private suspend fun BodyMeasurementWithLatestEntry.toBodyMeasurementListItem() =
        BodyMeasurementListItem(
            headline = name,
            supportingText = latestEntry?.let { formatBodyMeasurementValueToString(it.value) },
            bodyMeasurementID = id,
            bodyMeasurementType = type,
        )
}
