package com.patrykandpatryk.liftapp.functionality.database.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plan")
data class PlanEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "plan_id") val id: Long,
    @ColumnInfo(name = "plan_name") val name: String,
    @ColumnInfo(name = "plan_description") val description: String,
    @ColumnInfo(name = "plan_item_count") val itemCount: Int,
)
