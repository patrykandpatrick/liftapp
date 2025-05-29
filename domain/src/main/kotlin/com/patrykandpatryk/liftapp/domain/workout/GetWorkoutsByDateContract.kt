package com.patrykandpatryk.liftapp.domain.workout

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

fun interface GetWorkoutsByDateContract {
    fun getWorkouts(date: LocalDate): Flow<List<Workout>>
}
