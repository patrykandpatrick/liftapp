package com.patrykandpatrick.liftapp.functionality.database.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity

/**
 * One day of a plan.
 *
 * A day is identified by where it falls in the plan, not by the routine it holds: a plan is free to
 * repeat a routine, and keying on the routine made the second day upsert over the first, quietly
 * turning it into a rest day.
 */
@Entity(
    tableName = "plan_item",
    primaryKeys = ["plan_item_plan_id", "plan_item_order_index"],
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
