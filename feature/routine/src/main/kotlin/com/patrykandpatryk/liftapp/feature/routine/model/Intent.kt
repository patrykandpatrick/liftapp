package com.patrykandpatryk.liftapp.feature.routine.model

sealed interface Intent {

    object Edit : Intent

    object ShowDeleteDialog : Intent

    object HideDeleteDialog : Intent

    object Delete : Intent

    data class DeleteExercise(val exerciseId: Long) : Intent

    data class Reorder(val from: Int, val to: Int) : Intent
}
