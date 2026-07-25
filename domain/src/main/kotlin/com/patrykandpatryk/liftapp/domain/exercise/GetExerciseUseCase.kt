package com.patrykandpatryk.liftapp.domain.exercise

import kotlinx.coroutines.flow.Flow

fun interface GetExerciseUseCase {
    fun getExercise(id: Long): Flow<Exercise?>
}

operator fun GetExerciseUseCase.invoke(id: Long): Flow<Exercise?> = getExercise(id)
