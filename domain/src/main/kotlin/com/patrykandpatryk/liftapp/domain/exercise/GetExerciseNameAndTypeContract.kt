package com.patrykandpatryk.liftapp.domain.exercise

import kotlinx.coroutines.flow.Flow

fun interface GetExerciseNameAndTypeContract {
    fun getExerciseNameAndType(id: Long): Flow<ExerciseNameAndType?>
}
