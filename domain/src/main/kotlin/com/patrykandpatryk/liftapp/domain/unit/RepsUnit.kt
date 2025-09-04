package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("reps")
object RepsUnit : ValueUnit {
    private fun readResolve(): Any = RepsUnit

    override val hasLeadingSpace: Boolean = true
    override val isMetric: Boolean = true
}
