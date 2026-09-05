package com.patrykandpatrick.liftapp.functionality.database.target

import androidx.room.ColumnInfo
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.target.SetTarget

data class TargetWithMusclesDto(
    @ColumnInfo(name = "exercise_target_sets") val sets: List<SetTarget>,
    @ColumnInfo(name = "exercise_type") val exerciseType: ExerciseType,
    @ColumnInfo(name = "exercise_main_muscles") val mainMuscles: List<Muscle>,
    @ColumnInfo(name = "exercise_secondary_muscles") val secondaryMuscles: List<Muscle>,
    @ColumnInfo(name = "exercise_tertiary_muscles") val tertiaryMuscles: List<Muscle>,
)
