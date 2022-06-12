package com.patrykandpatryk.liftapp.functionality.database

import androidx.room.BuiltInTypeConverters
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.DateConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseDao
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementEntity
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementDao
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementEntryEntity
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementWithLatestEntryView

@androidx.room.Database(
    entities = [
        MeasurementEntity::class,
        MeasurementEntryEntity::class,
        ExerciseEntity::class,
    ],
    views = [
        MeasurementWithLatestEntryView::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    value = [
        JsonConverters::class,
        DateConverters::class,
    ],
    builtInTypeConverters = BuiltInTypeConverters(
        enums = BuiltInTypeConverters.State.ENABLED,
    ),
)
abstract class Database : RoomDatabase() {

    abstract val measurementDao: MeasurementDao

    abstract val exerciseDao: ExerciseDao
}
