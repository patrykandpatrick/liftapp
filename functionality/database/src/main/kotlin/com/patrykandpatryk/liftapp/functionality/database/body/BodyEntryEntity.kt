package com.patrykandpatryk.liftapp.functionality.database.body

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.body.BodyValues
import java.util.Calendar

@Entity(
    tableName = "body_entry",
    foreignKeys = [
        ForeignKey(
            entity = BodyEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = CASCADE,
        ),
    ],
)
class BodyEntryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entry_id", index = true)
    val id: Long = 0,
    @ColumnInfo(name = "parent_id", index = true)
    val parentId: Long,
    val values: BodyValues,
    @ColumnInfo(index = true)
    val timestamp: Calendar,
)
