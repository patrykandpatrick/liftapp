package com.patrykandpatryk.liftapp.functionality.database.bodymeasurement

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatryk.liftapp.domain.model.Name

@Entity(
    tableName = "body_measurements",
)
class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: Name,
    val type: BodyMeasurementType,
)
