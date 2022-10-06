package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity

@Entity(
    tableName = "exercise_with_routine",
    primaryKeys = ["routine_id", "exercise_id"],
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routine_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
class ExerciseWithRoutineEntity(
    @ColumnInfo(name = "routine_id")
    val routineId: Long,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long,
)
