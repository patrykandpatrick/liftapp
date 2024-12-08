package com.patrykandpatryk.liftapp.functionality.database.di

import android.app.Application
import androidx.room.Room
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatryk.liftapp.domain.goal.GoalRepository
import com.patrykandpatryk.liftapp.domain.model.StringResource
import com.patrykandpatryk.liftapp.domain.routine.RoutineRepository
import com.patrykandpatryk.liftapp.domain.serialization.PolymorphicEnumSerializer
import com.patrykandpatryk.liftapp.functionality.database.Database
import com.patrykandpatryk.liftapp.functionality.database.DatabaseCallback
import com.patrykandpatryk.liftapp.functionality.database.bodymeasurement.BodyMeasurementDao
import com.patrykandpatryk.liftapp.functionality.database.bodymeasurement.BodyMeasurementRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatryk.liftapp.functionality.database.converter.LocalDateTimeConverters
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseDao
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.goal.GoalDao
import com.patrykandpatryk.liftapp.functionality.database.goal.RoomGoalRepository
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineRepositoryImpl
import com.patrykandpatryk.liftapp.functionality.database.string.BodyMeasurementStringResource
import com.patrykandpatryk.liftapp.functionality.database.string.ExerciseStringResource
import com.patrykandpatryk.liftapp.functionality.database.workout.WorkoutDao
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
    fun bindBodyMeasurementRepository(repository: BodyMeasurementRepositoryImpl): BodyMeasurementRepository

    @Binds
    fun bindExerciseRepository(repository: ExerciseRepositoryImpl): ExerciseRepository

    @Binds
    fun bindRoutineRepository(repository: RoutineRepositoryImpl): RoutineRepository

    @Binds
    fun bindGoalRepository(repository: RoomGoalRepository): GoalRepository

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
            localDateTimeConverters: LocalDateTimeConverters,
            databaseCallback: DatabaseCallback,
        ): Database =
            Room
                .databaseBuilder(application, Database::class.java, Constants.Database.Name)
                .addCallback(databaseCallback)
                .addTypeConverter(jsonConverters)
                .addTypeConverter(localDateTimeConverters)
                .build()

        @Provides
        fun provideBodyMeasurementDao(database: Database): BodyMeasurementDao =
            database.bodyMeasurementDao

        @Provides
        fun provideExerciseDao(database: Database): ExerciseDao =
            database.exerciseDao

        @Provides
        fun provideRoutineDao(database: Database): RoutineDao =
            database.routineDao

        @Provides
        fun provideGoalDao(database: Database): GoalDao =
            database.goalDao

        @Provides
        fun provideWorkoutDao(database: Database): WorkoutDao =
            database.workoutDao

        @Provides
        @IntoSet
        fun provideExerciseStringResourceSerializer(): Pair<KClass<StringResource>, KSerializer<StringResource>> =
            (ExerciseStringResource::class to PolymorphicEnumSerializer(ExerciseStringResource.serializer()))
                as Pair<KClass<StringResource>, KSerializer<StringResource>>

        @Provides
        @IntoSet
        fun provideBodyMeasurementStringResourceSerializer(): Pair<
            KClass<StringResource>,
            KSerializer<StringResource>,
            > = (
            BodyMeasurementStringResource::class to
                PolymorphicEnumSerializer(BodyMeasurementStringResource.serializer())
            ) as Pair<KClass<StringResource>, KSerializer<StringResource>>
    }
}
