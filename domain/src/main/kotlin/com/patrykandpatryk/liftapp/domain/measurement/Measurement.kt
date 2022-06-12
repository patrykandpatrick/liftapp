package com.patrykandpatryk.liftapp.domain.measurement

data class Measurement(
    val id: Long,
    val name: String,
    val type: MeasurementType,
)
