package com.patrykandpatryk.liftapp.domain.unit

import kotlinx.serialization.Transient

interface ValueUnit : java.io.Serializable {

    @Transient
    val hasLeadingSpace: Boolean
}
