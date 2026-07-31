package com.patrykandpatrick.liftapp.feature.newroutine.model

internal sealed interface Action {
    data object SaveRoutine : Action

    data object PopBackStack : Action
}
