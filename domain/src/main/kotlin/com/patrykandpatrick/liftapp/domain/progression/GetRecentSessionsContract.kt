package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import java.time.LocalDateTime

fun interface GetRecentSessionsContract {

    /**
     * The sets of the [limit] most recent workouts that included [exerciseID] and started before
     * [before], most recent first.
     */
    suspend fun getRecentSessions(
        exerciseID: Long,
        before: LocalDateTime,
        limit: Int,
    ): List<ExerciseSetGroup>
}
