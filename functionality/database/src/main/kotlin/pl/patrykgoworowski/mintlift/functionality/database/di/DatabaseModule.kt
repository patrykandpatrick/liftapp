package pl.patrykgoworowski.mintlift.functionality.database.di

import android.app.Application
import androidx.room.Room
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.measurement.Measurement
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementEntry
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementRepository
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementWithLatestEntry
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton
import pl.patrykgoworowski.mintlift.functionality.database.Database
import pl.patrykgoworowski.mintlift.functionality.database.converter.DateConverters
import pl.patrykgoworowski.mintlift.functionality.database.converter.JsonConverters
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementDao
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementEntryEntityToDomainMapper
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementRepositoryImpl
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementWithLatestEntryToDomainMeasurementMapper
import pl.patrykgoworowski.mintlift.functionality.database.measurement.MeasurementWithLatestEntryView

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

    @Binds
    fun bindMeasurementWithLatestEntryToDomainMapper(
        mapper: MeasurementWithLatestEntryToDomainMeasurementMapper,
    ): Mapper<MeasurementWithLatestEntryView, MeasurementWithLatestEntry>

    @Binds
    fun bindMeasurementWithLatestEntryViewToDomainMapper(
        mapper: MeasurementEntryEntityToDomainMapper,
    ): Mapper<MeasurementWithLatestEntryView, MeasurementEntry?>

    @Binds
    fun bindMeasurementRepository(repository: MeasurementRepositoryImpl): MeasurementRepository

    companion object {

        @Provides
        @Singleton
        @DatabaseDateFormat
        fun provideDatabaseDateFormat(): SimpleDateFormat =
            SimpleDateFormat(Constants.Database.DATE_PATTERN, Locale.ENGLISH)

        @Provides
        @Singleton
        fun provideDatabase(
            application: Application,
            jsonConverters: JsonConverters,
            dateConverters: DateConverters,
        ): Database =
            Room
                .databaseBuilder(application, Database::class.java, Constants.Database.Name)
                .addTypeConverter(jsonConverters)
                .addTypeConverter(dateConverters)
                .build()

        @Provides
        fun provideMeasurementDao(database: Database): MeasurementDao =
            database.measurementDao
    }
}
