package com.patrykandpatrick.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "superset",
    primaryKeys = ["superset_routine_item_id"],
    foreignKeys =
        [
            ForeignKey(
                entity = RoutineItemEntity::class,
                parentColumns = ["routine_item_id"],
                childColumns = ["superset_routine_item_id"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
)
data class SupersetEntity(
    @ColumnInfo(name = "superset_routine_item_id") val routineItemID: Long,
    @ColumnInfo(name = "superset_sets") val sets: Int,
    @ColumnInfo(name = "superset_rest_time_millis") val restTimeMillis: Long,
)
