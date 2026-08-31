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
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Replays a version 15 database through [Migration15To16], which turns the flat per-workout
 * snapshot into the per-set list the target it is copied from already was.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class Migration15To16Test {

    @get:Rule
    val helper =
        MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), Database::class.java)

    private val json =
        DomainModule.provideJson(setOf(DatabaseModule.provideExerciseStringResourceSerializer()))

    private val migration = Migration15To16(json)

    private fun createVersion15Database(): SupportSQLiteDatabase =
        helper.createDatabase(DB_NAME, 15).apply {
            execSQL(
                "INSERT INTO exercise (exercise_id, exercise_name, exercise_type, " +
                    "exercise_main_muscles, exercise_secondary_muscles, exercise_tertiary_muscles, " +
                    "exercise_progression_policy) " +
                    "VALUES (?, '\"Bench\"', 'Weight', '[]', '[]', '[]', 'DoubleProgression')",
                arrayOf<Any?>(EXERCISE_ID),
            )
            execSQL(
                "INSERT INTO routine (routine_id, routine_name, routine_order_index) " +
                    "VALUES (?, 'Push', 0)",
                arrayOf<Any?>(ROUTINE_ID),
            )
            execSQL(
                "INSERT INTO workout (workout_id, workout_routine_id, workout_name, " +
                    "workout_start_date, workout_notes) VALUES (?, ?, 'Push', ?, '')",
                arrayOf<Any?>(WORKOUT_ID, ROUTINE_ID, "2026-09-01T10:00:00"),
            )
            insertFlatGoal(sets = 3, minReps = 8, maxReps = 12, restMillis = 120_000L)
            close()
        }

    private fun migrate(): SupportSQLiteDatabase {
        createVersion15Database()
        return helper.runMigrationsAndValidate(DB_NAME, 16, true, migration)
    }

    @Test
    fun `a flat goal becomes one target per set it asked for`() {
        val db = migrate()

        val sets = db.setTargets()

        assertEquals(3, sets.size)
        assertTrue(sets.all { it.minReps == 8 && it.maxReps == 12 })
    }

    @Test
    fun `the rest time survives`() {
        val db = migrate()

        assertEquals(
            120_000L,
            db.single("SELECT workout_goal_rest_time FROM workout_goal") { it.getLong(0) },
        )
    }

    @Test
    fun `a cardio goal keeps its duration and distance on every set`() {
        createVersion15Database()
        helper.createDatabase(DB_NAME, 15).apply {
            execSQL("DELETE FROM workout_goal")
            insertFlatGoal(
                sets = 2,
                minReps = 0,
                maxReps = 0,
                restMillis = 60_000L,
                durationMillis = 5.minutes.inWholeMilliseconds,
                distance = 5.0,
                calories = 300.0,
            )
            close()
        }
        val db = helper.runMigrationsAndValidate(DB_NAME, 16, true, migration)

        val sets = db.setTargets()

        assertEquals(2, sets.size)
        assertTrue(sets.all { it.duration == 5.minutes && it.distance == 5.0 })
    }

    @Test
    fun `the row keeps its identity, so a session in progress is not orphaned`() {
        val db = migrate()

        assertEquals(
            listOf(GOAL_ID to WORKOUT_ID),
            db.list("SELECT workout_goal_id, workout_goal_workout_id FROM workout_goal") { cursor ->
                cursor.getLong(0) to cursor.getLong(1)
            },
        )
    }

    @Test
    fun `running the migration twice changes nothing`() {
        val db = migrate()
        val afterFirstRun = db.goalRows()

        migration.migrate(db)

        assertEquals(afterFirstRun, db.goalRows())
    }

    @Test
    fun `leaves no dangling references`() {
        val db = migrate()

        assertTrue(db.list("PRAGMA foreign_key_check") { it.getString(0) }.isEmpty())
    }

    private fun SupportSQLiteDatabase.insertFlatGoal(
        sets: Int,
        minReps: Int,
        maxReps: Int,
        restMillis: Long,
        durationMillis: Long = 0L,
        distance: Double = 0.0,
        calories: Double = 0.0,
    ) {
        execSQL(
            "INSERT INTO workout_goal (workout_goal_id, workout_goal_workout_id, " +
                "workout_goal_exercise_id, workout_goal_min_reps, workout_goal_max_reps, " +
                "workout_goal_sets, workout_goal_rest_time, workout_goal_duration_millis, " +
                "workout_goal_distance, workout_goal_distance_unit, workout_goal_calories) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '\"Kilometer\"', ?)",
            arrayOf<Any?>(
                GOAL_ID,
                WORKOUT_ID,
                EXERCISE_ID,
                minReps,
                maxReps,
                sets,
                restMillis,
                durationMillis,
                distance,
                calories,
            ),
        )
    }

    private fun SupportSQLiteDatabase.setTargets(): List<SetTarget> =
        json.decodeFromString(
            single("SELECT workout_goal_set_targets FROM workout_goal") { it.getString(0) }
        )

    private fun SupportSQLiteDatabase.goalRows(): List<List<String?>> =
        list("SELECT * FROM workout_goal ORDER BY workout_goal_id") { cursor ->
            List(cursor.columnCount) { index ->
                if (cursor.isNull(index)) null else cursor.getString(index)
            }
        }

    private fun <T> SupportSQLiteDatabase.single(sql: String, read: (Cursor) -> T): T =
        list(sql, read).single()

    private fun <T> SupportSQLiteDatabase.list(sql: String, read: (Cursor) -> T): List<T> =
        query(sql).use { cursor -> buildList { while (cursor.moveToNext()) add(read(cursor)) } }

    private companion object {
        const val DB_NAME = "set-target-snapshot-migration-test"
        const val GOAL_ID = 1L
        const val WORKOUT_ID = 3L
        const val ROUTINE_ID = 7L
        const val EXERCISE_ID = 6L
    }
}
