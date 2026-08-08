package com.patrykandpatrick.liftapp.functionality.database.migration

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import com.patrykandpatrick.liftapp.functionality.database.Database
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Replays a version 12 database through [Migration12To13], which adds reps in reserve to a logged
 * set.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class Migration12To13Test {

    @get:Rule
    val helper =
        MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), Database::class.java)

    private fun createVersion12Database(): SupportSQLiteDatabase =
        helper.createDatabase(DB_NAME, 12).apply {
            execSQL(
                "INSERT INTO exercise (exercise_id, exercise_name, exercise_type, " +
                    "exercise_main_muscles, exercise_secondary_muscles, exercise_tertiary_muscles, " +
                    "exercise_goal) VALUES ($EXERCISE_ID, '\"Bench press\"', 'Weight', '[]', '[]', " +
                    "'[]', '{}')"
            )
            execSQL("INSERT INTO routine (routine_id, routine_name) VALUES ($ROUTINE_ID, 'Push')")
            execSQL(
                "INSERT INTO workout (workout_id, workout_routine_id, workout_name, " +
                    "workout_start_date, workout_end_date, workout_notes) " +
                    "VALUES ($WORKOUT_ID, $ROUTINE_ID, 'Push day', '2026-08-01T10:00:00', NULL, '')"
            )
            execSQL(
                "INSERT INTO exercise_set (exercise_set_workout_id, exercise_set_exercise_id, " +
                    "exercise_set_weight, exercise_set_reps, exercise_set_notes, " +
                    "workout_exercise_set_index) " +
                    "VALUES ($WORKOUT_ID, $EXERCISE_ID, 100.0, 8, 'Felt heavy', 0)"
            )
            close()
        }

    private fun migrate(): SupportSQLiteDatabase {
        createVersion12Database()
        return helper.runMigrationsAndValidate(DB_NAME, 13, true, Migration12To13)
    }

    @Test
    fun `a set logged before the column existed keeps its data and reports no rir`() {
        val db = migrate()

        assertEquals(
            Triple(100.0, 8, "Felt heavy"),
            db.single(
                "SELECT exercise_set_weight, exercise_set_reps, exercise_set_notes " +
                    "FROM exercise_set WHERE exercise_set_workout_id = $WORKOUT_ID"
            ) {
                Triple(it.getDouble(0), it.getInt(1), it.getString(2))
            },
        )
        assertNull(
            db.single(
                "SELECT exercise_set_rir FROM exercise_set " +
                    "WHERE exercise_set_workout_id = $WORKOUT_ID"
            ) {
                if (it.isNull(0)) null else it.getInt(0)
            }
        )
    }

    @Test
    fun `the new column accepts a reported rir, zero included`() {
        val db = migrate()

        db.execSQL(
            "INSERT INTO exercise_set (exercise_set_workout_id, exercise_set_exercise_id, " +
                "exercise_set_weight, exercise_set_reps, exercise_set_rir, exercise_set_notes, " +
                "workout_exercise_set_index) " +
                "VALUES ($WORKOUT_ID, $EXERCISE_ID, 100.0, 6, 0, '', 1)"
        )

        assertEquals(
            listOf(null, 0),
            db.list(
                "SELECT exercise_set_rir FROM exercise_set " +
                    "WHERE exercise_set_workout_id = $WORKOUT_ID " +
                    "ORDER BY workout_exercise_set_index"
            ) {
                if (it.isNull(0)) null else it.getInt(0)
            },
        )
    }

    @Test
    fun `leaves no dangling references`() {
        val db = migrate()

        assertTrue(db.list("PRAGMA foreign_key_check") { it.getString(0) }.isEmpty())
    }

    private fun <T> SupportSQLiteDatabase.single(sql: String, read: (Cursor) -> T): T =
        list(sql, read).single()

    private fun <T> SupportSQLiteDatabase.list(sql: String, read: (Cursor) -> T): List<T> =
        query(sql).use { cursor -> buildList { while (cursor.moveToNext()) add(read(cursor)) } }

    private companion object {
        const val DB_NAME = "rir-migration-test"
        const val EXERCISE_ID = 1L
        const val ROUTINE_ID = 100L
        const val WORKOUT_ID = 1_700_000_000_000L
    }
}
