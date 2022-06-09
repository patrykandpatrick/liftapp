package com.patrykandpatryk.liftapp.domain.measurement

import com.patrykandpatryk.liftapp.domain.model.Name

data class Measurement(
    val id: Long,
    val name: Name,
    val type: MeasurementType,
)
