package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.BodyMeasurementDetailsRouteData
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatryk.liftapp.core.chart.ExtraStoreKey
import com.patrykandpatryk.liftapp.core.mapper.BodyMeasurementEntryToChartEntryMapper
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.bodymeasurement.DeleteBodyMeasurementEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.FormatBodyMeasurementValueToStringUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementEntriesUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.invoke
import com.patrykandpatryk.liftapp.domain.date.DateInterval
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.GetUnitForBodyMeasurementTypeUseCase
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model.Action
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class BodyMeasurementDetailViewModel
@Inject
constructor(
    private val routeData: BodyMeasurementDetailsRouteData,
    repository: BodyMeasurementRepository,
    getBodyMeasurementEntriesUseCase: GetBodyMeasurementEntriesUseCase,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val chartEntryMapper: BodyMeasurementEntryToChartEntryMapper,
    private val deleteBodyMeasurementEntryUseCase: DeleteBodyMeasurementEntryUseCase,
    private val formatBodyMeasurementValueToStringUseCase:
        FormatBodyMeasurementValueToStringUseCase,
    private val getUnitForBodyMeasurementTypeUseCase: GetUnitForBodyMeasurementTypeUseCase,
    private val stringProvider: StringProvider,
    private val navigationCommander: NavigationCommander,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val chartModelProducer = CartesianChartModelProducer()

    private val dateIntervalOptions = DateInterval.bodyMeasurementOptions

    private val dateInterval =
        savedStateHandle.getMutableStateFlow(DATE_INTERVAL_KEY, dateIntervalOptions.first())

    private val entries: Flow<List<BodyMeasurementEntry>> =
        dateInterval.flatMapLatest { dateInterval ->
            getBodyMeasurementEntriesUseCase(
                bodyMeasurementID = routeData.bodyMeasurementID,
                dateInterval = dateInterval,
            )
        }

    val state: StateFlow<Loadable<ScreenState>> =
        combine(
                repository.getBodyMeasurement(routeData.bodyMeasurementID),
                entries,
                dateInterval,
                transform = ::mapPopulatedState,
            )
            .toLoadableStateFlow(viewModelScope)

    private suspend fun mapPopulatedState(
        bodyMeasurement: BodyMeasurement,
        entries: List<BodyMeasurementEntry>,
        dateInterval: DateInterval,
    ): ScreenState =
        withContext(exceptionHandler) {
            val preferredValueUnit = getUnitForBodyMeasurementTypeUseCase(bodyMeasurement.type)
            val mappedEntries = chartEntryMapper(entries)
            chartModelProducer.runTransaction {
                extras {
                    it[ExtraStoreKey.MinX] =
                        dateInterval.periodStartTime.toLocalDate().toEpochDay().toDouble()
                    it[ExtraStoreKey.MaxX] =
                        dateInterval.periodEndTime.toLocalDate().toEpochDay().toDouble()
                    it[ExtraStoreKey.DateInterval] = dateInterval
                    it[ExtraStoreKey.ShowLeftRightLegend] = mappedEntries.size > 1
                }

                if (mappedEntries.isNotEmpty()) {
                    lineSeries { mappedEntries.forEach { (x, y) -> series(x, y) } }
                }
            }

            ScreenState(
                bodyMeasurementID = bodyMeasurement.id,
                name = bodyMeasurement.name,
                entries =
                    entries.mapIndexedNotNull { index, entry ->
                        if (
                            entry.localDateTime !in
                                dateInterval.periodStartTime..dateInterval.periodEndTime
                        ) {
                            return@mapIndexedNotNull null
                        }

                        ScreenState.Entry(
                            id = entry.id,
                            value =
                                formatBodyMeasurementValueToStringUseCase(
                                    entry.value,
                                    entries.getOrNull(index + 1)?.value,
                                ),
                            date = entry.localDateTime,
                        )
                    },
                modelProducer = chartModelProducer,
                valueUnit = stringProvider.getDisplayUnit(preferredValueUnit),
                dateInterval = dateInterval,
                dateIntervalOptions = dateIntervalOptions,
            )
        }

    fun onAction(action: Action) {
        when (action) {
            is Action.DeleteBodyMeasurementEntry -> deleteBodyMeasurementEntry(action.id)
            is Action.PopBackStack -> popBackStack()
            is Action.EditBodyMeasurement -> addNewBodyMeasurement(action.bodyEntryMeasurementId)
            is Action.AddBodyMeasurement -> addNewBodyMeasurement()
            Action.DecrementDateInterval -> decrementDateInterval()
            Action.IncrementDateInterval -> incrementDateInterval()
            is Action.SetDateInterval -> setDateInterval(action.dateInterval)
        }
    }

    private fun deleteBodyMeasurementEntry(id: Long) {
        viewModelScope.launch { deleteBodyMeasurementEntryUseCase(id) }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun addNewBodyMeasurement(bodyMeasurementEntryID: Long? = null) {
        viewModelScope.launch {
            navigationCommander.navigateTo(
                Routes.BodyMeasurement.newMeasurement(
                    bodyMeasurementID = routeData.bodyMeasurementID,
                    bodyMeasurementEntryID = bodyMeasurementEntryID ?: ID_NOT_SET,
                )
            )
        }
    }

    private fun setDateInterval(dateInterval: DateInterval) {
        this.dateInterval.value = dateInterval
    }

    private fun incrementDateInterval() {
        setDateInterval(dateInterval.value.increment())
    }

    private fun decrementDateInterval() {
        setDateInterval(dateInterval.value.decrement())
    }

    companion object {
        private const val DATE_INTERVAL_KEY = "dateInterval"
    }
}
