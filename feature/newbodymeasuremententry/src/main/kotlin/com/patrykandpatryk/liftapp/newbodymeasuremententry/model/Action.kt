package com.patrykandpatryk.liftapp.newbodymeasuremententry.model

sealed interface Action {
    data object PopBackStack : Action
}
