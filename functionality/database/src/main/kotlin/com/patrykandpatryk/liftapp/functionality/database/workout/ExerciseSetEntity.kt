package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit

@Entity(tableName = "exercise_set")
data class ExerciseSetEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exercise_set_id")
    val id: Long = ID_NOT_SET,
    @ColumnInfo(name = "exercise_set_weight")
    val weight: Double?,
    @ColumnInfo(name = "exercise_set_weight_unit")
    val weightUnit: MassUnit?,
    @ColumnInfo(name = "exercise_set_reps")
    val reps: Int?,
    @ColumnInfo(name = "exercise_set_time")
    val timeMillis: Long?,
    @ColumnInfo(name = "exercise_set_distance")
    val distance: Double?,
    @ColumnInfo(name = "exercise_set_distance_unit")
    val distanceUnit: LongDistanceUnit?,
)
