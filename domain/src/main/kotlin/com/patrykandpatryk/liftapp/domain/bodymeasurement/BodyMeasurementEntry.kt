package com.patrykandpatryk.liftapp.domain.bodymeasurement

import java.time.LocalDateTime

data class BodyMeasurementEntry(
    val id: Long,
    val value: BodyMeasurementValue,
    val localDateTime: LocalDateTime,
)
