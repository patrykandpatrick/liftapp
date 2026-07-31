package com.patrykandpatrick.liftapp.functionality.database.routine

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET

@Entity(tableName = "routine")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "routine_id") val id: Long = ID_NOT_SET,
    @ColumnInfo(name = "routine_name") val name: String,
    @ColumnInfo(name = "routine_order_index", defaultValue = "0") val orderIndex: Int = 0,
)
