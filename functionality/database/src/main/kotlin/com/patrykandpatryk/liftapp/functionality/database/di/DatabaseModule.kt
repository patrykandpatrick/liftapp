package com.patrykandpatryk.liftapp.functionality.database.di

import android.app.Application
import androidx.room.Room
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.model.StringResource
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.serialization.PolymorphicEnumSerializer
import com.patrykandpatryk.liftapp.functionality.database.Database
import com.patrykandpatryk.liftapp.functionality.database.DatabaseCallback
import com.patrykandpatryk.liftapp.functionality.database.body.BodyDao
import com.patrykandpatryk.liftapp.functionality.database.body.BodyRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.converter.CalendarConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseDao
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.string.BodyStringResource
import com.patrykandpatryk.liftapp.functionality.database.string.ExerciseStringResource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import kotlinx.serialization.KSerializer
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Singleton
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

    @Binds
    fun bindBodyRepository(repository: BodyRepositoryImpl): BodyRepository

    @Binds
    fun bindExerciseRepository(repository: ExerciseRepositoryImpl): ExerciseRepository

    @Binds
    fun bindRoutineRepository(repository: RoutineRepositoryImpl): RoutineRepository

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
        fun provideBodyDao(database: Database): BodyDao =
            database.bodyDao

        @Provides
        fun provideExerciseDao(database: Database): ExerciseDao =
            database.exerciseDao

        @Provides
        fun provideRoutineDao(database: Database): RoutineDao =
            database.routineDao

        @Provides
        @IntoSet
        fun provideExerciseStringResourceSerializer(): Pair<KClass<StringResource>, KSerializer<StringResource>> =
            (ExerciseStringResource::class to PolymorphicEnumSerializer(ExerciseStringResource.serializer()))
                as Pair<KClass<StringResource>, KSerializer<StringResource>>

        @Provides
        @IntoSet
        fun provideBodyStringResourceSerializer(): Pair<KClass<StringResource>, KSerializer<StringResource>> =
            (BodyStringResource::class to PolymorphicEnumSerializer(BodyStringResource.serializer()))
                as Pair<KClass<StringResource>, KSerializer<StringResource>>
    }
}
