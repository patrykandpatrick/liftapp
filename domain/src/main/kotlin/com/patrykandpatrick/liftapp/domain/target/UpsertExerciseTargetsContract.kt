package com.patrykandpatrick.liftapp.domain.target

interface UpsertExerciseTargetsContract {
    /** Replaces whatever is stored, edits included. */
    suspend fun upsertTargets(targets: List<ExerciseTarget>)

    /**
     * Writes only the targets that are not there yet, so a plan that grew a day or an exercise
     * gains a prescription for it without the rest being re-authored underneath the user.
     */
    suspend fun addMissingTargets(targets: List<ExerciseTarget>)
}
