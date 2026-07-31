package com.patrykandpatrick.liftapp.functionality.database.workout

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import androidx.room.Transaction
import androidx.room.Upsert
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.functionality.database.converter.LocalDateTimeConverters
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineItemExerciseDto
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout WHERE workout_id = :workoutID")
    fun getWorkout(workoutID: Long): Flow<WorkoutEntity?>

    @Query(
        "SELECT workout_id FROM workout WHERE workout_end_date IS NULL " +
            "ORDER BY workout_start_date DESC LIMIT 1"
    )
    suspend fun getActiveWorkoutID(): Long?

    @Query("SELECT routine_name FROM routine WHERE routine_id = :routineID")
    fun getRoutineName(routineID: Long): Flow<String?>

    @Query(
        "SELECT item.*, exercise.*, goal.*, " +
            "membership.routine_item_exercise_order_index AS routine_item_exercise_order, " +
            "superset.superset_sets AS superset_sets, " +
            "superset.superset_rest_time_millis AS superset_rest_time_millis " +
            "FROM routine_item AS item " +
            "LEFT JOIN superset ON superset.superset_routine_item_id = item.routine_item_id " +
            "LEFT JOIN exercise_with_routine_item AS membership " +
            "ON membership.routine_item_id = item.routine_item_id " +
            "LEFT JOIN exercise ON exercise.exercise_id = membership.exercise_id " +
            "LEFT JOIN goal ON goal.goal_exercise_id = exercise.exercise_id " +
            "AND goal.goal_routine_id = :routineID " +
            "WHERE item.routine_item_routine_id = :routineID " +
            "ORDER BY item.routine_item_order_index, membership.routine_item_exercise_order_index"
    )
    fun getRoutineItems(routineID: Long): Flow<List<RoutineItemExerciseDto>>

    @Query(
        "SELECT value FROM body_measurement_entries WHERE body_measurement_id = 1 ORDER BY time DESC LIMIT 1"
    )
    suspend fun getLatestBodyWeight(): BodyMeasurementValue?

    @Transaction
    @Query(
        value =
            "SELECT item.*, exercise.*, workout_goal.*, " +
                "membership.workout_item_exercise_order_index AS workout_item_exercise_order, " +
                "membership.workout_item_exercise_notes AS workout_item_exercise_notes, " +
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
                "current_exercise_set.exercise_set_notes as current_exercise_set_notes, " +
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
                "last_exercise_set.exercise_set_notes as last_exercise_set_notes, " +
                "last_exercise_set.workout_exercise_set_index as last_workout_exercise_set_index " +
                "FROM workout_item AS item " +
                "LEFT JOIN exercise_with_workout_item AS membership " +
                "ON membership.workout_item_id = item.workout_item_id " +
                "LEFT JOIN exercise ON membership.exercise_id = exercise.exercise_id " +
                "LEFT JOIN workout_goal " +
                "ON membership.exercise_id = workout_goal_exercise_id AND workout_goal_workout_id = :workoutID " +
                "LEFT JOIN exercise_set AS current_exercise_set " +
                "ON current_exercise_set.exercise_set_workout_id = :workoutID " +
                "AND current_exercise_set.exercise_set_exercise_id = membership.exercise_id " +
                "LEFT JOIN exercise_set AS last_exercise_set " +
                "ON last_exercise_set.exercise_set_workout_id = (SELECT workout_id FROM workout " +
                "WHERE workout_routine_id = :routineID AND workout_start_date < " +
                "(SELECT workout_start_date FROM workout WHERE workout_id = :workoutID) " +
                "ORDER BY workout_start_date DESC LIMIT 1) " +
                "AND last_exercise_set.exercise_set_exercise_id = membership.exercise_id " +
                "WHERE item.workout_item_workout_id = :workoutID " +
                "ORDER BY item.workout_item_order_index, membership.workout_item_exercise_order_index, " +
                "current_exercise_set.workout_exercise_set_index"
    )
    fun getWorkoutExercises(workoutID: Long, routineID: Long): Flow<List<WorkoutExerciseDto>>

    @RawQuery(
        observedEntities =
            [
                WorkoutEntity::class,
                WorkoutItemEntity::class,
                ExerciseWithWorkoutItemEntity::class,
                ExerciseEntity::class,
                WorkoutGoalEntity::class,
                ExerciseSetEntity::class,
            ]
    )
    fun getWorkouts(query: RoomRawQuery): Flow<List<WorkoutWithWorkoutExerciseDto>>

    @Transaction
    @RawQuery(
        observedEntities =
            [
                WorkoutEntity::class,
                WorkoutItemEntity::class,
                ExerciseWithWorkoutItemEntity::class,
                ExerciseEntity::class,
                WorkoutGoalEntity::class,
                ExerciseSetEntity::class,
            ]
    )
    suspend fun getWorkoutsOnce(query: RoomRawQuery): List<WorkoutWithWorkoutExerciseDto>

    @Query("DELETE FROM workout WHERE workout_id = :workoutID")
    suspend fun deleteWorkout(workoutID: Long)

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
                "exercise_set_distance_unit, exercise_set_kcal, exercise_set_notes, " +
                "workout_exercise_set_index) " +
                "SELECT (SELECT COALESCE((SELECT id FROM exercise_set INNER JOIN " +
                "(SELECT e.exercise_set_id as id, e.exercise_set_workout_id as workoutID, " +
                "e.exercise_set_exercise_id as exerciseID FROM exercise_set as e " +
                "WHERE exercise_set_workout_id = :workoutID AND exercise_set_exercise_id = :exerciseID AND " +
                "workout_exercise_set_index = :setIndex) ON exercise_set_id = id " +
                "WHERE exercise_set_workout_id = :workoutID and exercise_set_exercise_id = :exerciseID" +
                "), NULL)), :workoutID, :exerciseID, :weight, :weightUnit, :reps, :timeMillis, :distance, " +
                ":distanceUnit, :kcal, :notes, :setIndex"
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
        notes: String = "",
        setIndex: Int,
    )

    @Query(
        "UPDATE exercise_with_workout_item SET workout_item_exercise_notes = :notes " +
            "WHERE workout_item_id = :workoutItemID AND exercise_id = :exerciseID"
    )
    suspend fun updateExerciseNotes(workoutItemID: Long, exerciseID: Long, notes: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Transaction
    suspend fun getOrInsertActiveWorkoutWithItemsAndGoals(
        workout: WorkoutEntity,
        routineItems: List<RoutineItemExerciseDto>,
        routineID: Long,
    ): Long {
        getActiveWorkoutID()?.let {
            return it
        }

        val workoutID = insertWorkout(workout)
        insertWorkoutItems(workoutID, routineItems)
        copyRoutineGoalsToWorkoutGoals(routineID, workoutID)
        return workoutID
    }

    @Insert suspend fun insertWorkoutItem(item: WorkoutItemEntity): Long

    @Insert suspend fun insertExerciseWithWorkoutItems(items: List<ExerciseWithWorkoutItemEntity>)

    @Query(
        "SELECT COALESCE(MAX(workout_item_order_index), -1) FROM workout_item " +
            "WHERE workout_item_workout_id = :workoutID"
    )
    suspend fun getLastWorkoutItemOrder(workoutID: Long): Int

    @Query(
        "SELECT workout_item_id FROM workout_item WHERE workout_item_workout_id = :workoutID " +
            "ORDER BY workout_item_order_index"
    )
    suspend fun getWorkoutItemIDs(workoutID: Long): List<Long>

    @Query(
        "SELECT membership.exercise_id FROM exercise_with_workout_item AS membership " +
            "INNER JOIN workout_item AS item ON membership.workout_item_id = item.workout_item_id " +
            "WHERE item.workout_item_workout_id = :workoutID"
    )
    suspend fun getWorkoutExerciseIDs(workoutID: Long): List<Long>

    @Query(
        "SELECT membership.exercise_id FROM exercise_with_workout_item AS membership " +
            "INNER JOIN workout_item AS item ON membership.workout_item_id = item.workout_item_id " +
            "WHERE item.workout_item_workout_id = :workoutID " +
            "AND item.workout_item_id = :workoutItemID"
    )
    suspend fun getWorkoutItemExerciseIDs(workoutID: Long, workoutItemID: Long): List<Long>

    @Query(
        "DELETE FROM workout_item WHERE workout_item_workout_id = :workoutID " +
            "AND workout_item_id = :workoutItemID"
    )
    suspend fun deleteWorkoutItem(workoutID: Long, workoutItemID: Long)

    @Query(
        "DELETE FROM exercise_set WHERE exercise_set_workout_id = :workoutID " +
            "AND exercise_set_exercise_id IN (:exerciseIDs) " +
            "AND exercise_set_exercise_id NOT IN (" +
            "SELECT membership.exercise_id FROM exercise_with_workout_item AS membership " +
            "INNER JOIN workout_item AS item ON membership.workout_item_id = item.workout_item_id " +
            "WHERE item.workout_item_workout_id = :workoutID)"
    )
    suspend fun deleteRemovedExerciseSets(workoutID: Long, exerciseIDs: List<Long>)

    @Query(
        "DELETE FROM workout_goal WHERE workout_goal_workout_id = :workoutID " +
            "AND workout_goal_exercise_id IN (:exerciseIDs) " +
            "AND workout_goal_exercise_id NOT IN (" +
            "SELECT membership.exercise_id FROM exercise_with_workout_item AS membership " +
            "INNER JOIN workout_item AS item ON membership.workout_item_id = item.workout_item_id " +
            "WHERE item.workout_item_workout_id = :workoutID)"
    )
    suspend fun deleteRemovedWorkoutGoals(workoutID: Long, exerciseIDs: List<Long>)

    @Query(
        "UPDATE workout_item SET workout_item_order_index = -workout_item_order_index - 1 " +
            "WHERE workout_item_workout_id = :workoutID"
    )
    suspend fun moveWorkoutItemsToTemporaryOrder(workoutID: Long)

    @Query(
        "UPDATE workout_item SET workout_item_order_index = :orderIndex " +
            "WHERE workout_item_workout_id = :workoutID AND workout_item_id = :workoutItemID"
    )
    suspend fun updateWorkoutItemOrder(workoutID: Long, workoutItemID: Long, orderIndex: Int)

    @Query(
        "UPDATE workout_item SET workout_item_sets = :setCount " +
            "WHERE workout_item_workout_id = :workoutID AND workout_item_id = :workoutItemID"
    )
    suspend fun updateWorkoutItemSetCount(workoutID: Long, workoutItemID: Long, setCount: Int)

    @Transaction
    suspend fun addWorkoutExercises(workoutID: Long, exerciseIDs: List<Long>) {
        val includedExerciseIDs = getWorkoutExerciseIDs(workoutID).toSet()
        var orderIndex = getLastWorkoutItemOrder(workoutID) + 1
        exerciseIDs.distinct().filterNot(includedExerciseIDs::contains).forEach { exerciseID ->
            val workoutItemID =
                insertWorkoutItem(
                    WorkoutItemEntity(
                        workoutID = workoutID,
                        orderIndex = orderIndex++,
                        type = RoutineItemType.Exercise,
                        sets = null,
                        restTimeMillis = null,
                    )
                )
            insertExerciseWithWorkoutItems(
                listOf(
                    ExerciseWithWorkoutItemEntity(
                        workoutItemID = workoutItemID,
                        exerciseID = exerciseID,
                        orderIndex = 0,
                    )
                )
            )
        }
    }

    @Transaction
    suspend fun reorderWorkoutItems(workoutID: Long, workoutItemIDs: List<Long>) {
        val currentIDs = getWorkoutItemIDs(workoutID)
        require(
            workoutItemIDs.size == currentIDs.size && workoutItemIDs.toSet() == currentIDs.toSet()
        ) {
            "The reordered workout items must contain every item exactly once."
        }
        if (workoutItemIDs == currentIDs) return

        moveWorkoutItemsToTemporaryOrder(workoutID)
        workoutItemIDs.forEachIndexed { orderIndex, workoutItemID ->
            updateWorkoutItemOrder(workoutID, workoutItemID, orderIndex)
        }
    }

    @Transaction
    suspend fun removeWorkoutItem(workoutID: Long, workoutItemID: Long) {
        val exerciseIDs = getWorkoutItemExerciseIDs(workoutID, workoutItemID)
        deleteWorkoutItem(workoutID, workoutItemID)
        if (exerciseIDs.isNotEmpty()) {
            deleteRemovedExerciseSets(workoutID, exerciseIDs)
            deleteRemovedWorkoutGoals(workoutID, exerciseIDs)
        }
    }

    @Transaction
    suspend fun insertWorkoutItems(workoutID: Long, routineItems: List<RoutineItemExerciseDto>) {
        routineItems
            .groupBy { it.item }
            .forEach { (routineItem, rows) ->
                val firstRow = rows.first()
                val workoutItemID =
                    insertWorkoutItem(
                        WorkoutItemEntity(
                            workoutID = workoutID,
                            orderIndex = routineItem.orderIndex,
                            type = routineItem.type,
                            sets = firstRow.supersetSets,
                            restTimeMillis = firstRow.supersetRestTimeMillis,
                        )
                    )
                insertExerciseWithWorkoutItems(
                    rows.map { row ->
                        ExerciseWithWorkoutItemEntity(
                            workoutItemID = workoutItemID,
                            exerciseID = row.exercise.id,
                            orderIndex = row.exerciseOrder,
                        )
                    }
                )
            }
    }

    @RawQuery suspend fun query(query: RoomRawQuery): List<Long>

    companion object {
        fun getWorkoutsQuery(
            hasEndDate: Boolean,
            limit: Int? = null,
            offset: Int = 0,
        ): RoomRawQuery {
            return RoomRawQuery(getWorkoutsSql(hasEndDate, limit, offset))
        }

        internal fun getWorkoutsSql(
            hasEndDate: Boolean,
            limit: Int? = null,
            offset: Int = 0,
        ): String {
            val endDate = if (hasEndDate) "NOT NULL" else "NULL"
            val condition = "w.workout_end_date IS $endDate"
            return getWorkoutsSql(
                if (limit == null) condition
                else
                    "$condition AND ${workoutWindow("workout_end_date IS $endDate", limit, offset)}"
            )
        }

        /**
         * Narrows the query to a window of workouts. The join fans each workout out into a row per
         * set, so a `LIMIT` on the query itself would count rows; the workouts have to be chosen
         * before they are joined.
         */
        private fun workoutWindow(condition: String, limit: Int, offset: Int): String =
            "w.workout_id IN (SELECT workout_id FROM workout WHERE $condition " +
                "ORDER BY workout_start_date DESC, workout_id DESC LIMIT $limit OFFSET $offset)"

        fun getWorkoutsQuery(localDate: LocalDate): RoomRawQuery {
            val query = getWorkoutsSql("w.workout_start_date LIKE ?")
            return RoomRawQuery(query) {
                it.bindText(1, "${LocalDateTimeConverters.toString(localDate)}T%")
            }
        }

        fun getPastWorkoutsQuery(
            start: LocalDateTime,
            endExclusive: LocalDateTime,
        ): RoomRawQuery {
            val query =
                getWorkoutsSql(
                    "w.workout_end_date IS NOT NULL AND w.workout_start_date >= ? " +
                        "AND w.workout_start_date < ?"
                )
            return RoomRawQuery(query) {
                it.bindText(1, LocalDateTimeConverters.toString(start))
                it.bindText(2, LocalDateTimeConverters.toString(endExclusive))
            }
        }

        private fun getWorkoutsSql(whereClause: String): String =
            "SELECT w.*, item.*, exercise.*, workout_goal.*, " +
                "membership.workout_item_exercise_order_index AS workout_item_exercise_order, " +
                "membership.workout_item_exercise_notes AS workout_item_exercise_notes, " +
                "exercise_set.exercise_set_id AS current_exercise_set_id, " +
                "exercise_set.exercise_set_workout_id AS current_exercise_set_workout_id, " +
                "exercise_set.exercise_set_exercise_id AS current_exercise_set_exercise_id, " +
                "exercise_set.exercise_set_weight AS current_exercise_set_weight, " +
                "exercise_set.exercise_set_weight_unit AS current_exercise_set_weight_unit, " +
                "exercise_set.exercise_set_reps AS current_exercise_set_reps, " +
                "exercise_set.exercise_set_time AS current_exercise_set_time, " +
                "exercise_set.exercise_set_distance AS current_exercise_set_distance, " +
                "exercise_set.exercise_set_distance_unit AS current_exercise_set_distance_unit, " +
                "exercise_set.exercise_set_kcal AS current_exercise_set_kcal, " +
                "exercise_set.exercise_set_notes AS current_exercise_set_notes, " +
                "exercise_set.workout_exercise_set_index AS current_workout_exercise_set_index " +
                "FROM workout AS w " +
                "LEFT JOIN workout_item AS item ON w.workout_id = item.workout_item_workout_id " +
                "LEFT JOIN exercise_with_workout_item AS membership ON membership.workout_item_id = item.workout_item_id " +
                "LEFT JOIN exercise ON exercise.exercise_id = membership.exercise_id " +
                "LEFT JOIN workout_goal ON membership.exercise_id = workout_goal_exercise_id AND workout_goal_workout_id = w.workout_id " +
                "LEFT JOIN exercise_set ON membership.exercise_id = exercise_set_exercise_id AND exercise_set_workout_id = w.workout_id " +
                "WHERE $whereClause ORDER BY w.workout_start_date DESC, w.workout_id DESC, " +
                "item.workout_item_order_index, membership.workout_item_exercise_order_index, " +
                "workout_exercise_set_index"
    }
}
