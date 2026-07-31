package com.patrykandpatrick.liftapp.functionality.database

import androidx.room.BuiltInTypeConverters
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.patrykandpatrick.liftapp.functionality.database.bodymeasurement.BodyMeasurementDao
import com.patrykandpatrick.liftapp.functionality.database.bodymeasurement.BodyMeasurementEntity
import com.patrykandpatrick.liftapp.functionality.database.bodymeasurement.BodyMeasurementEntryEntity
import com.patrykandpatrick.liftapp.functionality.database.bodymeasurement.BodyMeasurementWithLatestEntryViewResult
import com.patrykandpatrick.liftapp.functionality.database.converter.JsonConverters
import com.patrykandpatrick.liftapp.functionality.database.converter.LocalDateTimeConverters
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseDao
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.goal.GoalDao
import com.patrykandpatrick.liftapp.functionality.database.goal.GoalEntity
import com.patrykandpatrick.liftapp.functionality.database.plan.PlanDao
import com.patrykandpatrick.liftapp.functionality.database.plan.PlanEntity
import com.patrykandpatrick.liftapp.functionality.database.plan.PlanItemEntity
import com.patrykandpatrick.liftapp.functionality.database.plan.PlanItemSchedule
import com.patrykandpatrick.liftapp.functionality.database.routine.ExerciseWithRoutineEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineDao
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineWithExerciseNamesView
import com.patrykandpatrick.liftapp.functionality.database.workout.ExerciseSetEntity
import com.patrykandpatrick.liftapp.functionality.database.workout.WorkoutDao
import com.patrykandpatrick.liftapp.functionality.database.workout.WorkoutEntity
import com.patrykandpatrick.liftapp.functionality.database.workout.WorkoutGoalEntity
import com.patrykandpatrick.liftapp.functionality.database.workout.WorkoutWithExerciseEntity

@androidx.room.Database(
    entities =
        [
            BodyMeasurementEntity::class,
            BodyMeasurementEntryEntity::class,
            ExerciseEntity::class,
            RoutineEntity::class,
            ExerciseWithRoutineEntity::class,
            GoalEntity::class,
            WorkoutEntity::class,
            WorkoutWithExerciseEntity::class,
            WorkoutGoalEntity::class,
            ExerciseSetEntity::class,
            PlanEntity::class,
            PlanItemEntity::class,
            PlanItemSchedule::class,
        ],
    views = [BodyMeasurementWithLatestEntryViewResult::class, RoutineWithExerciseNamesView::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    value = [JsonConverters::class, LocalDateTimeConverters::class],
    builtInTypeConverters = BuiltInTypeConverters(enums = BuiltInTypeConverters.State.ENABLED),
)
abstract class Database : RoomDatabase() {

    abstract val bodyMeasurementDao: BodyMeasurementDao

    abstract val exerciseDao: ExerciseDao

    abstract val routineDao: RoutineDao

    abstract val goalDao: GoalDao

    abstract val workoutDao: WorkoutDao

    abstract val planDao: PlanDao
}
