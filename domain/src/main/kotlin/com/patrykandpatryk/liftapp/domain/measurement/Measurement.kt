package com.patrykandpatryk.liftapp.domain.measurement

import com.patrykandpatryk.liftapp.domain.model.Name

data class Measurement(
    val id: Long,
    val name: String,
    val type: MeasurementType,
) {

    data class Insert(
        val name: Name,
        val type: MeasurementType,
    )
}
