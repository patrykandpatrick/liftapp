package com.patrykandpatrick.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity

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
    /**
     * Renamed rather than reusing `workout_goal_sets`, which held a set *count* until schema 16. A
     * restore matches archive columns to table columns by name and skips what it does not
     * recognise, so keeping the name would have fed an old archive's `3` to a JSON parser. Under a
     * new name the stale column is ignored and this one falls back to its default.
     */
    @ColumnInfo(name = "workout_goal_set_targets", defaultValue = "[]") val sets: List<SetTarget>,
    @ColumnInfo(name = "workout_goal_rest_time") val restTimeMillis: Long,
    @ColumnInfo(name = "workout_goal_distance_unit") val distanceUnit: LongDistanceUnit,
)
