package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui

import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValueDisplay
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer

/**
 * One measurement as the list shows it. [value] stays null until the measurement has been logged
 * once, which is what turns its tile into an invitation to add the first entry.
 */
data class BodyMeasurementListItem(
    val id: Long,
    val name: String,
    val type: BodyMeasurementType,
    val value: BodyMeasurementValueDisplay?,
    /**
     * Feeds the trend line its recent readings, or null when there are too few to make a line. The
     * readings are left in their stored unit: every unit conversion here is a plain scale factor,
     * so it changes the line's values but not its shape, and the line is scaled to its own range
     * anyway. The producer outlives any one set of readings, so it is the same instance across
     * updates — see [BodyMeasurementListViewModel].
     */
    val trend: CartesianChartModelProducer?,
    /** Where the latest reading sits in 0..1, for the types whose range is known up front. */
    val progress: Float?,
    /**
     * The left side's share of the two sides, for measurements taken on both. Derived from the raw
     * values rather than the formatted ones, which a locale's decimal separator would break.
     */
    val sideBalance: Float?,
)
