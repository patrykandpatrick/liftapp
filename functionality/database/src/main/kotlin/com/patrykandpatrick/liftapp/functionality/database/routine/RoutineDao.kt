package com.patrykandpatrick.liftapp.functionality.database.routine

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("SELECT * FROM routine_with_exercise_names ORDER BY routine_order_index")
    fun getRoutinesWithExerciseNames(): Flow<List<RoutineWithExerciseNamesView>>

    @Query(
        value =
            "SELECT item.*, exercise.*, goal.*, " +
                "membership.routine_item_exercise_order_index AS routine_item_exercise_order, " +
                "superset.superset_sets AS superset_sets, " +
                "superset.superset_rest_time_millis AS superset_rest_time_millis " +
                "FROM routine_item AS item " +
                "LEFT JOIN superset ON superset.superset_routine_item_id = item.routine_item_id " +
                "LEFT JOIN exercise_with_routine_item AS membership " +
                "ON membership.routine_item_id = item.routine_item_id " +
                "LEFT JOIN exercise ON exercise.exercise_id = membership.exercise_id " +
                "LEFT JOIN goal on goal.goal_exercise_id = exercise.exercise_id " +
                "AND goal.goal_routine_id = :routineId " +
                "WHERE item.routine_item_routine_id = :routineId " +
                "ORDER BY item.routine_item_order_index, membership.routine_item_exercise_order_index"
    )
    fun getRoutineItems(routineId: Long): Flow<List<RoutineItemExerciseDto>>

    @Query("SELECT * FROM routine WHERE routine_id = :routineID")
    fun getRoutine(routineID: Long): Flow<RoutineEntity?>

    @Query("UPDATE routine SET routine_name = :name WHERE routine_id = :routineID")
    suspend fun updateName(routineID: Long, name: String): Int

    @Query("SELECT COALESCE(MAX(routine_order_index), -1) + 1 FROM routine")
    suspend fun getNextOrderIndex(): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    /** Updating a routine must not move it. New routines are appended to the existing order. */
    @Transaction
    suspend fun upsert(routine: RoutineEntity): Long {
        if (routine.id != ID_NOT_SET && updateName(routine.id, routine.name) > 0) {
            return routine.id
        }
        return insertRoutine(routine.copy(orderIndex = getNextOrderIndex()))
    }

    @Insert suspend fun insert(item: RoutineItemEntity): Long

    @Insert suspend fun insert(memberships: List<ExerciseWithRoutineItemEntity>)

    @Insert suspend fun insert(superset: SupersetEntity)

    @Query("DELETE FROM routine_item WHERE routine_item_routine_id = :routineId")
    suspend fun deleteItems(routineId: Long)

    @Transaction
    suspend fun replaceItems(routineId: Long, items: List<RoutineItem>) {
        deleteItems(routineId)
        // Items that already exist keep their IDs, so that whoever observes them—the routine
        // screen, most notably—keeps recognizing them across updates. Such items are reinserted
        // before the new ones, whose IDs are generated, so that a generated ID cannot take an ID
        // that is about to be reused.
        items
            .withIndex()
            .sortedByDescending { (_, item) -> item.id }
            .forEach { (itemIndex, item) ->
                val itemID =
                    insert(
                        RoutineItemEntity(
                            id = item.id,
                            routineID = routineId,
                            orderIndex = itemIndex,
                            type = item.type,
                        )
                    )
                insert(
                    item.exerciseIDs.mapIndexed { exerciseIndex, exerciseID ->
                        ExerciseWithRoutineItemEntity(itemID, exerciseID, exerciseIndex)
                    }
                )
                if (item.type == RoutineItemType.Superset) {
                    val config = checkNotNull(item.supersetConfig)
                    insert(SupersetEntity(itemID, config.sets, config.restTime.inWholeMilliseconds))
                }
            }
    }

    @Transaction
    suspend fun upsertWithItems(routine: RoutineEntity, items: List<RoutineItem>): Long {
        val routineID = upsert(routine).takeIf { it > 0 } ?: routine.id
        replaceItems(routineID, items)
        return routineID
    }

    @Query("UPDATE routine SET routine_order_index = :orderIndex WHERE routine_id = :routineID")
    suspend fun updateOrderIndex(routineID: Long, orderIndex: Int)

    @Transaction
    suspend fun reorder(routineIDs: List<Long>) {
        routineIDs.forEachIndexed { orderIndex, routineID ->
            updateOrderIndex(routineID, orderIndex)
        }
    }

    @Query("DELETE FROM routine WHERE routine_id = :routineId") suspend fun delete(routineId: Long)
}
