package com.patrykandpatryk.liftapp.domain.measurement

import com.patrykandpatryk.liftapp.domain.model.Name

data class MeasurementWithLatestEntry(
    val id: Long,
    val name: Name,
    val type: MeasurementType,
    val latestEntry: MeasurementEntry?,
)
