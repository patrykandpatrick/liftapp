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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class BodyScreenStateHandler @Inject constructor(
    @BodyId bodyId: Long,
    repository: BodyRepository,
    private val unitConverter: UnitConverter,
    exceptionHandler: CoroutineExceptionHandler,
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val chartEntriesMapper: Mapper<List<BodyEntry>, List<List<ChartEntry>>>,
) : ScreenStateHandler<ScreenState, Unit, Unit> {

    private val scope = CoroutineScope(dispatcher + exceptionHandler)

    override val state: StateFlow<ScreenState> =
        combine(
            repository.getBody(bodyId),
            repository.getEntries(bodyId),
        ) { body, entries -> mapPopulatedState(body, entries) }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = ScreenState.Loading(bodyId),
            )

    override val events: MutableSharedFlow<Unit> = MutableSharedFlow(replay = 1)

    private suspend fun mapPopulatedState(
        body: Body,
        entries: List<BodyEntry>,
    ): ScreenState.Populated {
        return ScreenState.Populated(
            bodyId = body.id,
            name = body.name,
            entries = entries.map { entry ->
                ScreenState.Entry(
                    id = entry.id,
                    value = entry.values.toPrettyValue(),
                    date = entry.formattedDate.dateShort,
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

    override fun handleIntent(intent: Unit) = Unit

    override fun close() {
        scope.cancel()
    }
}
