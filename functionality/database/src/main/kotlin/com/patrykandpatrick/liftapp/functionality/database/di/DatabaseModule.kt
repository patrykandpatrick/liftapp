package com.patrykandpatrick.liftapp.functionality.database.di

import android.app.Application
import androidx.room.Room
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRepository
import com.patrykandpatrick.liftapp.domain.model.StringResource
import com.patrykandpatrick.liftapp.domain.routine.RoutineRepository
import com.patrykandpatrick.liftapp.domain.serialization.PolymorphicEnumSerializer
import com.patrykandpatrick.liftapp.functionality.database.Database
import com.patrykandpatrick.liftapp.functionality.database.DatabaseCallback
import com.patrykandpatrick.liftapp.functionality.database.R
import com.patrykandpatrick.liftapp.functionality.database.bodymeasurement.BodyMeasurementDao
import com.patrykandpatrick.liftapp.functionality.database.bodymeasurement.BodyMeasurementRepositoryImpl
import com.patrykandpatrick.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseDao
import com.patrykandpatrick.liftapp.functionality.database.exercise.RoomExerciseRepository
import com.patrykandpatrick.liftapp.functionality.database.goal.GoalDao
import com.patrykandpatrick.liftapp.functionality.database.migration.Migration11To12
import com.patrykandpatrick.liftapp.functionality.database.migration.Migration1To12
import com.patrykandpatrick.liftapp.functionality.database.migration.PublishedMigrations
import com.patrykandpatrick.liftapp.functionality.database.plan.PlanDao
import com.patrykandpatrick.liftapp.functionality.database.routine.RoomRoutineRepository
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatrick.liftapp.functionality.database.string.BodyMeasurementStringResource
import com.patrykandpatrick.liftapp.functionality.database.string.ExerciseStringResource
import com.patrykandpatrick.liftapp.functionality.database.workout.WorkoutDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Singleton
import kotlin.reflect.KClass
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

    @Binds
    fun bindBodyMeasurementRepository(
        repository: BodyMeasurementRepositoryImpl
    ): BodyMeasurementRepository

    @Binds fun bindExerciseRepository(repository: RoomExerciseRepository): ExerciseRepository

    @Binds fun bindRoutineRepository(repository: RoomRoutineRepository): RoutineRepository

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
            databaseCallback: DatabaseCallback,
            json: Json,
        ): Database =
            Room.databaseBuilder(application, Database::class.java, Constants.Database.Name)
                .addCallback(databaseCallback)
                .addTypeConverter(jsonConverters)
                .addMigrations(
                    PublishedMigrations.MIGRATION_6_7,
                    PublishedMigrations.MIGRATION_7_8,
                    PublishedMigrations.MIGRATION_6_8,
                    PublishedMigrations.MIGRATION_8_9,
                    PublishedMigrations.MIGRATION_9_10,
                    PublishedMigrations.MIGRATION_10_11,
                    Migration11To12(json, legacyPlan = { readLegacyPlan(application) }),
                    Migration1To12,
                )
                .build()

        /**
         * The published app kept the training plan in its default `SharedPreferences` rather than
         * in the database. Read lazily: the migration runs off the main thread, the module runs on
         * it.
         */
        private fun readLegacyPlan(application: Application): Migration11To12.LegacyPlan? {
            val ids =
                application
                    .getSharedPreferences(
                        Constants.LegacyApp.preferencesFileName(application.packageName),
                        Application.MODE_PRIVATE,
                    )
                    .getString(Constants.LegacyApp.PLAN_IDS_KEY, null)
                    ?.let { value -> Regex("-?\\d+").findAll(value).map { it.value.toLong() } }
                    ?.toList()
            if (ids.isNullOrEmpty()) return null
            return Migration11To12.LegacyPlan(
                name = application.getString(R.string.plan_migrated_from_published_app),
                legacyIDs = ids,
            )
        }

        @Provides
        fun provideBodyMeasurementDao(database: Database): BodyMeasurementDao =
            database.bodyMeasurementDao

        @Provides fun provideExerciseDao(database: Database): ExerciseDao = database.exerciseDao

        @Provides fun provideRoutineDao(database: Database): RoutineDao = database.routineDao

        @Provides fun provideGoalDao(database: Database): GoalDao = database.goalDao

        @Provides fun provideWorkoutDao(database: Database): WorkoutDao = database.workoutDao

        @Provides fun providePlanDao(database: Database): PlanDao = database.planDao

        @Provides fun provideInvalidationTracker(database: Database) = database.invalidationTracker

        @Provides
        @IntoSet
        fun provideExerciseStringResourceSerializer():
            Pair<KClass<StringResource>, KSerializer<StringResource>> =
            (ExerciseStringResource::class to
                PolymorphicEnumSerializer(ExerciseStringResource.serializer()))
                as Pair<KClass<StringResource>, KSerializer<StringResource>>

        @Provides
        @IntoSet
        fun provideBodyMeasurementStringResourceSerializer():
            Pair<KClass<StringResource>, KSerializer<StringResource>> =
            (BodyMeasurementStringResource::class to
                PolymorphicEnumSerializer(BodyMeasurementStringResource.serializer()))
                as Pair<KClass<StringResource>, KSerializer<StringResource>>
    }
}
