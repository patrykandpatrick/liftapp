package com.patrykandpatryk.liftapp.feature.bodymeasurementlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.FormatBodyMeasurementValueToStringUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementsWithLatestEntriesUseCase
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.feature.bodymeasurementlist.model.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

@HiltViewModel
class BodyMeasurementListViewModel
@Inject
constructor(
    getBodyMeasurementsWithLatestEntries: GetBodyMeasurementsWithLatestEntriesUseCase,
    exceptionHandler: CoroutineExceptionHandler,
    private val formatBodyMeasurementValueToString: FormatBodyMeasurementValueToStringUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    val items =
        getBodyMeasurementsWithLatestEntries()
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

    fun onAction(action: Action) {
        when (action) {
            is Action.OpenDetails -> openDetails(action.bodyMeasurementID)
        }
    }

    private fun openDetails(bodyMeasurementID: Long) {
        viewModelScope.launch {
            navigationCommander.navigateTo(Routes.BodyMeasurement.details(bodyMeasurementID))
        }
    }
}
