package com.patrykandpatryk.liftapp.domain.body

import kotlinx.serialization.Serializable

@Serializable
enum class BodyType(val fields: Int) {
    Weight(fields = 1),
    Length(fields = 1),
    LengthTwoSides(fields = 2),
    Percentage(fields = 1),
}
