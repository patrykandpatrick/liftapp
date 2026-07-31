package com.patrykandpatrick.liftapp.functionality.database.migration

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import com.patrykandpatrick.liftapp.functionality.database.Database
import com.patrykandpatrick.liftapp.functionality.database.di.DatabaseModule
import java.time.LocalDate
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Walks a database from version 6 — the oldest schema the published app could migrate, and the
 * oldest an Android auto-backup can hand this app — through every carried-over migration and the
 * 11→12 conversion. Before version 11, routine ID lists held raw exercise IDs with `&` joining
 * supersets, so this also exercises the ported 10→11 model extraction.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PublishedMigrationsTest {

    @get:Rule
    val helper =
        MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), Database::class.java)

    private val json =
        DomainModule.provideJson(
            setOf(
                DatabaseModule.provideExerciseStringResourceSerializer(),
                DatabaseModule.provideBodyMeasurementStringResourceSerializer(),
            )
        )

    @Test
    fun `a version 6 database reaches version 12 with its data converted`() {
        createVersion6Database()

        val db =
            helper.runMigrationsAndValidate(
                DB_NAME,
                12,
                true,
                PublishedMigrations.MIGRATION_6_7,
                PublishedMigrations.MIGRATION_7_8,
                PublishedMigrations.MIGRATION_6_8,
                PublishedMigrations.MIGRATION_8_9,
                PublishedMigrations.MIGRATION_9_10,
                PublishedMigrations.MIGRATION_10_11,
                Migration11To12(
                    json = json,
                    legacyPlan = { Migration11To12.LegacyPlan("My training plan", listOf(100)) },
                    startDate = { LocalDate.of(2026, 7, 31) },
                ),
            )

        val items = mutableListOf<Pair<Long, String>>()
        db.query(
                "SELECT routine_item_id, routine_item_type FROM routine_item " +
                    "WHERE routine_item_routine_id = 100 ORDER BY routine_item_order_index"
            )
            .use { cursor ->
                while (cursor.moveToNext()) items.add(cursor.getLong(0) to cursor.getString(1))
            }
        assertEquals(listOf("Exercise", "Superset"), items.map { it.second })

        val supersetMembers = mutableListOf<Long>()
        db.query(
                "SELECT exercise_id FROM exercise_with_routine_item " +
                    "WHERE routine_item_id = ${items[1].first} " +
                    "ORDER BY routine_item_exercise_order_index"
            )
            .use { cursor -> while (cursor.moveToNext()) supersetMembers.add(cursor.getLong(0)) }
        assertEquals(listOf(2L, CUSTOM_EXERCISE_ID), supersetMembers)

        db.query(
                "SELECT workout_end_date, workout_name FROM workout WHERE workout_id = $WORKOUT_ID"
            )
            .use { cursor ->
                cursor.moveToFirst()
                assertEquals("Old push", cursor.getString(1))
            }
        db.query("SELECT COUNT(*) FROM exercise_set WHERE exercise_set_workout_id = $WORKOUT_ID")
            .use { cursor ->
                cursor.moveToFirst()
                assertEquals(3, cursor.getInt(0))
            }
    }

    /**
     * The version 6 layout, reconstructed from the published `MIGRATION_6_7` and `MIGRATION_6_8`.
     */
    private fun createVersion6Database() {
        val file = ApplicationProvider.getApplicationContext<Context>().getDatabasePath(DB_NAME)
        file.parentFile?.mkdirs()
        SQLiteDatabase.openOrCreateDatabase(file, null).use { db ->
            db.execSQL(
                "CREATE TABLE TrainingPlan (id INTEGER PRIMARY KEY NOT NULL, name TEXT NOT NULL, " +
                    "exercise_ids TEXT NOT NULL, order_number INTEGER NOT NULL)"
            )
            db.execSQL(
                "CREATE TABLE PlanRecord (id INTEGER PRIMARY KEY NOT NULL, " +
                    "end_time INTEGER NOT NULL, record_plan_id INTEGER NOT NULL, " +
                    "name TEXT NOT NULL, exercise_ids TEXT NOT NULL, record_ids TEXT NOT NULL)"
            )
            db.execSQL(
                "CREATE INDEX index_PlanRecord_record_plan_id ON PlanRecord (record_plan_id)"
            )
            db.execSQL(
                "CREATE TABLE Exercise (exercise_id INTEGER NOT NULL, exercise_type TEXT, " +
                    "exercise_name TEXT, exercise_goal TEXT, exercise_main_muscles TEXT, " +
                    "exercise_secondary_muscles TEXT, exercise_tertiary_muscles TEXT, " +
                    "PRIMARY KEY(exercise_id))"
            )
            db.execSQL(
                "CREATE TABLE ExerciseRecord (ex_record_id INTEGER NOT NULL, " +
                    "ex_record_exercise_id INTEGER NOT NULL, ex_records TEXT, ex_comment TEXT, " +
                    "PRIMARY KEY(ex_record_id))"
            )
            db.execSQL("CREATE TABLE Body (m_id INTEGER NOT NULL, m_type TEXT, PRIMARY KEY(m_id))")
            db.execSQL(
                "CREATE TABLE BodyRecord (r_id INTEGER NOT NULL, r_m_id INTEGER NOT NULL, " +
                    "r_value_left REAL NOT NULL, r_value_right REAL NOT NULL, r_unit TEXT, " +
                    "PRIMARY KEY(r_id))"
            )

            db.execSQL(
                "INSERT INTO Exercise VALUES (6, 'WEIGHT', 'ex_flat_bench_press', '4 3 12 180', " +
                    "'CHEST TRICEPS', 'SHOULDERS', 'FOREARMS ABS LATS')"
            )
            db.execSQL(
                "INSERT INTO Exercise VALUES (2, 'CALISTHENICS', 'ex_pull_ups', '5 4 12 120', " +
                    "'LATS BICEPS', 'SHOULDERS FOREARMS', 'ABS')"
            )
            db.execSQL(
                "INSERT INTO Exercise VALUES ($CUSTOM_EXERCISE_ID, 'WEIGHT', 'My curls', " +
                    "'3 8 12 60', 'BICEPS', NULL, NULL)"
            )
            db.execSQL(
                "INSERT INTO TrainingPlan VALUES (100, 'Old push', '6 2&$CUSTOM_EXERCISE_ID', 0)"
            )
            db.execSQL(
                "INSERT INTO PlanRecord VALUES ($WORKOUT_ID, ${WORKOUT_ID + 3_600_000}, 100, " +
                    "'Old push', '6 2&$CUSTOM_EXERCISE_ID', '${WORKOUT_ID + 1} ${WORKOUT_ID + 2} ${WORKOUT_ID + 3}')"
            )
            db.execSQL(
                "INSERT INTO ExerciseRecord VALUES (${WORKOUT_ID + 1}, 6, " +
                    "'WEIGHT kg 4 100 5<comments></comments>', NULL)"
            )
            db.execSQL(
                "INSERT INTO ExerciseRecord VALUES (${WORKOUT_ID + 2}, 2, " +
                    "'CALISTHENICS kg 3 10 80 8<comments></comments>', NULL)"
            )
            db.execSQL(
                "INSERT INTO ExerciseRecord VALUES (${WORKOUT_ID + 3}, $CUSTOM_EXERCISE_ID, " +
                    "'WEIGHT lb 3 45 12<comments></comments>', NULL)"
            )
            db.version = 6
        }
    }

    private companion object {
        const val DB_NAME = "published-migrations-test"
        const val CUSTOM_EXERCISE_ID = 1_600_000_000_000L
        const val WORKOUT_ID = 1_700_000_000_000L
    }
}
