package com.patrykandpatryk.liftapp.feature.routine.model

sealed interface Intent {

    object Edit : Intent

    object ShowDeleteDialog : Intent

    object HideDeleteDialog : Intent

    object Delete : Intent
}
