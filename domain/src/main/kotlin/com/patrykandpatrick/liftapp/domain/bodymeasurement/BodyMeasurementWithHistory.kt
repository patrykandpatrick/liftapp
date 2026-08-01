package com.patrykandpatrick.liftapp.domain.bodymeasurement

/**
 * A body measurement together with its most recent entries, newest first. The list is capped by
 * whoever loads it, so it is a recent window rather than the measurement's full history.
 */
data class BodyMeasurementWithHistory(
    val id: Long,
    val name: String,
    val type: BodyMeasurementType,
    val entries: List<BodyMeasurementEntry>,
) {
    val latestEntry: BodyMeasurementEntry?
        get() = entries.firstOrNull()

    val previousEntry: BodyMeasurementEntry?
        get() = entries.getOrNull(1)
}
