package com.patrykandpatryk.liftapp.feature.bodymeasurementlist.model

sealed class Action {
    data class OpenDetails(val bodyMeasurementID: Long) : Action()
}
