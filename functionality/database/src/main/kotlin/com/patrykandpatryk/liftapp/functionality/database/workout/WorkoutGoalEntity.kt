package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity

@Entity(
    tableName = "workout_goal",
    foreignKeys =
        [
            ForeignKey(
                entity = WorkoutEntity::class,
                parentColumns = ["workout_id"],
                childColumns = ["workout_goal_workout_id"],
                onDelete = ForeignKey.CASCADE,
                deferred = true,
            ),
            ForeignKey(
                entity = ExerciseEntity::class,
                parentColumns = ["exercise_id"],
                childColumns = ["workout_goal_exercise_id"],
                onDelete = ForeignKey.CASCADE,
                deferred = true,
            ),
        ],
)
data class WorkoutGoalEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "workout_goal_id") val id: Long,
    @ColumnInfo(name = "workout_goal_workout_id", index = true) val workoutID: Long,
    @ColumnInfo(name = "workout_goal_exercise_id", index = true) val exerciseID: Long,
    @ColumnInfo(name = "workout_goal_min_reps") val minReps: Int,
    @ColumnInfo(name = "workout_goal_max_reps") val maxReps: Int,
    @ColumnInfo(name = "workout_goal_sets") val sets: Int,
    @ColumnInfo(name = "workout_goal_rest_time") val restTimeMillis: Long,
    @ColumnInfo(name = "workout_goal_duration_millis") val durationMillis: Long,
    @ColumnInfo(name = "workout_goal_distance") val distance: Double,
    @ColumnInfo(name = "workout_goal_distance_unit") val distanceUnit: LongDistanceUnit,
    @ColumnInfo(name = "workout_goal_calories") val calories: Double,
)
