package com.patrykandpatrick.liftapp.feature.plan.targets.model

import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets

sealed interface Action {
    data object PopBackStack : Action

    data class Edit(val item: PlanDayTargets.Item) : Action

    data object DismissEditor : Action

    data class SetRir(val rir: Int?) : Action

    data object SaveEdit : Action
}
