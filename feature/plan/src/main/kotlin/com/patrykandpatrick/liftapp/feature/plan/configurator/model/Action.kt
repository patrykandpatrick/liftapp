package com.patrykandpatrick.liftapp.feature.plan.configurator.model

import com.patrykandpatrick.liftapp.feature.plan.configurator.ui.ScreenState

sealed interface Action {
    data object PopBackStack : Action

    data class Save(val state: ScreenState) : Action
}
