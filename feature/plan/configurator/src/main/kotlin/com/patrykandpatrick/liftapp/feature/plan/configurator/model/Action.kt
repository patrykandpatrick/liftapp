package com.patrykandpatrick.liftapp.feature.plan.configurator.model

sealed interface Action {
    data object PopBackStack : Action

    data object Save : Action
}
