package com.patrykandpatrick.liftapp.functionality.database.migration

import android.database.Cursor
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.PercentageUnit
import com.patrykandpatrick.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatrick.liftapp.functionality.database.Database
import com.patrykandpatrick.liftapp.functionality.database.di.DatabaseModule
import com.patrykandpatrick.liftapp.functionality.database.string.BodyMeasurementStringResource
import com.patrykandpatrick.liftapp.functionality.database.string.ExerciseStringResource
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Replays a database the published app could leave behind — version 11, created from the vendored
 * `11.json` the published app exported — through [Migration11To12] and checks the outcome against
 * the rewrite's schema and conversions.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class Migration11To12Test {

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

    private val startDate: LocalDate = LocalDate.of(2026, 7, 31)

    private fun migration(
        planIDs: List<Long>? = listOf(100, -1, 101, -2),
        start: LocalDate = startDate,
    ) =
        Migration11To12(
            json = json,
            legacyPlan = { planIDs?.let { Migration11To12.LegacyPlan("My training plan", it) } },
            startDate = { start },
        )

    private fun createLegacyDatabase(): SupportSQLiteDatabase =
        helper.createDatabase(DB_NAME, 11).apply {
            // A subset of the built-in catalog plus one user-created exercise. The published app
            // stored resource keys for built-ins and free text for custom rows.
            execSQL(
                "INSERT INTO Exercise (exercise_id, exercise_type, exercise_name, exercise_goal, " +
                    "exercise_main_muscles, exercise_secondary_muscles, exercise_tertiary_muscles) " +
                    "VALUES (6, 'WEIGHT', 'ex_flat_bench_press', '4 3 12 180', 'CHEST TRICEPS', " +
                    "'SHOULDERS', 'FOREARMS ABS LATS')"
            )
            execSQL(
                "INSERT INTO Exercise (exercise_id, exercise_type, exercise_name, exercise_goal, " +
                    "exercise_main_muscles, exercise_secondary_muscles, exercise_tertiary_muscles) " +
                    "VALUES (2, 'CALISTHENICS', 'ex_pull_ups', '5 4 12 120', 'LATS BICEPS', " +
                    "'SHOULDERS FOREARMS', 'ABS')"
            )
            execSQL(
                "INSERT INTO Exercise (exercise_id, exercise_type, exercise_name, exercise_goal, " +
                    "exercise_main_muscles, exercise_secondary_muscles, exercise_tertiary_muscles) " +
                    "VALUES (12, 'CARDIO', 'ex_long_run', '1 0 0 120', 'HAMSTRINGS CALVES', " +
                    "'QUADRICEPS GLUTES', NULL)"
            )
            execSQL(
                "INSERT INTO Exercise (exercise_id, exercise_type, exercise_name, exercise_goal, " +
                    "exercise_main_muscles, exercise_secondary_muscles, exercise_tertiary_muscles) " +
                    "VALUES ($CUSTOM_EXERCISE_ID, 'WEIGHT', 'My curls', '3 8 12 60', 'BICEPS', " +
                    "NULL, NULL)"
            )

            execSQL("INSERT INTO SuperExerciseModel (id, exercise_ids) VALUES (1, '6')")
            execSQL(
                "INSERT INTO SuperExerciseModel (id, exercise_ids) " +
                    "VALUES (2, '2 $CUSTOM_EXERCISE_ID')"
            )
            execSQL("INSERT INTO SuperExerciseModel (id, exercise_ids) VALUES (3, '12')")

            execSQL(
                "INSERT INTO Routine (id, name, exercise_ids, order_number, strategy) " +
                    "VALUES (100, 'Push day', '1 2', 0, 'NORMAL')"
            )
            execSQL(
                "INSERT INTO Routine (id, name, exercise_ids, order_number, strategy) " +
                    "VALUES (101, 'Cardio', '3', 1, 'NORMAL')"
            )

            execSQL(
                "INSERT INTO ExerciseGoal (routine_id, exercise_id, set_count, break_time, rep_range) " +
                    "VALUES (100, 1, 5, 180, '3..5')"
            )
            execSQL(
                "INSERT INTO ExerciseGoal (routine_id, exercise_id, set_count, break_time, rep_range) " +
                    "VALUES (100, 2, 3, 90, '8..12')"
            )

            // A finished workout of the push routine, with per-set and per-exercise comments.
            execSQL(
                "INSERT INTO RoutineRecord (id, end_time, routine_id, name, exercise_ids, record_ids) " +
                    "VALUES ($WORKOUT_ID, ${WORKOUT_ID + 3_600_000}, 100, 'Push day', '1 2', " +
                    "'${WORKOUT_ID + 1} ${WORKOUT_ID + 2} ${WORKOUT_ID + 3}')"
            )
            execSQL(
                "INSERT INTO ExerciseRecord (ex_record_id, ex_record_exercise_id, ex_records, ex_comment) " +
                    "VALUES (${WORKOUT_ID + 1}, 6, " +
                    "'WEIGHT kg 5 100 5 102.5 3<comments><c1>Felt heavy</c1></comments>', 'PR attempt')"
            )
            execSQL(
                "INSERT INTO ExerciseRecord (ex_record_id, ex_record_exercise_id, ex_records, ex_comment) " +
                    "VALUES (${WORKOUT_ID + 2}, 2, " +
                    "'CALISTHENICS kg 3 10 80 8<comments></comments>', NULL)"
            )
            execSQL(
                "INSERT INTO ExerciseRecord (ex_record_id, ex_record_exercise_id, ex_records, ex_comment) " +
                    "VALUES (${WORKOUT_ID + 3}, $CUSTOM_EXERCISE_ID, " +
                    "'WEIGHT lb 3 45 12<comments></comments>', NULL)"
            )

            // A workout that was still running: end_time 0 meant in progress.
            execSQL(
                "INSERT INTO RoutineRecord (id, end_time, routine_id, name, exercise_ids, record_ids) " +
                    "VALUES ($OPEN_WORKOUT_ID, 0, 101, 'Cardio', '3', '${OPEN_WORKOUT_ID + 1}')"
            )
            execSQL(
                "INSERT INTO ExerciseRecord (ex_record_id, ex_record_exercise_id, ex_records, ex_comment) " +
                    "VALUES (${OPEN_WORKOUT_ID + 1}, 12, 'CARDIO km 1 5.5 1800<comments></comments>', NULL)"
            )

            // The measurement catalog the published app seeded (ID 6 was never used), plus records.
            longArrayOf(1, 2, 3, 4, 5, 7, 8, 9, 10, 11).forEach { id ->
                execSQL("INSERT INTO Body (m_id, m_type) VALUES ($id, 'WEIGHT')")
            }
            execSQL(
                "INSERT INTO BodyRecord (r_id, r_m_id, r_value_left, r_value_right, r_unit) " +
                    "VALUES ($BODY_RECORD_ID, 1, 82.5, 0, 'kg')"
            )
            execSQL(
                "INSERT INTO BodyRecord (r_id, r_m_id, r_value_left, r_value_right, r_unit) " +
                    "VALUES (${BODY_RECORD_ID + 1}, 5, 35, 36, 'cm')"
            )
            execSQL(
                "INSERT INTO BodyRecord (r_id, r_m_id, r_value_left, r_value_right, r_unit) " +
                    "VALUES (${BODY_RECORD_ID + 2}, 7, 100, 0, 'in')"
            )
            execSQL(
                "INSERT INTO BodyRecord (r_id, r_m_id, r_value_left, r_value_right, r_unit) " +
                    "VALUES (${BODY_RECORD_ID + 3}, 2, 15.5, 0, '%')"
            )

            execSQL(
                "INSERT INTO ChangelogItem (id, rolloutDate, releaseName, content) " +
                    "VALUES (0, '01.01.2023', '1.9.0', '[]')"
            )
            close()
        }

    private fun migrate(
        planIDs: List<Long>? = listOf(100, -1, 101, -2),
        start: LocalDate = startDate,
    ): SupportSQLiteDatabase {
        createLegacyDatabase()
        return helper.runMigrationsAndValidate(DB_NAME, 12, true, migration(planIDs, start))
    }

    @Test
    fun `converts exercises, keeping resource names for built-ins and raw names for custom rows`() {
        val db = migrate()

        assertEquals(102, db.count("exercise"))
        assertEquals(
            json.encodeToString<Name>(Name.Resource(ExerciseStringResource.FlatBenchPress)),
            db.single("SELECT exercise_name FROM exercise WHERE exercise_id = 6") {
                it.getString(0)
            },
        )
        assertEquals(
            json.encodeToString<Name>(Name.Raw("My curls")),
            db.single(
                "SELECT exercise_name FROM exercise WHERE exercise_id = $CUSTOM_EXERCISE_ID"
            ) {
                it.getString(0)
            },
        )
        val benchGoal =
            json.decodeFromString<Goal>(
                db.single("SELECT exercise_goal FROM exercise WHERE exercise_id = 6") {
                    it.getString(0)
                }
            )
        assertEquals(
            Goal.default.copy(sets = 4, minReps = 3, maxReps = 12, restTime = 180.seconds),
            benchGoal,
        )
        assertEquals(
            "Weight",
            db.single(
                "SELECT exercise_type FROM exercise WHERE exercise_id = $CUSTOM_EXERCISE_ID"
            ) {
                it.getString(0)
            },
        )
    }

    @Test
    fun `converts routines, supersets, and goals`() {
        val db = migrate()

        assertEquals(
            listOf("Push day" to 0, "Cardio" to 1),
            db.list(
                "SELECT routine_name, routine_order_index FROM routine ORDER BY routine_order_index"
            ) {
                it.getString(0) to it.getInt(1)
            },
        )

        val items =
            db.list(
                "SELECT routine_item_id, routine_item_type FROM routine_item " +
                    "WHERE routine_item_routine_id = 100 ORDER BY routine_item_order_index"
            ) {
                it.getLong(0) to it.getString(1)
            }
        assertEquals(listOf("Exercise", "Superset"), items.map { it.second })

        assertEquals(
            listOf(2L, CUSTOM_EXERCISE_ID),
            db.list(
                "SELECT exercise_id FROM exercise_with_routine_item " +
                    "WHERE routine_item_id = ${items[1].first} " +
                    "ORDER BY routine_item_exercise_order_index"
            ) {
                it.getLong(0)
            },
        )
        assertEquals(
            3 to 90_000L,
            db.single(
                "SELECT superset_sets, superset_rest_time_millis FROM superset " +
                    "WHERE superset_routine_item_id = ${items[1].first}"
            ) {
                it.getInt(0) to it.getLong(1)
            },
        )

        assertEquals(
            listOf(6L to 5, 2L to 3, CUSTOM_EXERCISE_ID to 3),
            db.list(
                "SELECT goal_exercise_id, goal_sets FROM goal WHERE goal_routine_id = 100 " +
                    "ORDER BY goal_id"
            ) {
                it.getLong(0) to it.getInt(1)
            },
        )
        assertEquals(
            3 to 5,
            db.single(
                "SELECT goal_min_reps, goal_max_reps FROM goal " +
                    "WHERE goal_routine_id = 100 AND goal_exercise_id = 6"
            ) {
                it.getInt(0) to it.getInt(1)
            },
        )
    }

    @Test
    fun `converts workouts with sets, comments, and the in-progress end date`() {
        val db = migrate()

        assertEquals(
            Triple(date(WORKOUT_ID), date(WORKOUT_ID + 3_600_000), "Push day"),
            db.single(
                "SELECT workout_start_date, workout_end_date, workout_name FROM workout " +
                    "WHERE workout_id = $WORKOUT_ID"
            ) {
                Triple(it.getString(0), it.getString(1), it.getString(2))
            },
        )
        assertNull(
            db.single("SELECT workout_end_date FROM workout WHERE workout_id = $OPEN_WORKOUT_ID") {
                if (it.isNull(0)) null else it.getString(0)
            }
        )

        // "WEIGHT kg 5 100 5 102.5 3" declares five goal sets but carries two performed sets.
        assertEquals(
            listOf(
                Triple(100.0, 5, ""),
                Triple(102.5, 3, "Felt heavy"),
            ),
            db.list(
                "SELECT exercise_set_weight, exercise_set_reps, exercise_set_notes FROM exercise_set " +
                    "WHERE exercise_set_workout_id = $WORKOUT_ID AND exercise_set_exercise_id = 6 " +
                    "ORDER BY workout_exercise_set_index"
            ) {
                Triple(it.getDouble(0), it.getInt(1), it.getString(2))
            },
        )
        assertEquals(
            json.encodeToString<MassUnit>(MassUnit.Pounds),
            db.single(
                "SELECT exercise_set_weight_unit FROM exercise_set " +
                    "WHERE exercise_set_exercise_id = $CUSTOM_EXERCISE_ID"
            ) {
                it.getString(0)
            },
        )
        assertEquals(
            5.5 to 1_800_000L,
            db.single(
                "SELECT exercise_set_distance, exercise_set_time FROM exercise_set " +
                    "WHERE exercise_set_workout_id = $OPEN_WORKOUT_ID"
            ) {
                it.getDouble(0) to it.getLong(1)
            },
        )

        // The calisthenics record carried a body weight, which becomes the workout's body weight.
        assertEquals(
            BodyMeasurementValue.SingleValue(80.0, MassUnit.Kilograms),
            json.decodeFromString<BodyMeasurementValue>(
                db.single(
                    "SELECT workout_body_weight FROM workout WHERE workout_id = $WORKOUT_ID"
                ) {
                    it.getString(0)
                }
            ),
        )
        assertEquals(
            "PR attempt",
            db.single(
                "SELECT workout_item_exercise_notes FROM exercise_with_workout_item " +
                    "WHERE exercise_id = 6"
            ) {
                it.getString(0)
            },
        )
    }

    @Test
    fun `seeds the default catalogs and remaps body measurement entries`() {
        val db = migrate()

        assertEquals(10, db.count("body_measurements"))
        assertEquals(
            json.encodeToString<Name>(Name.Resource(BodyMeasurementStringResource.BodyWeight)),
            db.single("SELECT name FROM body_measurements WHERE id = 1") { it.getString(0) },
        )

        assertEquals(
            BodyMeasurementValue.SingleValue(82.5, MassUnit.Kilograms),
            db.entryValue(BODY_RECORD_ID, expectedMeasurementID = 1),
        )
        assertEquals(
            BodyMeasurementValue.DoubleValue(35.0, 36.0, ShortDistanceUnit.Centimeter),
            db.entryValue(BODY_RECORD_ID + 1, expectedMeasurementID = 5),
        )
        // The published ID 7 (chest) shifts down by one because ID 6 was never used.
        assertEquals(
            BodyMeasurementValue.SingleValue(100.0, ShortDistanceUnit.Inch),
            db.entryValue(BODY_RECORD_ID + 2, expectedMeasurementID = 6),
        )
        assertEquals(
            BodyMeasurementValue.SingleValue(15.5, PercentageUnit),
            db.entryValue(BODY_RECORD_ID + 3, expectedMeasurementID = 2),
        )
    }

    @Test
    fun `recreates the training plan with rest days and a six-cycle schedule`() {
        val db = migrate()

        assertEquals(
            Triple("My training plan", "", 4),
            db.single("SELECT plan_name, plan_description, plan_item_count FROM plan") {
                Triple(it.getString(0), it.getString(1), it.getInt(2))
            },
        )
        assertEquals(
            listOf(0 to 100L, 2 to 101L),
            db.list(
                "SELECT plan_item_order_index, plan_item_routine_id FROM plan_item " +
                    "ORDER BY plan_item_order_index"
            ) {
                it.getInt(0) to it.getLong(1)
            },
        )

        // The last completed workout was the push routine, long ago, so the cycle resumes at the
        // cardio routine — the workout the published app showed as due — not at the first item.
        val schedule = db.schedule()
        assertEquals(24, schedule.size)
        assertEquals(101L to "2026-07-31", schedule[0])
        assertEquals(null to "2026-08-01", schedule[1])
        assertEquals(100L to "2026-08-02", schedule[2])
        assertEquals(null to "2026-08-03", schedule[3])
        assertEquals(101L to "2026-08-04", schedule[4])
        assertEquals("2026-08-23", schedule.last().second)
    }

    @Test
    fun `the schedule starts at the first item when no completed workout anchors it`() {
        // The push routine — the only one ever completed — is not part of this plan.
        val schedule = migrate(planIDs = listOf(101, -1)).schedule()

        assertEquals(101L to "2026-07-31", schedule[0])
        assertEquals(null to "2026-08-01", schedule[1])
    }

    @Test
    fun `a workout completed yesterday leaves today as the rest day that follows it`() {
        val dayAfterLastWorkout =
            LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(WORKOUT_ID + 3_600_000),
                    ZoneId.systemDefault(),
                )
                .toLocalDate()
                .plusDays(1)

        val schedule = migrate(start = dayAfterLastWorkout).schedule()

        assertEquals(null to dayAfterLastWorkout.toString(), schedule[0])
        assertEquals(101L to dayAfterLastWorkout.plusDays(1).toString(), schedule[1])
    }

    @Test
    fun `skips the plan when the published app had none scheduled`() {
        val db = migrate(planIDs = null)

        assertEquals(0, db.count("plan"))
        assertEquals(0, db.count("plan_item_schedule"))
    }

    @Test
    fun `drops plan entries whose routines are gone`() {
        val db = migrate(planIDs = listOf(100, 999, -1))

        assertEquals(3, db.single("SELECT plan_item_count FROM plan") { it.getInt(0) })
        assertEquals(
            listOf(0 to 100L),
            db.list("SELECT plan_item_order_index, plan_item_routine_id FROM plan_item") {
                it.getInt(0) to it.getLong(1)
            },
        )
    }

    @Test
    fun `leaves no dangling references`() {
        val db = migrate()

        db.query("PRAGMA foreign_key_check").use { cursor ->
            assertTrue(!cursor.moveToFirst(), "The migrated database has broken references.")
        }
    }

    private fun SupportSQLiteDatabase.schedule(): List<Pair<Long?, String>> =
        list(
            "SELECT plan_item_routine_id, plan_item_schedule_date FROM plan_item_schedule " +
                "ORDER BY plan_item_schedule_date"
        ) {
            (if (it.isNull(0)) null else it.getLong(0)) to it.getString(1)
        }

    private fun SupportSQLiteDatabase.entryValue(
        id: Long,
        expectedMeasurementID: Long,
    ): BodyMeasurementValue {
        val (measurementID, value) =
            single(
                "SELECT body_measurement_id, value FROM body_measurement_entries WHERE id = $id"
            ) {
                it.getLong(0) to it.getString(1)
            }
        assertEquals(expectedMeasurementID, measurementID)
        return json.decodeFromString(value)
    }

    private fun SupportSQLiteDatabase.count(table: String): Int =
        single("SELECT COUNT(*) FROM $table") { it.getInt(0) }

    private fun <T> SupportSQLiteDatabase.single(sql: String, read: (Cursor) -> T): T =
        list(sql, read).single()

    private fun <T> SupportSQLiteDatabase.list(sql: String, read: (Cursor) -> T): List<T> =
        query(sql).use { cursor -> buildList { while (cursor.moveToNext()) add(read(cursor)) } }

    private fun date(epochMillis: Long): String =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
            .toString()

    private companion object {
        const val DB_NAME = "legacy-migration-test"
        const val CUSTOM_EXERCISE_ID = 1_600_000_000_000L
        const val WORKOUT_ID = 1_700_000_000_000L
        const val OPEN_WORKOUT_ID = 1_700_100_000_000L
        const val BODY_RECORD_ID = 1_690_000_000_000L
    }
}
