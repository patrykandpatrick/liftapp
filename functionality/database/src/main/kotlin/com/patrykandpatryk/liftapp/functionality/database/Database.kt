package com.patrykandpatryk.liftapp.functionality.database

import androidx.room.BuiltInTypeConverters
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.patrykandpatryk.liftapp.functionality.database.bodymeasurement.BodyMeasurementDao
import com.patrykandpatryk.liftapp.functionality.database.bodymeasurement.BodyMeasurementEntity
import com.patrykandpatryk.liftapp.functionality.database.bodymeasurement.BodyMeasurementEntryEntity
import com.patrykandpatryk.liftapp.functionality.database.bodymeasurement.BodyMeasurementWithLatestEntryViewResult
import com.patrykandpatryk.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.LocalDateTimeConverters
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseDao
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.goal.GoalDao
import com.patrykandpatryk.liftapp.functionality.database.goal.GoalEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.ExerciseWithRoutineEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineEntity
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineWithExerciseNamesView

@androidx.room.Database(
    entities = [
        BodyMeasurementEntity::class,
        BodyMeasurementEntryEntity::class,
        ExerciseEntity::class,
        RoutineEntity::class,
        ExerciseWithRoutineEntity::class,
        GoalEntity::class,
    ],
    views = [
        BodyMeasurementWithLatestEntryViewResult::class,
        RoutineWithExerciseNamesView::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    value = [
        JsonConverters::class,
        LocalDateTimeConverters::class,
    ],
    builtInTypeConverters = BuiltInTypeConverters(
        enums = BuiltInTypeConverters.State.ENABLED,
    ),
)
abstract class Database : RoomDatabase() {

    abstract val bodyMeasurementDao: BodyMeasurementDao

    abstract val exerciseDao: ExerciseDao

    abstract val routineDao: RoutineDao

    abstract val goalDao: GoalDao
}
