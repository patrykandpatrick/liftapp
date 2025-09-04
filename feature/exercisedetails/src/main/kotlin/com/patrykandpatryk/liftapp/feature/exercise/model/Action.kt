package com.patrykandpatryk.liftapp.feature.exercise.model

import com.patrykandpatryk.liftapp.domain.date.DateInterval
import com.patrykandpatryk.liftapp.domain.exerciseset.ExerciseSummaryType

sealed interface Action {

    data object ShowDeleteDialog : Action

    data object HideDeleteDialog : Action

    data object Delete : Action

    data object Edit : Action

    data object PopBackStack : Action

    data class SetDateInterval(val dateInterval: DateInterval) : Action

    data class SetSummaryType(val summaryType: ExerciseSummaryType) : Action

    data object IncrementDateInterval : Action

    data object DecrementDateInterval : Action
}
