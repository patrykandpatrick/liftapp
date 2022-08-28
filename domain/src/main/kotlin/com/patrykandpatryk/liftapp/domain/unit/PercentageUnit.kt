package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("percentage")
object PercentageUnit : ValueUnit {

    override val hasLeadingSpace: Boolean = false
}
