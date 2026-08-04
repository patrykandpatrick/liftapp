package com.patrykandpatrick.liftapp.functionality.database.routine

import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.routine.RoutineItem
import kotlin.test.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RoutineDaoReplaceItemsTest {

    @Test
    fun `reordering routines stores dense order indices`() = runTest {
        val dao = FakeRoutineDao()

        dao.reorder(listOf(8L, 3L, 5L))

        assertEquals(mapOf(8L to 0, 3L to 1, 5L to 2), dao.routineOrderIndices)
    }

    @Test
    fun `replacing items keeps the IDs of the items that already exist`() = runTest {
        val dao = FakeRoutineDao()
        dao.replaceItems(
            ROUTINE_ID,
            listOf(RoutineItem.exercise(exerciseID = 1), RoutineItem.exercise(exerciseID = 2)),
        )
        val originalIDs = dao.items.sortedBy { it.orderIndex }.map { it.id }

        dao.replaceItems(
            ROUTINE_ID,
            listOf(
                RoutineItem.exercise(exerciseID = 2, id = originalIDs[1]),
                RoutineItem.exercise(exerciseID = 1, id = originalIDs[0]),
            ),
        )

        assertEquals(
            listOf(originalIDs[1], originalIDs[0]),
            dao.items.sortedBy { it.orderIndex }.map { it.id },
        )
    }

    @Test
    fun `replacing items gives the new ones IDs that do not clash with the reused ones`() =
        runTest {
            val dao = FakeRoutineDao()
            dao.replaceItems(ROUTINE_ID, listOf(RoutineItem.exercise(exerciseID = 1)))
            val existingID = dao.items.single().id

            dao.replaceItems(
                ROUTINE_ID,
                listOf(
                    RoutineItem.exercise(exerciseID = 2),
                    RoutineItem.exercise(exerciseID = 1, id = existingID),
                ),
            )

            val ids = dao.items.map { it.id }
            assertEquals(ids.size, ids.distinct().size)
            assertEquals(setOf(0, 1), dao.items.map { it.orderIndex }.toSet())
            assertEquals(existingID, dao.items.single { it.orderIndex == 1 }.id)
        }

    @Test
    fun `replacing items stores the exercises and the superset settings in order`() = runTest {
        val dao = FakeRoutineDao()
        val superset = RoutineItem.superset(exerciseIDs = listOf(3, 4))

        dao.replaceItems(ROUTINE_ID, listOf(RoutineItem.exercise(exerciseID = 1), superset))

        val supersetID = dao.items.single { it.orderIndex == 1 }.id
        assertEquals(
            listOf(3L to 0, 4L to 1),
            dao.memberships
                .asSequence()
                .filter { it.routineItemID == supersetID }
                .sortedBy { it.orderIndex }
                .map { it.exerciseID to it.orderIndex }
                .toList(),
        )
        assertEquals(supersetID, dao.supersets.single().routineItemID)
        assertEquals(checkNotNull(superset.supersetConfig).sets, dao.supersets.single().sets)
    }

    /** Assigns IDs the way SQLite assigns row IDs: one above the highest one in the table. */
    private class FakeRoutineDao : RoutineDao {
        val items = mutableListOf<RoutineItemEntity>()
        val memberships = mutableListOf<ExerciseWithRoutineItemEntity>()
        val supersets = mutableListOf<SupersetEntity>()
        val routineOrderIndices = mutableMapOf<Long, Int>()

        override suspend fun insert(item: RoutineItemEntity): Long {
            val id = item.id.takeIf { it != ID_NOT_SET } ?: (items.maxOfOrNull { it.id } ?: 0) + 1
            require(items.none { it.id == id }) { "Duplicate routine item ID $id." }
            items.add(item.copy(id = id))
            return id
        }

        override suspend fun insert(memberships: List<ExerciseWithRoutineItemEntity>) {
            this.memberships.addAll(memberships)
        }

        override suspend fun insert(superset: SupersetEntity) {
            supersets.add(superset)
        }

        override suspend fun deleteItems(routineId: Long) {
            val removedIDs = items.filter { it.routineID == routineId }.map { it.id }.toSet()
            items.removeAll { it.routineID == routineId }
            memberships.removeAll { it.routineItemID in removedIDs }
            supersets.removeAll { it.routineItemID in removedIDs }
        }

        override fun getRoutinesWithExerciseNames(): Flow<List<RoutineWithExerciseNamesView>> =
            unsupported()

        override fun getRoutineItems(routineId: Long): Flow<List<RoutineItemExerciseDto>> =
            unsupported()

        override fun getRoutine(routineID: Long): Flow<RoutineEntity?> = unsupported()

        override suspend fun upsert(routine: RoutineEntity): Long = unsupported()

        override suspend fun updateName(routineID: Long, name: String): Int = unsupported()

        override suspend fun getNextOrderIndex(): Int = unsupported()

        override suspend fun insertRoutine(routine: RoutineEntity): Long = unsupported()

        override suspend fun updateOrderIndex(routineID: Long, orderIndex: Int) {
            routineOrderIndices[routineID] = orderIndex
        }

        override suspend fun delete(routineId: Long) = unsupported()

        private fun unsupported(): Nothing = throw UnsupportedOperationException()
    }

    private companion object {
        const val ROUTINE_ID = 1L
    }
}
