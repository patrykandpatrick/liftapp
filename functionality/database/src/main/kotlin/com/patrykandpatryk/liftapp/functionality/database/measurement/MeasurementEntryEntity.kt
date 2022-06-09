package com.patrykandpatryk.liftapp.functionality.database.measurement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementValues
import java.util.Date

@Entity(
    tableName = "measurement_entry",
    foreignKeys = [
        ForeignKey(
            entity = MeasurementEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = CASCADE,
        ),
    ],
    indices = [
        Index(
            "entry_id", "parent_id", "timestamp", "timestamp",
            orders = [Index.Order.ASC, Index.Order.ASC, Index.Order.DESC, Index.Order.ASC],
        ),
    ],
)
class MeasurementEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entry_id")
    val id: Long = 0,
    @ColumnInfo(name = "parent_id")
    val parentId: Long,
    val values: MeasurementValues,
    val timestamp: Date,
)
