package com.patrykandpatrick.liftapp.functionality.database.migration

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import com.patrykandpatrick.liftapp.functionality.database.Database
import com.patrykandpatrick.liftapp.functionality.database.di.DatabaseModule
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Replays a version 13 database through [Migration13To14], which classifies exercises and splits
 * `Muscle.Shoulders` into the three deltoid heads.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class Migration13To14Test {

    @get:Rule
    val helper =
        MigrationTestHelper(InstrumentationRegistry.getInstrumentation(), Database::class.java)

    private val migration =
        Migration13To14(
            DomainModule.provideJson(
                setOf(DatabaseModule.provideExerciseStringResourceSerializer())
            )
        )

    private fun createVersion13Database(): SupportSQLiteDatabase =
        helper.createDatabase(DB_NAME, 13).apply {
            // Built-in rows, with the muscle lists they carried before the split. The IDs are the
            // ones DefaultExercises gives these exercises.
            insertExercise(
                id = BENCH_PRESS_ID,
                name = "ex_flat_bench_press",
                type = "Weight",
                main = """["Chest","Triceps"]""",
                secondary = """["Shoulders"]""",
                tertiary = """["Forearms","Abs","Lats"]""",
            )
            insertExercise(
                id = FACE_PULL_ID,
                name = "ex_face_pull",
                type = "Weight",
                main = """["Shoulders"]""",
                secondary = """["Traps"]""",
                tertiary = """["Forearms"]""",
            )
            insertExercise(
                id = SIDE_ARM_RAISE_ID,
                name = "ex_side_arm_raise",
                type = "Weight",
                main = """["Shoulders"]""",
                secondary = """["Traps"]""",
            )
            insertExercise(
                id = SHRUGS_ID,
                name = "ex_shrugs",
                type = "Weight",
                main = """["Shoulders"]""",
                tertiary = """["Forearms"]""",
            )
            insertExercise(
                id = LONG_RUN_ID,
                name = "ex_long_run",
                type = "Cardio",
                main = """["Hamstrings","Calves"]""",
                secondary = """["Quadriceps","Glutes"]""",
            )
            insertExercise(
                id = CUSTOM_EXERCISE_ID,
                name = "My raises",
                type = "Weight",
                main = """["Shoulders"]""",
                secondary = """["Chest"]""",
            )
            close()
        }

    private fun migrate(): SupportSQLiteDatabase {
        createVersion13Database()
        return helper.runMigrationsAndValidate(DB_NAME, 14, true, migration)
    }

    @Test
    fun `built-in exercises get their own classification rather than one shared default`() {
        val db = migrate()

        assertEquals(
            Classification("PrimaryCompound", "DoubleProgression", 2.5),
            db.classification(BENCH_PRESS_ID),
        )
        assertEquals(
            Classification("Isolation", "RepsOnly", 5.0),
            db.classification(FACE_PULL_ID),
        )
        assertEquals(
            Classification("Isolation", "RepsOnly", 2.0),
            db.classification(SIDE_ARM_RAISE_ID),
        )
    }

    @Test
    fun `cardio has no role and never gets a load suggestion`() {
        val db = migrate()

        assertEquals(Classification(null, "RepsOnly", null), db.classification(LONG_RUN_ID))
    }

    @Test
    fun `an exercise the user created is left unclassified`() {
        val db = migrate()

        val classification = db.classification(CUSTOM_EXERCISE_ID)
        assertNull(classification.role)
        assertNull(classification.loadIncrement)
        // The column is not nullable, so the SQL default is the only honest answer available.
        assertEquals("DoubleProgression", classification.progressionPolicy)
    }

    @Test
    fun `shoulders become the deltoid head the exercise actually loads`() {
        val db = migrate()

        assertEquals("""["FrontDelts"]""", db.muscles(BENCH_PRESS_ID).secondary)
        assertEquals("""["RearDelts"]""", db.muscles(FACE_PULL_ID).main)
        assertEquals("""["SideDelts"]""", db.muscles(SIDE_ARM_RAISE_ID).main)
    }

    @Test
    fun `shrugs move off the shoulders and onto the traps`() {
        val db = migrate()

        assertEquals("""["Traps"]""", db.muscles(SHRUGS_ID).main)
    }

    @Test
    fun `an exercise the user created falls back to the front delts`() {
        val db = migrate()

        assertEquals(
            Muscles(main = """["FrontDelts"]""", secondary = """["Chest"]""", tertiary = "[]"),
            db.muscles(CUSTOM_EXERCISE_ID),
        )
    }

    @Test
    fun `running the migration twice changes nothing`() {
        val db = migrate()
        val afterFirstRun = db.exerciseRows()

        migration.migrate(db)

        assertEquals(afterFirstRun, db.exerciseRows())
    }

    @Test
    fun `leaves no dangling references`() {
        val db = migrate()

        assertTrue(db.list("PRAGMA foreign_key_check") { it.getString(0) }.isEmpty())
    }

    private fun SupportSQLiteDatabase.insertExercise(
        id: Long,
        name: String,
        type: String,
        main: String,
        secondary: String = "[]",
        tertiary: String = "[]",
    ) {
        execSQL(
            "INSERT INTO exercise (exercise_id, exercise_name, exercise_type, " +
                "exercise_main_muscles, exercise_secondary_muscles, exercise_tertiary_muscles, " +
                "exercise_goal) VALUES (?, ?, ?, ?, ?, ?, '{}')",
            arrayOf<Any?>(id, "\"$name\"", type, main, secondary, tertiary),
        )
    }

    private fun SupportSQLiteDatabase.classification(id: Long): Classification =
        single(
            "SELECT exercise_role, exercise_progression_policy, exercise_load_increment " +
                "FROM exercise WHERE exercise_id = $id"
        ) { cursor ->
            Classification(
                role = cursor.stringOrNull(0),
                progressionPolicy = cursor.getString(1),
                loadIncrement = if (cursor.isNull(2)) null else cursor.getDouble(2),
            )
        }

    private fun SupportSQLiteDatabase.muscles(id: Long): Muscles =
        single(
            "SELECT exercise_main_muscles, exercise_secondary_muscles, " +
                "exercise_tertiary_muscles FROM exercise WHERE exercise_id = $id"
        ) { cursor ->
            Muscles(cursor.getString(0), cursor.getString(1), cursor.getString(2))
        }

    private fun SupportSQLiteDatabase.exerciseRows(): List<List<String?>> =
        list("SELECT * FROM exercise ORDER BY exercise_id") { cursor ->
            List(cursor.columnCount) { index -> cursor.stringOrNull(index) }
        }

    private fun Cursor.stringOrNull(index: Int): String? =
        if (isNull(index)) null else getString(index)

    private fun <T> SupportSQLiteDatabase.single(sql: String, read: (Cursor) -> T): T =
        list(sql, read).single()

    private fun <T> SupportSQLiteDatabase.list(sql: String, read: (Cursor) -> T): List<T> =
        query(sql).use { cursor -> buildList { while (cursor.moveToNext()) add(read(cursor)) } }

    private data class Classification(
        val role: String?,
        val progressionPolicy: String,
        val loadIncrement: Double?,
    )

    private data class Muscles(val main: String, val secondary: String, val tertiary: String)

    private companion object {
        const val DB_NAME = "classification-migration-test"
        const val BENCH_PRESS_ID = 6L
        const val FACE_PULL_ID = 40L
        const val SIDE_ARM_RAISE_ID = 45L
        const val SHRUGS_ID = 90L
        const val LONG_RUN_ID = 12L
        const val CUSTOM_EXERCISE_ID = 1_600_000_000_000L
    }
}
