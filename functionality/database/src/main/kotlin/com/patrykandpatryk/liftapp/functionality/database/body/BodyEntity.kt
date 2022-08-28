package com.patrykandpatryk.liftapp.functionality.database.body

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.body.BodyType
import com.patrykandpatryk.liftapp.domain.model.Name

@Entity(
    tableName = "body",
)
class BodyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: Name,
    val type: BodyType,
)
