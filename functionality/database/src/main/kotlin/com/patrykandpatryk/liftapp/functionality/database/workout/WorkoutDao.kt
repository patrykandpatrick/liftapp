package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.functionality.database.converter.LocalDateTimeConverters
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseEntity
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout WHERE workout_id = :workoutID")
    fun getWorkout(workoutID: Long): Flow<WorkoutEntity?>

    @Query("SELECT routine_name FROM routine WHERE routine_id = :routineID")
    fun getRoutineName(routineID: Long): Flow<String?>

    @Query(
        "SELECT exercise_id FROM exercise_with_routine WHERE routine_id = :routineID ORDER BY order_index"
    )
    fun getExerciseIDs(routineID: Long): Flow<List<Long>>

    @Query(
        "SELECT value FROM body_measurement_entries WHERE body_measurement_id = 1 ORDER BY time DESC LIMIT 1"
    )
    suspend fun getLatestBodyWeight(): BodyMeasurementValue?

    @Transaction
    @Query(
        value =
            "SELECT exercise.*, workout_goal.*, " +
                "current_exercise_set.exercise_set_id as current_exercise_set_id, " +
                "current_exercise_set.exercise_set_workout_id as current_exercise_set_workout_id, " +
                "current_exercise_set.exercise_set_exercise_id as current_exercise_set_exercise_id, " +
                "current_exercise_set.exercise_set_weight as current_exercise_set_weight, " +
                "current_exercise_set.exercise_set_weight_unit as current_exercise_set_weight_unit, " +
                "current_exercise_set.exercise_set_reps as current_exercise_set_reps, " +
                "current_exercise_set.exercise_set_time as current_exercise_set_time, " +
                "current_exercise_set.exercise_set_distance as current_exercise_set_distance, " +
                "current_exercise_set.exercise_set_distance_unit as current_exercise_set_distance_unit, " +
                "current_exercise_set.exercise_set_kcal as current_exercise_set_kcal, " +
                "current_exercise_set.workout_exercise_set_index as current_workout_exercise_set_index, " +
                "last_exercise_set.exercise_set_id as last_exercise_set_id, " +
                "last_exercise_set.exercise_set_workout_id as last_exercise_set_workout_id, " +
                "last_exercise_set.exercise_set_exercise_id as last_exercise_set_exercise_id, " +
                "last_exercise_set.exercise_set_weight as last_exercise_set_weight, " +
                "last_exercise_set.exercise_set_weight_unit as last_exercise_set_weight_unit, " +
                "last_exercise_set.exercise_set_reps as last_exercise_set_reps, " +
                "last_exercise_set.exercise_set_time as last_exercise_set_time, " +
                "last_exercise_set.exercise_set_distance as last_exercise_set_distance, " +
                "last_exercise_set.exercise_set_distance_unit as last_exercise_set_distance_unit, " +
                "last_exercise_set.exercise_set_kcal as last_exercise_set_kcal, " +
                "last_exercise_set.workout_exercise_set_index as last_workout_exercise_set_index " +
                "FROM workout_with_exercise AS wwe " +
                "LEFT JOIN exercise ON wwe.exercise_id = exercise.exercise_id " +
                "LEFT JOIN workout_goal " +
                "ON wwe.exercise_id = workout_goal_exercise_id AND workout_goal_workout_id = :workoutID " +
                "LEFT JOIN exercise_set AS current_exercise_set " +
                "ON current_exercise_set.exercise_set_workout_id = :workoutID " +
                "AND current_exercise_set.exercise_set_exercise_id = wwe.exercise_id " +
                "LEFT JOIN exercise_set AS last_exercise_set " +
                "ON last_exercise_set.exercise_set_workout_id = (SELECT workout_id FROM workout " +
                "WHERE workout_routine_id = :routineID AND workout_start_date < " +
                "(SELECT workout_start_date FROM workout WHERE workout_id = :workoutID) " +
                "ORDER BY workout_start_date DESC LIMIT 1) " +
                "AND last_exercise_set.exercise_set_exercise_id = wwe.exercise_id " +
                "WHERE wwe.workout_id = :workoutID ORDER BY order_index, current_exercise_set.workout_exercise_set_index"
    )
    fun getWorkoutExercises(workoutID: Long, routineID: Long): Flow<List<WorkoutExerciseDto>>

    @RawQuery(
        observedEntities =
            [
                WorkoutEntity::class,
                WorkoutWithExerciseEntity::class,
                ExerciseEntity::class,
                WorkoutGoalEntity::class,
                ExerciseSetEntity::class,
            ]
    )
    fun getWorkouts(query: RoomRawQuery): Flow<List<WorkoutWithWorkoutExerciseDto>>

    @Query(
        value =
            "INSERT INTO workout_goal (" +
                "workout_goal_workout_id, workout_goal_exercise_id, workout_goal_min_reps, workout_goal_max_reps, " +
                "workout_goal_sets, workout_goal_rest_time, workout_goal_duration_millis, workout_goal_distance, " +
                "workout_goal_distance_unit, workout_goal_calories" +
                ") SELECT :workoutID, goal_exercise_id, goal_min_reps, goal_max_reps, goal_sets, goal_rest_time, " +
                "goal_duration_millis, goal_distance, goal_distance_unit, goal_calories " +
                "FROM goal WHERE goal_routine_id = :routineID"
    )
    suspend fun copyRoutineGoalsToWorkoutGoals(routineID: Long, workoutID: Long)

    @Upsert suspend fun upsertWorkoutGoal(goal: WorkoutGoalEntity)

    @Query(
        value =
            "INSERT OR REPLACE INTO exercise_set (" +
                "exercise_set_id, exercise_set_workout_id, exercise_set_exercise_id, exercise_set_weight, " +
                "exercise_set_weight_unit, exercise_set_reps, exercise_set_time, exercise_set_distance," +
                "exercise_set_distance_unit, exercise_set_kcal, workout_exercise_set_index) " +
                "SELECT (SELECT COALESCE((SELECT id FROM exercise_set INNER JOIN " +
                "(SELECT e.exercise_set_id as id, e.exercise_set_workout_id as workoutID, " +
                "e.exercise_set_exercise_id as exerciseID FROM exercise_set as e " +
                "WHERE exercise_set_workout_id = :workoutID AND exercise_set_exercise_id = :exerciseID AND " +
                "workout_exercise_set_index = :setIndex) ON exercise_set_id = id " +
                "WHERE exercise_set_workout_id = :workoutID and exercise_set_exercise_id = :exerciseID" +
                "), NULL)), :workoutID, :exerciseID, :weight, :weightUnit, :reps, :timeMillis, :distance, " +
                ":distanceUnit, :kcal, :setIndex"
    )
    suspend fun upsertExerciseSet(
        workoutID: Long,
        exerciseID: Long,
        weight: Double? = 0.0,
        weightUnit: MassUnit? = null,
        reps: Int? = 0,
        timeMillis: Long? = 0L,
        distance: Double? = 0.0,
        distanceUnit: LongDistanceUnit? = null,
        kcal: Double? = null,
        setIndex: Int,
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkoutWithExercises(workoutWithExercises: List<WorkoutWithExerciseEntity>)

    @RawQuery suspend fun query(query: RoomRawQuery): List<Long>

    companion object {
        fun getWorkoutsQuery(hasEndDate: Boolean): RoomRawQuery {
            val endDate = if (hasEndDate) "NOT NULL" else "NULL"
            val query =
                "SELECT w.*, exercise.*, workout_goal.*, " +
                    "exercise_set.exercise_set_id as current_exercise_set_id, " +
                    "exercise_set.exercise_set_workout_id as current_exercise_set_workout_id, " +
                    "exercise_set.exercise_set_exercise_id as current_exercise_set_exercise_id, " +
                    "exercise_set.exercise_set_weight as current_exercise_set_weight, " +
                    "exercise_set.exercise_set_weight_unit as current_exercise_set_weight_unit, " +
                    "exercise_set.exercise_set_reps as current_exercise_set_reps, " +
                    "exercise_set.exercise_set_time as current_exercise_set_time, " +
                    "exercise_set.exercise_set_distance as current_exercise_set_distance, " +
                    "exercise_set.exercise_set_distance_unit as current_exercise_set_distance_unit, " +
                    "exercise_set.exercise_set_kcal as current_exercise_set_kcal, " +
                    "exercise_set.workout_exercise_set_index as current_workout_exercise_set_index " +
                    "FROM workout AS w " +
                    "LEFT JOIN workout_with_exercise AS wwe ON w.workout_id = wwe.workout_id " +
                    "LEFT JOIN exercise ON exercise.exercise_id = wwe.exercise_id " +
                    "LEFT JOIN workout_goal ON wwe.exercise_id = workout_goal_exercise_id AND workout_goal_workout_id = w.workout_id " +
                    "LEFT JOIN exercise_set ON wwe.exercise_id = exercise_set_exercise_id AND exercise_set_workout_id = w.workout_id " +
                    "WHERE w.workout_end_date IS %s ORDER BY w.workout_start_date DESC, order_index, workout_exercise_set_index"
                        .format(endDate)
            return RoomRawQuery(query)
        }

        fun getWorkoutsQuery(localDate: LocalDate): RoomRawQuery {
            val query =
                "SELECT w.*, exercise.*, workout_goal.*, exercise_set.* FROM workout AS w " +
                    "LEFT JOIN workout_with_exercise AS wwe ON w.workout_id = wwe.workout_id " +
                    "LEFT JOIN exercise ON exercise.exercise_id = wwe.exercise_id " +
                    "LEFT JOIN workout_goal ON wwe.exercise_id = workout_goal_exercise_id AND workout_goal_workout_id = w.workout_id " +
                    "LEFT JOIN exercise_set ON wwe.exercise_id = exercise_set_exercise_id AND exercise_set_workout_id = w.workout_id " +
                    "WHERE w.workout_start_date LIKE ? ORDER BY w.workout_start_date DESC, order_index, workout_exercise_set_index"
            return RoomRawQuery(query) {
                it.bindText(1, "${LocalDateTimeConverters.toString(localDate)}T%")
            }
        }
    }
}
