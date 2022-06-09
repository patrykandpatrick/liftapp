package com.patrykandpatryk.liftapp.domain.measurement

import kotlinx.serialization.Serializable

@Serializable
sealed class MeasurementValues {

    @Serializable
    class Single(val value: Float) : MeasurementValues()

    @Serializable
    class Double(val left: Float, val right: Float) : MeasurementValues()
}
