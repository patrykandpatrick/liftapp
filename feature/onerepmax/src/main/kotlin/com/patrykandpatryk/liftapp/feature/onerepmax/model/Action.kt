package com.patrykandpatryk.liftapp.feature.onerepmax.model

sealed interface Action {
    data object PopBackStack : Action
}
