package com.patrykandpatryk.liftapp.feature.bodydetails.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.body.BodyValues
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.feature.bodydetails.di.BodyId
import com.patrykandpatrick.vico.core.entry.ChartEntry
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
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
class BodyDetailsViewModel @Inject constructor(
    @BodyId bodyId: Long,
    repository: BodyRepository,
    private val unitConverter: UnitConverter,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val chartEntriesMapper: Mapper<List<BodyEntry>, List<List<ChartEntry>>>,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Unit> {

    val chartEntryModelProducer = ChartEntryModelProducer()

    private val expandedItemId = MutableStateFlow<Long?>(null)

    override val state: StateFlow<ScreenState> = combine(
        repository.getBody(bodyId),
        repository.getEntries(bodyId),
        expandedItemId,
        transform = ::mapPopulatedState,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ScreenState.Loading(
            bodyId = bodyId,
            chartEntryModelProducer = chartEntryModelProducer,
        ),
    )

    override val events: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)

    private suspend fun mapPopulatedState(
        body: Body,
        entries: List<BodyEntry>,
        expandedItemId: Long?,
    ): ScreenState.Populated = withContext(exceptionHandler) {

        chartEntryModelProducer.setEntries(chartEntriesMapper(entries))

        ScreenState.Populated(
            bodyId = body.id,
            name = body.name,
            entries = entries.map { entry ->
                ScreenState.Entry(
                    id = entry.id,
                    value = entry.values.toPrettyValue(),
                    date = entry.formattedDate.dateShort,
                    isExpanded = entry.id == expandedItemId,
                )
            },
            chartEntryModelProducer = chartEntryModelProducer,
        )
    }

    private suspend fun BodyValues.toPrettyValue(): String = when (this) {
        is BodyValues.Double -> unitConverter.convertToPreferredUnitAndFormat(
            from = unit,
            left,
            right,
        )

        is BodyValues.Single -> unitConverter.convertToPreferredUnitAndFormat(
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
