package com.patrykandpatrick.liftapp.domain.target

fun interface UpsertExerciseTargetsContract {
    suspend fun upsertTargets(targets: List<ExerciseTarget>)
}
