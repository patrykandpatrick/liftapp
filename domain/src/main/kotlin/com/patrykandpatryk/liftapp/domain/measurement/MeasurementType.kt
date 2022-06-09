package com.patrykandpatryk.liftapp.domain.measurement

import kotlinx.serialization.Serializable

@Serializable
enum class MeasurementType {
    Weight,
    Length,
    LengthTwoSides,
    Percentage,
}
