package com.patrykandpatryk.liftapp.feature.bodymeasurementdetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatryk.liftapp.core.mapper.BodyMeasurementEntryToChartEntryMapper
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurement
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.bodymeasurement.DeleteBodyMeasurementEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.FormatBodyMeasurementValueToStringUseCase
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = BodyMeasurementDetailViewModel.Factory::class)
class BodyMeasurementDetailViewModel @AssistedInject constructor(
    @Assisted bodyMeasurementID: Long,
    repository: BodyMeasurementRepository,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val chartEntryMapper: BodyMeasurementEntryToChartEntryMapper,
    private val deleteBodyMeasurementEntry: DeleteBodyMeasurementEntryUseCase,
    private val formatBodyMeasurementValueToString: FormatBodyMeasurementValueToStringUseCase,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Unit> {

    val chartEntryModelProducer = ChartEntryModelProducer()

    private val expandedItemId = MutableStateFlow<Long?>(null)

    private val newEntry = MutableStateFlow<ScreenState.NewEntry?>(null)

    override val state: StateFlow<ScreenState> = combine(
        repository.getBodyMeasurement(bodyMeasurementID),
        repository.getBodyMeasurementEntries(bodyMeasurementID),
        expandedItemId,
        newEntry,
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
        newEntry: ScreenState.NewEntry?,
    ): ScreenState.Populated = withContext(exceptionHandler) {
        chartEntryModelProducer.setEntries(chartEntryMapper(entries))

        ScreenState.Populated(
            bodyMeasurementID = bodyMeasurement.id,
            name = bodyMeasurement.name,
            entries = entries.map { entry ->
                ScreenState.Entry(
                    id = entry.id,
                    value = formatBodyMeasurementValueToString(entry.value),
                    date = entry.formattedDate.dateShort,
                    isExpanded = entry.id == expandedItemId,
                )
            },
            chartEntryModelProducer = chartEntryModelProducer,
            newEntry = newEntry,
        )
    }

    override fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.ExpandItem -> expandedItemId.update { currentId ->
                if (currentId == intent.id) null else intent.id
            }
            is Intent.DeleteBodyMeasurementEntry -> viewModelScope.launch { deleteBodyMeasurementEntry(intent.id) }
            is Intent.NewEntry -> newEntry.value = ScreenState.NewEntry(intent.bodyMeasurementID, intent.bodyMeasurementEntryID)
            is Intent.DismissNewEntry -> newEntry.value = null
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(bodyMeasurementID: Long): BodyMeasurementDetailViewModel
    }
}
