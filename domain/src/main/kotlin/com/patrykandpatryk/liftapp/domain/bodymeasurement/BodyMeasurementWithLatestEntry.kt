package com.patrykandpatryk.liftapp.domain.bodymeasurement

data class BodyMeasurementWithLatestEntry(
    val id: Long,
    val name: String,
    val type: BodyMeasurementType,
    val latestEntry: BodyMeasurementEntry?,
)
