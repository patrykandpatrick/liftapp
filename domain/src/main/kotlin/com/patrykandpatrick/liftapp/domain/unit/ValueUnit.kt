package com.patrykandpatrick.liftapp.domain.unit

import kotlinx.serialization.Transient

interface ValueUnit : java.io.Serializable {

    @Transient val hasLeadingSpace: Boolean

    @Transient val isMetric: Boolean
}
