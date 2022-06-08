package pl.patrykgoworowski.mintlift.functionality.database

import androidx.room.BuiltInTypeConverters
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.patrykgoworowski.mintlift.functionality.database.converter.DateConverters
import pl.patrykgoworowski.mintlift.functionality.database.converter.JsonConverters
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementEntity
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementDao
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementEntryEntity
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementWithLatestEntryView

@androidx.room.Database(
    entities = [
        MeasurementEntity::class,
        MeasurementEntryEntity::class,
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
}
