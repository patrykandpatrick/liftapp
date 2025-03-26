package com.patrykandpatryk.liftapp.feature.newexercise.model

sealed interface Action {
    data object PopBackStack : Action
}
