package com.patrykandpatryk.liftapp.domain.exerciseset

import com.patrykandpatryk.liftapp.domain.date.DateInterval
import kotlinx.coroutines.flow.Flow

interface GetExerciseSetsUseCase {
    fun getExerciseSets(exerciseID: Long, dateInterval: DateInterval): Flow<List<ExerciseSetGroup>>
}

operator fun GetExerciseSetsUseCase.invoke(
    exerciseID: Long,
    dateInterval: DateInterval,
): Flow<List<ExerciseSetGroup>> = getExerciseSets(exerciseID, dateInterval)
