package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.LocalDateTime

class ExerciseSetWithWorkoutDataDto(
    @Embedded val exerciseSet: ExerciseSetEntity,
    @ColumnInfo(name = "workout_start_date") val workoutStartDate: LocalDateTime,
    @ColumnInfo(name = "workout_name") val workoutName: String,
)
