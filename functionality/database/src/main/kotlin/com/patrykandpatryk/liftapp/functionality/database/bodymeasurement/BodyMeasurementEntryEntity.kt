package com.patrykandpatryk.liftapp.functionality.database.bodymeasurement

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import java.util.*

@Entity(
    tableName = "body_measurement_entries",
    foreignKeys = [
        ForeignKey(
            entity = BodyMeasurementEntity::class,
            parentColumns = ["id"],
            childColumns = ["body_measurement_id"],
            onDelete = CASCADE,
        ),
    ],
)
class BodyMeasurementEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(index = true)
    val id: Long = 0,
    @ColumnInfo(name = "body_measurement_id", index = true)
    val bodyMeasurementID: Long,
    val value: BodyMeasurementValue,
    @ColumnInfo(index = true)
    val timestamp: Calendar,
)
