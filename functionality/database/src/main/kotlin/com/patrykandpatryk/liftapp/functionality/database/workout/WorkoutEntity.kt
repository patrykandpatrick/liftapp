package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.patrykandpatryk.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity
import com.patrykandpatryk.liftapp.functionality.database.workout.WorkoutEntity.Companion.TABLE
import java.time.LocalDateTime

@Entity(
    tableName = TABLE,
    foreignKeys =
        [
            ForeignKey(
                entity = RoutineEntity::class,
                parentColumns = ["routine_id"],
                childColumns = ["workout_routine_id"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = ID) val id: Long = ID_NOT_SET,
    @ColumnInfo(name = ROUTINE_ID, index = true) val routineID: Long,
    @ColumnInfo(name = NAME) val name: String,
    @ColumnInfo(name = START_DATE) val startDate: LocalDateTime,
    @ColumnInfo(name = END_DATE) val endDate: LocalDateTime?,
    @ColumnInfo(name = NOTES) val notes: String,
    @ColumnInfo(name = BODY_WEIGHT) val bodyWeight: BodyMeasurementValue?,
) {
    companion object {
        const val TABLE = "workout"
        const val ID = "workout_id"
        const val ROUTINE_ID = "workout_routine_id"
        const val NAME = "workout_name"
        const val START_DATE = "workout_start_date"
        const val END_DATE = "workout_end_date"
        const val NOTES = "workout_notes"
        const val BODY_WEIGHT = "workout_body_weight"
    }
}
