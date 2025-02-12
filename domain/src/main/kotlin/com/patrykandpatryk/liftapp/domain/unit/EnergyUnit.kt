package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("energy")
enum class EnergyUnit : ValueUnit {
    @SerialName("kiloCalorie")
    KiloCalorie {
        override val hasLeadingSpace: Boolean = true
        override val isMetric: Boolean = true
    }
}
