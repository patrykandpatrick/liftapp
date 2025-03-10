package com.patrykandpatryk.liftapp.functionality.database.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity

@Entity(
    tableName = "plan_item",
    primaryKeys = ["plan_item_plan_id", "plan_item_routine_id"],
    indices = [Index("plan_item_plan_id"), Index("plan_item_routine_id")],
    foreignKeys =
        [
            ForeignKey(
                entity = PlanEntity::class,
                parentColumns = ["plan_id"],
                childColumns = ["plan_item_plan_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = RoutineEntity::class,
                parentColumns = ["routine_id"],
                childColumns = ["plan_item_routine_id"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
)
data class PlanItemEntity(
    @ColumnInfo(name = "plan_item_plan_id") val planId: Long,
    @ColumnInfo(name = "plan_item_routine_id") val routineId: Long,
    @ColumnInfo(name = "plan_item_order_index") val orderIndex: Int,
)
