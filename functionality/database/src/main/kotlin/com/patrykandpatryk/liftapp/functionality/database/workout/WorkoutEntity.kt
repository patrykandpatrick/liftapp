package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity
import java.time.LocalDateTime

@Entity(
    tableName = "workout",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["routine_id"],
            childColumns = ["workout_routine_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ]
)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "workout_id")
    val id: Long = ID_NOT_SET,
    @ColumnInfo(name = "workout_routine_id")
    val routineID: Long,
    @ColumnInfo(name = "workout_name")
    val name: String,
    @ColumnInfo(name = "workout_date")
    val date: LocalDateTime,
    @ColumnInfo(name = "workout_duration")
    val durationMillis: Long,
    @ColumnInfo(name = "workout_notes")
    val notes: String,
)
