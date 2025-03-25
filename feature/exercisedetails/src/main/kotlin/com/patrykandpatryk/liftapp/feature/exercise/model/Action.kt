package com.patrykandpatryk.liftapp.feature.exercise.model

sealed interface Action {

    data object ShowDeleteDialog : Action

    data object HideDeleteDialog : Action

    data object Delete : Action

    data object Edit : Action

    data object PopBackStack : Action
}
