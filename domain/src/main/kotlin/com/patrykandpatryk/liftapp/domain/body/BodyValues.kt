package com.patrykandpatryk.liftapp.domain.body

import kotlinx.serialization.Serializable

@Serializable
sealed class BodyValues {

    @Serializable
    class Single(val value: Float) : BodyValues()

    @Serializable
    class Double(val left: Float, val right: Float) : BodyValues()
}
