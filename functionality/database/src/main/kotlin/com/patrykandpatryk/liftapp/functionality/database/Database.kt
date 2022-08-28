package com.patrykandpatryk.liftapp.functionality.database

import androidx.room.BuiltInTypeConverters
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.CalendarConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseDao
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.body.BodyEntity
import com.patrykandpatryk.liftapp.functionality.database.body.BodyDao
import com.patrykandpatryk.liftapp.functionality.database.body.BodyEntryEntity
import com.patrykandpatryk.liftapp.functionality.database.body.BodyWithLatestEntryView

@androidx.room.Database(
    entities = [
        BodyEntity::class,
        BodyEntryEntity::class,
        ExerciseEntity::class,
    ],
    views = [
        BodyWithLatestEntryView::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    value = [
        JsonConverters::class,
        CalendarConverters::class,
    ],
    builtInTypeConverters = BuiltInTypeConverters(
        enums = BuiltInTypeConverters.State.ENABLED,
    ),
)
abstract class Database : RoomDatabase() {

    abstract val bodyDao: BodyDao

    abstract val exerciseDao: ExerciseDao
}
