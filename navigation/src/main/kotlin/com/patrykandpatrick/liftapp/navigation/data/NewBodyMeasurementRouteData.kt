package com.patrykandpatrick.liftapp.navigation.data

import kotlinx.serialization.Serializable

@Serializable
data class NewBodyMeasurementRouteData
internal constructor(val bodyMeasurementID: Long, val bodyMeasurementEntryID: Long)
