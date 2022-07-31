package com.patrykandpatryk.liftapp.domain.measurement

import kotlinx.serialization.Serializable

@Serializable
enum class MeasurementType(val fields: Int) {
    Weight(fields = 1),
    Length(fields = 1),
    LengthTwoSides(fields = 2),
    Percentage(fields = 1),
}
