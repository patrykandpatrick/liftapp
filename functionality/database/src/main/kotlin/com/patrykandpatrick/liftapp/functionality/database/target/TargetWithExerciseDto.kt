package com.patrykandpatrick.liftapp.functionality.database.target

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name

data class TargetWithExerciseDto(
    @Embedded val target: ExerciseTargetEntity,
    @ColumnInfo(name = "exercise_name") val exerciseName: Name,
    @ColumnInfo(name = "exercise_type") val exerciseType: ExerciseType,
    @ColumnInfo(name = "routine_name") val routineName: String?,
)
