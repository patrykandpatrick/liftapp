package com.patrykandpatryk.liftapp.functionality.database.measurement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementValues
import java.util.Calendar

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
)
class MeasurementEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entry_id", index = true)
    val id: Long = 0,
    @ColumnInfo(name = "parent_id", index = true)
    val parentId: Long,
    val values: MeasurementValues,
    @ColumnInfo(index = true)
    val timestamp: Calendar,
)
