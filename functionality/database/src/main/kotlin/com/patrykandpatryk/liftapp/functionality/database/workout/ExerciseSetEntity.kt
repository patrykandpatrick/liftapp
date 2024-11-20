package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity

@Entity(
    tableName = "exercise_set",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["workout_id"],
            childColumns = ["exercise_set_workout_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exercise_id"],
            childColumns = ["exercise_set_exercise_id"],
            onDelete = ForeignKey.CASCADE
        ),
    ]
)
data class ExerciseSetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exercise_set_id")
    val id: Long = 0L,
    @ColumnInfo(name = "exercise_set_workout_id", index = true)
    val workoutID: Long,
    @ColumnInfo(name = "exercise_set_exercise_id", index = true)
    val exerciseID: Long,
    @ColumnInfo(name = "exercise_set_weight")
    val weight: Double? = null,
    @ColumnInfo(name = "exercise_set_weight_unit")
    val weightUnit: MassUnit? = null,
    @ColumnInfo(name = "exercise_set_reps")
    val reps: Int? = null,
    @ColumnInfo(name = "exercise_set_time")
    val timeMillis: Long? = null,
    @ColumnInfo(name = "exercise_set_distance")
    val distance: Double? = null,
    @ColumnInfo(name = "exercise_set_distance_unit")
    val distanceUnit: LongDistanceUnit? = null,
    @ColumnInfo(name = "exercise_set_kcal")
    val kcal: Double? = null,
    @ColumnInfo(name = "workout_exercise_set_index")
    val setIndex: Int,
)
