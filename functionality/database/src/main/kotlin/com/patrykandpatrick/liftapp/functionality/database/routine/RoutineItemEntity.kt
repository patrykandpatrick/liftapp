package com.patrykandpatrick.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType

@Entity(
    tableName = "routine_item",
    foreignKeys =
        [
            ForeignKey(
                entity = RoutineEntity::class,
                parentColumns = ["routine_id"],
                childColumns = ["routine_item_routine_id"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
    indices =
        [
            Index(value = ["routine_item_routine_id"]),
            Index(value = ["routine_item_routine_id", "routine_item_order_index"], unique = true),
        ],
)
data class RoutineItemEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "routine_item_id")
    val id: Long = ID_NOT_SET,
    @ColumnInfo(name = "routine_item_routine_id") val routineID: Long,
    @ColumnInfo(name = "routine_item_order_index") val orderIndex: Int,
    @ColumnInfo(name = "routine_item_type") val type: RoutineItemType,
)
