package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.liftapp.core.mapper.BodyMeasurementEntryToChartEntryMapper
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.di.BodyMeasurementID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class BodyMeasurementDetailViewModel @Inject constructor(
    @BodyMeasurementID bodyMeasurementID: Long,
    repository: BodyMeasurementRepository,
    private val unitConverter: UnitConverter,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val chartEntryMapper: BodyMeasurementEntryToChartEntryMapper,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Unit> {

    val chartEntryModelProducer = ChartEntryModelProducer()

    private val expandedItemId = MutableStateFlow<Long?>(null)

    override val state: StateFlow<ScreenState> = combine(
        repository.getBodyMeasurement(bodyMeasurementID),
        repository.getBodyMeasurementEntries(bodyMeasurementID),
        expandedItemId,
        transform = ::mapPopulatedState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ScreenState.Loading(
            bodyMeasurementID = bodyMeasurementID,
            chartEntryModelProducer = chartEntryModelProducer,
        ),
    )

    override val events: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)

    private suspend fun mapPopulatedState(
        bodyMeasurement: BodyMeasurement,
        entries: List<BodyMeasurementEntry>,
        expandedItemId: Long?,
    ): ScreenState.Populated = withContext(exceptionHandler) {

        chartEntryModelProducer.setEntries(chartEntryMapper(entries))

        ScreenState.Populated(
            bodyMeasurementID = bodyMeasurement.id,
            name = bodyMeasurement.name,
            entries = entries.map { entry ->
                ScreenState.Entry(
                    id = entry.id,
                    value = entry.value.toPrettyValue(),
                    date = entry.formattedDate.dateShort,
                    isExpanded = entry.id == expandedItemId,
                )
            },
            chartEntryModelProducer = chartEntryModelProducer,
        )
    }

    private suspend fun BodyMeasurementValue.toPrettyValue(): String = when (this) {
        is BodyMeasurementValue.Double -> unitConverter.convertToPreferredUnitAndFormat(
            from = unit,
            left,
            right,
        )

        is BodyMeasurementValue.Single -> unitConverter.convertToPreferredUnitAndFormat(
            from = unit,
            value,
        )
    }

    override fun handleIntent(intent: Intent) = when (intent) {
        is Intent.ExpandItem -> expandedItemId.update { currentId ->
            if (currentId == intent.id) null else intent.id
        }
    }
}
