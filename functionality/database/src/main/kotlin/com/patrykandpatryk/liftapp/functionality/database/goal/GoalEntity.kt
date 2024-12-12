package com.patrykandpatryk.liftapp.functionality.database.goal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity

@Entity(
    tableName = "goal",
    foreignKeys =
        [
            ForeignKey(
                entity = RoutineEntity::class,
                parentColumns = ["routine_id"],
                childColumns = ["goal_routine_id"],
                onDelete = ForeignKey.CASCADE,
                deferred = true,
            ),
            ForeignKey(
                entity = ExerciseEntity::class,
                parentColumns = ["exercise_id"],
                childColumns = ["goal_exercise_id"],
                onDelete = ForeignKey.CASCADE,
                deferred = true,
            ),
        ],
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "goal_id") val id: Long,
    @ColumnInfo(name = "goal_routine_id", index = true) val routineID: Long,
    @ColumnInfo(name = "goal_exercise_id", index = true) val exerciseID: Long,
    @ColumnInfo(name = "goal_min_reps") val minReps: Int,
    @ColumnInfo(name = "goal_max_reps") val maxReps: Int,
    @ColumnInfo(name = "goal_sets") val sets: Int,
    @ColumnInfo(name = "goal_break_duration") val breakDurationMillis: Long,
)
