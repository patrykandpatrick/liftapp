package com.patrykandpatrick.liftapp.domain.workout

/**
 * Reads one window of finished workouts, newest first. The journal pages through these rather than
 * observing them all, since a workout carries every set it recorded.
 */
fun interface GetPastWorkoutPageContract {
    suspend fun getPastWorkoutPage(limit: Int, offset: Int): List<Workout>
}
