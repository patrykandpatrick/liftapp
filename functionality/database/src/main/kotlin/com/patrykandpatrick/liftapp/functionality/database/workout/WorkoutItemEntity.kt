package com.patrykandpatrick.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType

@Entity(
    tableName = "workout_item",
    foreignKeys =
        [
            ForeignKey(
                entity = WorkoutEntity::class,
                parentColumns = ["workout_id"],
                childColumns = ["workout_item_workout_id"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices =
        [
            Index(value = ["workout_item_workout_id"]),
            Index(value = ["workout_item_workout_id", "workout_item_order_index"], unique = true),
        ],
)
data class WorkoutItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "workout_item_id")
    val id: Long = ID_NOT_SET,
    @ColumnInfo(name = "workout_item_workout_id") val workoutID: Long,
    @ColumnInfo(name = "workout_item_order_index") val orderIndex: Int,
    @ColumnInfo(name = "workout_item_type") val type: RoutineItemType,
    @ColumnInfo(name = "workout_item_sets") val sets: Int?,
    @ColumnInfo(name = "workout_item_rest_time_millis") val restTimeMillis: Long?,
)
