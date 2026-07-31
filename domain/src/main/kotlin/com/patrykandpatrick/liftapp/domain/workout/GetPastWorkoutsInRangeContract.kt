package com.patrykandpatrick.liftapp.domain.workout

import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

fun interface GetPastWorkoutsInRangeContract {
    fun getPastWorkouts(start: LocalDateTime, endExclusive: LocalDateTime): Flow<List<Workout>>
}
