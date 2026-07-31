package com.patrykandpatrick.liftapp.domain.bodymeasurement

import com.patrykandpatrick.liftapp.domain.model.Name

data class BodyMeasurement(val id: Long, val name: String, val type: BodyMeasurementType) {

    data class Insert(val name: Name, val type: BodyMeasurementType)
}
