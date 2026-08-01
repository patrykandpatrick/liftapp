package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.chart.runSparklineTransaction
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementWithHistory
import com.patrykandpatrick.liftapp.domain.bodymeasurement.FormatBodyMeasurementValueUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementsWithHistoriesUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.getValueRange
import com.patrykandpatrick.liftapp.domain.bodymeasurement.invoke
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.feature.bodymeasurementlist.model.Action
import com.patrykandpatrick.liftapp.feature.bodymeasurementlist.model.ScreenState
import com.patrykandpatrick.liftapp.feature.bodymeasurementlist.model.isComposition
import com.patrykandpatrick.liftapp.feature.bodymeasurementlist.model.isFeatureable
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
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
    getBodyMeasurementsWithHistories: GetBodyMeasurementsWithHistoriesUseCase,
    exceptionHandler: CoroutineExceptionHandler,
    private val formatBodyMeasurementValue: FormatBodyMeasurementValueUseCase,
    private val navigationCommander: NavigationCommander,
) : ViewModel() {

    /**
     * A producer per measurement, by measurement ID. Only ever read and written from the single
     * coroutine that maps the flow below, which is what keeps a plain map safe here.
     */
    private val trendProducers = mutableMapOf<Long, CartesianChartModelProducer>()

    val state =
        getBodyMeasurementsWithHistories(TREND_LENGTH)
            .map { bodyMeasurements -> bodyMeasurements.map { it.toListItem() }.toScreenState() }
            .stateIn(
                scope = viewModelScope + exceptionHandler,
                started = SharingStarted.Eagerly,
                initialValue =
                    ScreenState(
                        featured = null,
                        composition = emptyList(),
                        measurements = emptyList(),
                    ),
            )

    private suspend fun BodyMeasurementWithHistory.toListItem(): BodyMeasurementListItem {
        val latestValue = latestEntry?.value

        return BodyMeasurementListItem(
            id = id,
            name = name,
            type = type,
            value =
                latestValue?.let { value ->
                    formatBodyMeasurementValue(value, previousEntry?.value)
                },
            trend = trendProducer(),
            progress = latestValue?.let { value -> progressOf(value) },
            sideBalance = (latestValue as? BodyMeasurementValue.DoubleValue)?.leftShare,
        )
    }

    /**
     * The producer behind this measurement's trend line, or null while it has too few readings to
     * draw one through. The producer is kept and refilled rather than replaced, which is what makes
     * a new reading animate into the line instead of replacing it; a measurement's producer is
     * reused for as long as the screen is open. Refilling with unchanged readings does nothing, so
     * this is cheap on the emissions that changed some other measurement.
     */
    private suspend fun BodyMeasurementWithHistory.trendProducer(): CartesianChartModelProducer? {
        if (entries.size < 2) return null
        // Entries arrive newest first; a trend line reads the other way round.
        val values = entries.asReversed().map { entry -> entry.value.primaryValue }
        return trendProducers
            .getOrPut(id) { CartesianChartModelProducer() }
            .apply { runSparklineTransaction(values) }
    }

    /**
     * Only the types with a range fixed in advance get a meter. A circumference has no ceiling to
     * measure a reading against, so filling a bar to some invented maximum would be a claim the app
     * cannot support.
     */
    private fun BodyMeasurementWithHistory.progressOf(value: BodyMeasurementValue): Float? {
        if (type != BodyMeasurementType.Percentage) return null
        val range = type.getValueRange(value.unit)
        val single = (value as? BodyMeasurementValue.SingleValue) ?: return null
        val span = range.endInclusive - range.start
        return if (span > 0) ((single.value - range.start) / span).toFloat() else null
    }

    private fun List<BodyMeasurementListItem>.toScreenState(): ScreenState {
        val featured = firstOrNull { item -> item.type.isFeatureable }

        return ScreenState(
            featured = featured,
            composition = filter { item -> item.type.isComposition },
            measurements = filter { item -> item !== featured && !item.type.isComposition },
        )
    }

    fun onAction(action: Action) {
        when (action) {
            is Action.OpenDetails ->
                navigateTo(Routes.BodyMeasurement.details(action.bodyMeasurementID))
            is Action.AddEntry ->
                navigateTo(Routes.BodyMeasurement.newMeasurement(action.bodyMeasurementID))
        }
    }

    private fun navigateTo(route: Any) {
        viewModelScope.launch { navigationCommander.navigateTo(route) }
    }

    private companion object {
        /** Enough points for the trend lines to have a shape without pulling the whole history. */
        const val TREND_LENGTH = 12
    }
}

/** The value a single-number display leads with, and the one a trend line follows. */
private val BodyMeasurementValue.primaryValue: Double
    get() =
        when (this) {
            is BodyMeasurementValue.SingleValue -> value
            is BodyMeasurementValue.DoubleValue -> left
        }

private val BodyMeasurementValue.DoubleValue.leftShare: Float?
    get() = (left + right).takeIf { it > 0 }?.let { total -> (left / total).toFloat() }
