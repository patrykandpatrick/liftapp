package com.patrykandpatrick.liftapp.functionality.database.migration

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.functionality.database.Database
import com.patrykandpatrick.liftapp.functionality.database.di.DatabaseModule
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Replays a version 14 database through [Migration14To15], which moves prescription out of the
 * exercise and the routine and into the plan.
 *
 * The fixture is one two-day plan scheduled for three cycles: a push day carrying two exercises
 * with goals of their own, and a rest day. One exercise deliberately has no `goal` row, to exercise
 * the fall back to what the exercise itself carried.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class Migration14To15Test {

    @get:Rule
    val helper =
        MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), Database::class.java)

    private val json =
        DomainModule.provideJson(setOf(DatabaseModule.provideExerciseStringResourceSerializer()))

    private val migration = Migration14To15(json)

    private fun createVersion14Database(): SupportSQLiteDatabase =
        helper.createDatabase(DB_NAME, 14).apply {
            insertExercise(BENCH_ID, "ex_flat_bench_press", goalSets = 3)
            insertExercise(CURL_ID, "ex_db_bicep_curl", goalSets = 3)

            execSQL(
                "INSERT INTO routine (routine_id, routine_name, routine_order_index) " +
                    "VALUES (?, ?, 0)",
                arrayOf<Any?>(ROUTINE_ID, "Push"),
            )
            insertRoutineItem(itemID = 1L, exerciseID = BENCH_ID, orderIndex = 0)
            insertRoutineItem(itemID = 2L, exerciseID = CURL_ID, orderIndex = 1)

            // The bench has a routine-specific goal; the curl has none and must fall back.
            insertGoal(exerciseID = BENCH_ID, sets = 4, minReps = 3, maxReps = 5, restSeconds = 180)

            execSQL(
                "INSERT INTO `plan` (plan_id, plan_name, plan_description, plan_item_count) " +
                    "VALUES (?, ?, '', 2)",
                arrayOf<Any?>(PLAN_ID, "Upper/Lower"),
            )
            execSQL(
                "INSERT INTO plan_item (plan_item_plan_id, plan_item_routine_id, " +
                    "plan_item_order_index) VALUES (?, ?, 0)",
                arrayOf<Any?>(PLAN_ID, ROUTINE_ID),
            )
            // Two days per cycle, three cycles: six schedule rows from the 1st onward.
            repeat(CYCLE_COUNT * ITEM_COUNT) { dayIndex ->
                execSQL(
                    "INSERT INTO plan_item_schedule (plan_item_schedule_plan_id, " +
                        "plan_item_routine_id, plan_item_schedule_date) VALUES (?, ?, ?)",
                    arrayOf<Any?>(
                        PLAN_ID,
                        ROUTINE_ID.takeIf { dayIndex % ITEM_COUNT == 0 },
                        "2026-09-%02d".format(dayIndex + 1),
                    ),
                )
            }
            close()
        }

    private fun migrate(): SupportSQLiteDatabase {
        createVersion14Database()
        return helper.runMigrationsAndValidate(DB_NAME, 15, true, migration)
    }

    @Test
    fun `a goal becomes one target per set it asked for`() {
        val db = migrate()

        val sets = db.targetSets(cycleIndex = 0, exerciseID = BENCH_ID)

        assertEquals(4, sets.size)
        assertTrue(sets.all { it.minReps == 3 && it.maxReps == 5 })
    }

    @Test
    fun `the rest time comes across with the reps`() {
        val db = migrate()

        assertEquals(
            180_000L,
            db.single(
                "SELECT exercise_target_rest_time FROM exercise_target " +
                    "WHERE exercise_target_exercise_id = $BENCH_ID " +
                    "AND exercise_target_cycle_index = 0"
            ) {
                it.getLong(0)
            },
        )
    }

    @Test
    fun `an exercise with no goal of its own falls back to the one it carried`() {
        val db = migrate()

        val sets = db.targetSets(cycleIndex = 0, exerciseID = CURL_ID)

        assertEquals(3, sets.size)
        assertTrue(sets.all { it.minReps == 8 && it.maxReps == 12 })
    }

    @Test
    fun `every scheduled cycle gets its own copy, so editing one week leaves the others alone`() {
        val db = migrate()

        repeat(CYCLE_COUNT) { cycleIndex ->
            assertEquals(
                4,
                db.targetSets(cycleIndex, BENCH_ID).size,
                "Cycle $cycleIndex is missing the bench target.",
            )
        }
        assertEquals(
            CYCLE_COUNT * 2,
            db.single("SELECT COUNT(*) FROM exercise_target") { it.getInt(0) },
        )
    }

    @Test
    fun `the schedule learns where each of its days falls`() {
        val db = migrate()

        val positions =
            db.list(
                "SELECT plan_item_schedule_cycle_index, plan_item_schedule_item_order_index " +
                    "FROM plan_item_schedule ORDER BY plan_item_schedule_date"
            ) { cursor ->
                cursor.getInt(0) to cursor.getInt(1)
            }

        assertEquals(listOf(0 to 0, 0 to 1, 1 to 0, 1 to 1, 2 to 0, 2 to 1), positions)
    }

    @Test
    fun `prescription is gone from the exercise and the routine`() {
        val db = migrate()

        assertFalse("exercise_goal" in db.columnNames("exercise"))
        assertTrue(
            db.list("SELECT name FROM sqlite_master WHERE type = 'table' AND name = 'goal'") {
                    it.getString(0)
                }
                .isEmpty()
        )
    }

    @Test
    fun `the exercises themselves survive the rebuild`() {
        val db = migrate()

        assertEquals(
            listOf(CURL_ID, BENCH_ID),
            db.list("SELECT exercise_id FROM exercise ORDER BY exercise_id") { it.getLong(0) },
        )
        assertEquals(
            "DoubleProgression",
            db.single(
                "SELECT exercise_progression_policy FROM exercise WHERE exercise_id = $BENCH_ID"
            ) {
                it.getString(0)
            },
        )
    }

    @Test
    fun `running the migration twice changes nothing`() {
        val db = migrate()
        val afterFirstRun = db.targetRows()

        migration.migrate(db)

        assertEquals(afterFirstRun, db.targetRows())
    }

    @Test
    fun `leaves no dangling references`() {
        val db = migrate()

        assertTrue(db.list("PRAGMA foreign_key_check") { it.getString(0) }.isEmpty())
    }

    private fun SupportSQLiteDatabase.insertExercise(id: Long, name: String, goalSets: Int) {
        val goal = LegacyGoal.default.copy(sets = goalSets)
        execSQL(
            "INSERT INTO exercise (exercise_id, exercise_name, exercise_type, " +
                "exercise_main_muscles, exercise_secondary_muscles, exercise_tertiary_muscles, " +
                "exercise_goal, exercise_progression_policy) " +
                "VALUES (?, ?, 'Weight', '[]', '[]', '[]', ?, 'DoubleProgression')",
            arrayOf<Any?>(id, "\"$name\"", json.encodeToString(goal)),
        )
    }

    private fun SupportSQLiteDatabase.insertRoutineItem(
        itemID: Long,
        exerciseID: Long,
        orderIndex: Int,
    ) {
        execSQL(
            "INSERT INTO routine_item (routine_item_id, routine_item_routine_id, " +
                "routine_item_order_index, routine_item_type) VALUES (?, ?, ?, 'Exercise')",
            arrayOf<Any?>(itemID, ROUTINE_ID, orderIndex),
        )
        execSQL(
            "INSERT INTO exercise_with_routine_item (routine_item_id, exercise_id, " +
                "routine_item_exercise_order_index) VALUES (?, ?, 0)",
            arrayOf<Any?>(itemID, exerciseID),
        )
    }

    private fun SupportSQLiteDatabase.insertGoal(
        exerciseID: Long,
        sets: Int,
        minReps: Int,
        maxReps: Int,
        restSeconds: Int,
    ) {
        execSQL(
            "INSERT INTO goal (goal_routine_id, goal_exercise_id, goal_min_reps, goal_max_reps, " +
                "goal_sets, goal_rest_time, goal_duration_millis, goal_distance, " +
                "goal_distance_unit, goal_calories) VALUES (?, ?, ?, ?, ?, ?, 0, 0.0, ?, 0.0)",
            arrayOf<Any?>(
                ROUTINE_ID,
                exerciseID,
                minReps,
                maxReps,
                sets,
                restSeconds * 1_000L,
                json.encodeToString(LegacyGoal.default.distanceUnit),
            ),
        )
    }

    private fun SupportSQLiteDatabase.targetSets(
        cycleIndex: Int,
        exerciseID: Long,
    ): List<SetTarget> =
        json.decodeFromString(
            single(
                "SELECT exercise_target_sets FROM exercise_target " +
                    "WHERE exercise_target_exercise_id = $exerciseID " +
                    "AND exercise_target_cycle_index = $cycleIndex"
            ) {
                it.getString(0)
            }
        )

    private fun SupportSQLiteDatabase.targetRows(): List<List<String?>> =
        list(
            "SELECT * FROM exercise_target ORDER BY exercise_target_cycle_index, " +
                "exercise_target_exercise_id"
        ) { cursor ->
            List(cursor.columnCount) { index -> cursor.stringOrNull(index) }
        }

    private fun SupportSQLiteDatabase.columnNames(table: String): Set<String> =
        query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            buildSet { while (cursor.moveToNext()) add(cursor.getString(nameIndex)) }
        }

    private fun Cursor.stringOrNull(index: Int): String? =
        if (isNull(index)) null else getString(index)

    private fun <T> SupportSQLiteDatabase.single(sql: String, read: (Cursor) -> T): T =
        list(sql, read).single()

    private fun <T> SupportSQLiteDatabase.list(sql: String, read: (Cursor) -> T): List<T> =
        query(sql).use { cursor -> buildList { while (cursor.moveToNext()) add(read(cursor)) } }

    private companion object {
        const val DB_NAME = "prescription-migration-test"
        const val PLAN_ID = 1L
        const val ROUTINE_ID = 7L
        const val BENCH_ID = 6L
        const val CURL_ID = 4L
        const val ITEM_COUNT = 2
        const val CYCLE_COUNT = 3
    }
}
