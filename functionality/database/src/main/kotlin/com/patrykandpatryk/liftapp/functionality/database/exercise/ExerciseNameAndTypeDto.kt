package com.patrykandpatryk.liftapp.functionality.database.exercise

import androidx.room.ColumnInfo
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name

data class ExerciseNameAndTypeDto(
    @ColumnInfo(name = "exercise_name", index = true) val name: Name,
    @ColumnInfo(name = "exercise_type", index = true) val type: ExerciseType,
)
