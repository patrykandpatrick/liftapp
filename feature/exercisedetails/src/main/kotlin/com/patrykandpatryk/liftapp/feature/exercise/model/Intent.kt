package com.patrykandpatryk.liftapp.feature.exercise.model

sealed interface Intent {

    object ShowDeleteDialog : Intent

    object HideDeleteDialog : Intent

    object Delete : Intent

    object Edit : Intent
}
