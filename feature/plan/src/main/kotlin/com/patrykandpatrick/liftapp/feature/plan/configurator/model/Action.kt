package com.patrykandpatrick.liftapp.feature.plan.configurator.model

import com.patrykandpatrick.liftapp.domain.target.TrainingIntent
import com.patrykandpatrick.liftapp.feature.plan.configurator.ui.ScreenState

sealed interface Action {
    data object PopBackStack : Action

    data class SetIntent(val intent: TrainingIntent) : Action

    data class SetRebuildTargets(val rebuild: Boolean) : Action

    data class Save(val state: ScreenState) : Action
}
