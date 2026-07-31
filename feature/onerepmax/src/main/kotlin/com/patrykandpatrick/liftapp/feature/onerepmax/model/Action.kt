package com.patrykandpatrick.liftapp.feature.onerepmax.model

sealed interface Action {
    data object PopBackStack : Action
}
