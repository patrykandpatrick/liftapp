package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet

/**
 * One exercise's sets from one past session, in the order they were done.
 *
 * Only completed sets are of interest to the loop — an empty row a user never filled in says
 * nothing about how the session went — so [workingSets] is what everything reads.
 */
data class ExerciseSession(val sets: List<ExerciseSet>) {

    val workingSets: List<ExerciseSet> = sets.filter { it.isCompleted && it.reps != null }

    /** The set the loop judges the session by, which is the one done freshest. */
    val firstWorkingSet: ExerciseSet? = workingSets.firstOrNull()

    val hasWorkingSets: Boolean
        get() = workingSets.isNotEmpty()
}
