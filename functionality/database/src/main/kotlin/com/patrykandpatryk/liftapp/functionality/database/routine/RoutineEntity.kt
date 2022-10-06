package com.patrykandpatryk.liftapp.functionality.database.routine

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET

@Entity(tableName = "routine")
class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = ID_NOT_SET,
    val name: String,
) {

    override fun toString(): String =
        "RoutineEntity(id=$id, name='$name')"
}
