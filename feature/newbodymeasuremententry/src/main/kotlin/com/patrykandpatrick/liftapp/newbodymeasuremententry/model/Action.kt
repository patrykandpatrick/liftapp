package com.patrykandpatrick.liftapp.newbodymeasuremententry.model

sealed interface Action {
    data object PopBackStack : Action

    data class Save(val state: NewBodyMeasurementState) : Action
}
