package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.BodyMeasurementDetailsRouteData
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.liftapp.core.mapper.BodyMeasurementEntryToChartEntryMapper
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.bodymeasurement.DeleteBodyMeasurementEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.FormatBodyMeasurementValueToStringUseCase
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model.Action
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.model.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class BodyMeasurementDetailViewModel
@Inject
constructor(
    private val routeData: BodyMeasurementDetailsRouteData,
    repository: BodyMeasurementRepository,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val chartEntryMapper: BodyMeasurementEntryToChartEntryMapper,
    private val deleteBodyMeasurementEntryUseCase: DeleteBodyMeasurementEntryUseCase,
    private val formatBodyMeasurementValueToStringUseCase:
        FormatBodyMeasurementValueToStringUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    val chartEntryModelProducer = ChartEntryModelProducer()

    private val expandedItemId = MutableStateFlow<Long?>(null)

    val state: StateFlow<Loadable<ScreenState>> =
        combine(
                repository.getBodyMeasurement(routeData.bodyMeasurementID),
                repository.getBodyMeasurementEntries(routeData.bodyMeasurementID),
                expandedItemId,
                transform = ::mapPopulatedState,
            )
            .toLoadableStateFlow(viewModelScope)

    private suspend fun mapPopulatedState(
        bodyMeasurement: BodyMeasurement,
        entries: List<BodyMeasurementEntry>,
        expandedItemId: Long?,
    ): ScreenState =
        withContext(exceptionHandler) {
            chartEntryModelProducer.setEntries(chartEntryMapper(entries))

            ScreenState(
                bodyMeasurementID = bodyMeasurement.id,
                name = bodyMeasurement.name,
                entries =
                    entries.map { entry ->
                        ScreenState.Entry(
                            id = entry.id,
                            value = formatBodyMeasurementValueToStringUseCase(entry.value),
                            date = entry.formattedDate.dateShort,
                            isExpanded = entry.id == expandedItemId,
                        )
                    },
                chartEntryModelProducer = chartEntryModelProducer,
            )
        }

    fun onAction(action: Action) {
        when (action) {
            is Action.ExpandItem -> expandItem(action.id)
            is Action.DeleteBodyMeasurementEntry -> deleteBodyMeasurementEntry(action.id)
            is Action.PopBackStack -> popBackStack()
            is Action.EditBodyMeasurement -> addNewBodyMeasurement(action.bodyEntryMeasurementId)
            is Action.AddBodyMeasurement -> addNewBodyMeasurement()
        }
    }

    private fun expandItem(id: Long) {
        expandedItemId.update { currentId -> if (currentId == id) null else id }
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
}
