package com.patrykandpatryk.liftapp.functionality.database.workout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout WHERE workout_id = :workoutID")
    fun getWorkout(workoutID: Long): Flow<WorkoutEntity?>

    @Query("SELECT routine_name FROM routine WHERE routine_id = :routineID")
    fun getRoutineName(routineID: Long): Flow<String?>

    @Query("SELECT exercise_id FROM exercise_with_routine WHERE routine_id = :routineID ORDER BY order_index")
    fun getExerciseIDs(routineID: Long): Flow<List<Long>>

    @Transaction
    @Query(
        value = "SELECT exercise.*, workout_goal.* FROM workout_with_exercise as wwe " +
                "LEFT JOIN exercise on wwe.exercise_id = exercise.exercise_id " +
                "LEFT JOIN workout_goal " +
                "ON wwe.exercise_id = workout_goal_exercise_id AND workout_goal_workout_id = :workoutID " +
                "WHERE wwe.workout_id = :workoutID ORDER BY order_index",
    )
    fun getWorkoutExercises(workoutID: Long): Flow<List<WorkoutExerciseDto>>

    @Query(
        value = "INSERT INTO workout_goal (" +
                "workout_goal_workout_id, workout_goal_exercise_id, workout_goal_min_reps, workout_goal_max_reps, " +
                "workout_goal_sets, workout_goal_break_duration" +
                ") SELECT :workoutID, goal_exercise_id, goal_min_reps, goal_max_reps, goal_sets, goal_break_duration " +
                "FROM goal WHERE goal_routine_id = :routineID"
    )
    suspend fun copyRoutineGoalsToWorkoutGoals(routineID: Long, workoutID: Long)

    @Query(
        value = "INSERT OR REPLACE INTO workout_goal (" +
                "workout_goal_id, workout_goal_workout_id, workout_goal_exercise_id, workout_goal_min_reps, " +
                "workout_goal_max_reps, workout_goal_sets, workout_goal_break_duration" +
                ") SELECT (SELECT COALESCE((SELECT id FROM workout_goal LEFT JOIN " +
                "(SELECT g.workout_goal_id as id, g.workout_goal_workout_id as workoutID, " +
                "g.workout_goal_exercise_id as exerciseID FROM workout_goal as g " +
                "WHERE workout_goal_workout_id = :workoutID AND workout_goal_exercise_id = :exerciseID) " +
                "on workout_goal_id = id " +
                "WHERE workout_goal_workout_id = :workoutID AND workout_goal_exercise_id = :exerciseID" +
                "), NULL)), :workoutID, :exerciseID, :minReps, :maxReps, :sets, :breakDurationMillis"
    )
    suspend fun upsertWorkoutGoal(
        workoutID: Long,
        exerciseID: Long,
        minReps: Int,
        maxReps: Int,
        sets: Int,
        breakDurationMillis: Long,
    )

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkoutWithExercises(workoutWithExercises: List<WorkoutWithExerciseEntity>)
}
