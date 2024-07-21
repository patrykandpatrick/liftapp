package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("percentage")
object PercentageUnit : ValueUnit {
    private fun readResolve(): Any = PercentageUnit

    override val isMetric: Boolean = false

    override val hasLeadingSpace: Boolean = false
}
