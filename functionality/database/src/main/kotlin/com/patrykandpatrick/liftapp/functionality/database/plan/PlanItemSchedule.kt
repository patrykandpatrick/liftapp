package com.patrykandpatrick.liftapp.functionality.database.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity
import java.time.LocalDate

@Entity(
    tableName = "plan_item_schedule",
    foreignKeys =
        [
            ForeignKey(
                entity = RoutineEntity::class,
                parentColumns = ["routine_id"],
                childColumns = ["plan_item_routine_id"],
                onDelete = ForeignKey.CASCADE,
            ),
            ForeignKey(
                entity = PlanEntity::class,
                parentColumns = ["plan_id"],
                childColumns = ["plan_item_schedule_plan_id"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
    indices =
        [
            Index(
                name = "index_plan_item_schedule_plan_id",
                value = ["plan_item_schedule_plan_id"],
            ),
            Index(
                name = "index_plan_item_routine_id",
                value = ["plan_item_routine_id"],
            ),
        ],
)
data class PlanItemSchedule(
    @ColumnInfo(name = "plan_item_schedule_id")
    @PrimaryKey(autoGenerate = true)
    val id: Long = ID_NOT_SET,
    @ColumnInfo(name = "plan_item_schedule_plan_id") val planID: Long,
    @ColumnInfo(name = "plan_item_routine_id") val routineID: Long?,
    @ColumnInfo(name = "plan_item_schedule_date") val date: LocalDate,
)
