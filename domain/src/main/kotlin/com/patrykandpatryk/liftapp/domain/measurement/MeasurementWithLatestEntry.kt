package com.patrykandpatryk.liftapp.domain.measurement

data class MeasurementWithLatestEntry(
    val id: Long,
    val name: String,
    val type: MeasurementType,
    val latestEntry: MeasurementEntry?,
)
