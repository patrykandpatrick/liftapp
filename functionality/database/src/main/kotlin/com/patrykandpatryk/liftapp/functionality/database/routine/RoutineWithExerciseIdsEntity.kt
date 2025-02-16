package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.ColumnInfo

data class RoutineWithExerciseIdsEntity(
    @ColumnInfo("routine_id") val id: Long,
    @ColumnInfo("routine_name") val name: String,
    @ColumnInfo("exercise_ids") val exerciseIDs: String,
)
