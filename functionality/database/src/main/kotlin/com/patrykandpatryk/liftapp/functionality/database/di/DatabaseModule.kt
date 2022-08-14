package com.patrykandpatryk.liftapp.functionality.database.di

import android.app.Application
import androidx.room.Room
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.exercise.Exercise
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.measurement.Measurement
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementEntry
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementRepository
import com.patrykandpatryk.liftapp.domain.measurement.MeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.functionality.database.Database
import com.patrykandpatryk.liftapp.functionality.database.DatabaseCallback
import com.patrykandpatryk.liftapp.functionality.database.converter.CalendarConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseDao
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntityToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseInsertToEntityMapper
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseUpdateToEntityMapper
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementDao
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementEntity
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementEntityToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementEntryEntityToDomainMapper
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementWithLatestEntryToDomainMeasurementMapper
import com.patrykandpatryk.liftapp.functionality.database.measurement.MeasurementWithLatestEntryView
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

    @Binds
    fun bindMeasurementEntityToDomainMapper(
        mapper: MeasurementEntityToDomainMapper,
    ): Mapper<MeasurementEntity, Measurement>

    @Binds
    fun bindMeasurementWithLatestEntryToDomainMapper(
        mapper: MeasurementWithLatestEntryToDomainMeasurementMapper,
    ): Mapper<MeasurementWithLatestEntryView, MeasurementWithLatestEntry>

    @Binds
    fun bindMeasurementWithLatestEntryViewToDomainMapper(
        mapper: MeasurementEntryEntityToDomainMapper,
    ): Mapper<MeasurementWithLatestEntryView, MeasurementEntry?>

    @Binds
    fun bindExerciseEntityToDomainMapper(
        mapper: ExerciseEntityToDomainMapper,
    ): Mapper<ExerciseEntity, Exercise>

    @Binds
    fun bindExerciseInsertToEntityMapper(
        mapper: ExerciseInsertToEntityMapper,
    ): Mapper<Exercise.Insert, ExerciseEntity>

    @Binds
    fun bindExerciseUpdateToEntityMapper(
        mapper: ExerciseUpdateToEntityMapper,
    ): Mapper<Exercise.Update, ExerciseEntity.Update>

    @Binds
    fun bindMeasurementRepository(repository: MeasurementRepositoryImpl): MeasurementRepository

    @Binds
    fun bindExerciseRepository(repository: ExerciseRepositoryImpl): ExerciseRepository

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
            calendarConverters: CalendarConverters,
            databaseCallback: DatabaseCallback,
        ): Database =
            Room
                .databaseBuilder(application, Database::class.java, Constants.Database.Name)
                .addCallback(databaseCallback)
                .addTypeConverter(jsonConverters)
                .addTypeConverter(calendarConverters)
                .build()

        @Provides
        fun provideMeasurementDao(database: Database): MeasurementDao =
            database.measurementDao

        @Provides
        fun provideExerciseDao(database: Database): ExerciseDao =
            database.exerciseDao
    }
}
