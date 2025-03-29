package com.patrykandpatryk.liftapp.feature.routine.model

import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.core.model.MuscleModel
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem

@Immutable
data class ScreenState(
    val name: String,
    val showDeleteDialog: Boolean,
    val imagePath: String?,
    val exercises: List<RoutineExerciseItem>,
    val muscles: List<MuscleModel>,
)
