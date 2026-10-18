package com.patrykandpatrick.liftapp.feature.plan.targets.model

import com.patrykandpatrick.liftapp.domain.target.PlanDayTargets

sealed interface Action {
    data object PopBackStack : Action

    data object PreviousCycle : Action

    data object NextCycle : Action

    data class Edit(val item: PlanDayTargets.Item) : Action

    data object DismissEditor : Action

    /** Null selects every set at once. */
    data class SelectSet(val index: Int?) : Action

    data class SetRir(val rir: Int?) : Action

    data class UseLoadPercent(val percent: Int) : Action

    data object SaveEdit : Action
}
