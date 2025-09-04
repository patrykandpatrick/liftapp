package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("duration")
object DurationUnit : ValueUnit {
    private fun readResolve(): Any = DurationUnit

    override val hasLeadingSpace: Boolean = true
    override val isMetric: Boolean = true
}
