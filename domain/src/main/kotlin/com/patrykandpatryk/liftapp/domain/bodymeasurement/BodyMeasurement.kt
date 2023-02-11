package com.patrykandpatryk.liftapp.domain.bodymeasurement

import com.patrykandpatryk.liftapp.domain.model.Name

data class BodyMeasurement(
    val id: Long,
    val name: String,
    val type: BodyMeasurementType,
) {

    data class Insert(
        val name: Name,
        val type: BodyMeasurementType,
    )
}
