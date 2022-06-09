package com.patrykandpatryk.liftapp.functionality.database.di

import android.app.Application
import androidx.room.Room
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
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
import com.patrykandpatryk.liftapp.functionality.database.Database
import com.patrykandpatryk.liftapp.functionality.database.converter.DateConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementDao
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementEntryEntityToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementWithLatestEntryToDomainMeasurementMapper
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementWithLatestEntryView

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
