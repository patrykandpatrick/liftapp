package com.patrykandpatrick.liftapp.feature.bodymeasurementlist.model

import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.feature.bodymeasurementlist.ui.BodyMeasurementListItem

/**
 * The list split by how each measurement is best read. [BodyMeasurementType] decides the split, so
 * a measurement the user adds later lands in the right group without being named anywhere.
 */
data class ScreenState(
    /** The measurement given the headline treatment: the first one weighed. */
    val featured: BodyMeasurementListItem?,
    /** Percentages, which have a known range and so can be shown as meters. */
    val composition: List<BodyMeasurementListItem>,
    /** Everything measured with a tape, plus any weight beyond the featured one. */
    val measurements: List<BodyMeasurementListItem>,
)

/** Fit to lead the screen, being the reading looked for before any other. */
internal val BodyMeasurementType.isFeatureable: Boolean
    get() = this == BodyMeasurementType.Weight

/** Read as a share of the body rather than a size of it, which is what earns a meter. */
internal val BodyMeasurementType.isComposition: Boolean
    get() = this == BodyMeasurementType.Percentage
