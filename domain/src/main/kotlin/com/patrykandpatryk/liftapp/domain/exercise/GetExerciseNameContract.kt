package com.patrykandpatryk.liftapp.domain.exercise

import com.patrykandpatryk.liftapp.domain.model.Name
import kotlinx.coroutines.flow.Flow

fun interface GetExerciseNameContract {
    fun getExerciseName(id: Long): Flow<Name?>
}
