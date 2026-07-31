package com.patrykandpatrick.liftapp.domain.exerciseset

import com.patrykandpatrick.liftapp.domain.date.DateInterval
import kotlinx.coroutines.flow.Flow

interface GetExerciseSetsUseCase {
    fun getExerciseSets(exerciseID: Long, dateInterval: DateInterval): Flow<List<ExerciseSetGroup>>

    fun hasExerciseSets(exerciseID: Long): Flow<Boolean>
}

operator fun GetExerciseSetsUseCase.invoke(
    exerciseID: Long,
    dateInterval: DateInterval,
): Flow<List<ExerciseSetGroup>> = getExerciseSets(exerciseID, dateInterval)
