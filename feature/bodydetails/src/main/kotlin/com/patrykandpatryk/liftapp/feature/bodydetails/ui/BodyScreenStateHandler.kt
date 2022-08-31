package com.patrykandpatryk.liftapp.feature.bodydetails.ui

import com.patrykandpatryk.liftapp.domain.body.Body
import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.body.BodyValues
import com.patrykandpatryk.liftapp.domain.di.IODispatcher
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.feature.bodydetails.di.BodyId
import com.patrykandpatryk.vico.core.entry.ChartEntry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class BodyScreenStateHandler @Inject constructor(
    @BodyId bodyId: Long,
    repository: BodyRepository,
    private val unitConverter: UnitConverter,
    exceptionHandler: CoroutineExceptionHandler,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val chartEntriesMapper: Mapper<List<BodyEntry>, List<List<ChartEntry>>>,
) : ScreenStateHandler<ScreenState, Intent, Unit> {

    private val scope = CoroutineScope(dispatcher + exceptionHandler)

    private val expandedItemId = MutableStateFlow<Long?>(null)

    override val state: StateFlow<ScreenState> =
        combine(
            repository.getBody(bodyId),
            repository.getEntries(bodyId),
            expandedItemId,
            transform = ::mapPopulatedState,
        ).stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ScreenState.Loading(bodyId),
        )

    override val events: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)

    private suspend fun mapPopulatedState(
        body: Body,
        entries: List<BodyEntry>,
        expandedItemId: Long?,
    ): ScreenState.Populated {
        return ScreenState.Populated(
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
            chartEntries = chartEntriesMapper(entries),
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

    override fun close() {
        scope.cancel()
    }
}
