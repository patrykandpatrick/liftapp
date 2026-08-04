package com.patrykandpatrick.liftapp.functionality.database.migration

import android.database.Cursor
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.functionality.database.bodymeasurement.DefaultBodyMeasurements
import com.patrykandpatrick.liftapp.functionality.database.exercise.DefaultExercises
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Converts a published-app database (schema 11) into the rewrite's schema.
 *
 * The old rows are read into memory first, because the two schemas reuse table names that differ
 * only in case (`Exercise` vs `exercise`), which SQLite does not distinguish. The old tables are
 * then dropped, the new schema is created exactly as Room expects it, the default catalogs are
 * seeded the way a fresh install would seed them — the published app used the same IDs for its
 * built-in rows — and the old data is replayed on top through [LegacyDatabaseWriter], the same code
 * path a legacy backup import takes.
 *
 * [legacyPlan] supplies the training plan recreated from the published app's `plan_ids` preference,
 * which lives outside the database; null means there is nothing to carry over. The plan's schedule
 * starts at [startDate] and covers [Constants.TrainingPlan.DEFAULT_CYCLE_COUNT] cycles, matching
 * the active-plan preference written by the preference migration. It picks up where the published
 * app's cycle left off rather than at the first item; see [scheduleRotation].
 */
class Migration11To12(
    private val json: Json,
    private val legacyPlan: () -> LegacyPlan?,
    private val startDate: () -> LocalDate = LocalDate::now,
) : Migration(11, 12) {

    /** The plan to recreate: [name] is localized, [legacyIDs] is the raw `plan_ids` list. */
    data class LegacyPlan(val name: String, val legacyIDs: List<Long>)

    override fun migrate(db: SupportSQLiteDatabase) {
        val exercises = readExercises(db)
        val models = readModels(db).associateBy { it.id }
        val goals = readGoals(db)
        val routines = readRoutines(db)
        val workouts = readWorkouts(db)
        val records = readRecords(db).associateBy { it.id }
        val bodyRecords = readBodyRecords(db)

        LEGACY_TABLES.forEach { table -> db.execSQL("DROP TABLE IF EXISTS `$table`") }
        NEW_SCHEMA.forEach(db::execSQL)
        db.createPlanItemScheduleIndexes()

        seedDefaults(db)

        val writer = LegacyDatabaseWriter(json)
        val exerciseIDs = { modelID: Long -> models[modelID]?.exerciseIDs.orEmpty() }
        exercises.distinctBy { it.id }.forEach { writer.restoreExercise(db, it) }
        routines.distinctBy { it.id }.forEach { writer.restoreRoutine(db, it, exerciseIDs, goals) }
        workouts
            .distinctBy { it.id }
            .forEach { writer.restoreWorkout(db, it, exerciseIDs, goals, records) }
        bodyRecords.forEach { writer.restoreBodyRecord(db, it) }

        val plan = legacyPlan()
        if (plan != null && plan.legacyIDs.isNotEmpty()) {
            val items = writer.writePlan(db, Constants.LegacyApp.PLAN_ID, plan.name, plan.legacyIDs)
            writeSchedule(db, items, scheduleRotation(plan.legacyIDs, items, workouts))
        }
    }

    /**
     * Materializes the schedule the way activating a plan would — one row per day, null routine for
     * a rest day, repeated for the default cycle count — except that the cycle starts at [rotation]
     * instead of the first item. The plan definition keeps the user's order; only the dates shift.
     */
    private fun writeSchedule(db: SupportSQLiteDatabase, items: List<Long?>, rotation: Int) {
        var date = startDate()
        repeat(Constants.TrainingPlan.DEFAULT_CYCLE_COUNT * items.size) { slot ->
            db.execSQL(
                "INSERT INTO plan_item_schedule (plan_item_schedule_plan_id, " +
                    "plan_item_routine_id, plan_item_schedule_date) VALUES (?, ?, ?)",
                arrayOf<Any?>(
                    Constants.LegacyApp.PLAN_ID,
                    items[(rotation + slot) % items.size],
                    date.toString(),
                ),
            )
            date = date.plusDays(1)
        }
    }

    /**
     * The cycle position the published app would show today, so the schedule resumes there rather
     * than starting over. The published app anchored the cycle to the last completed workout:
     * starting from that routine's slot in `plan_ids`, the position advanced one step per elapsed
     * calendar day but paused at the next workout slot until it was done. Without a completed
     * workout, or when its routine is no longer in the plan — the published app bailed out there
     * too — the schedule starts at the first item.
     */
    private fun scheduleRotation(
        legacyIDs: List<Long>,
        items: List<Long?>,
        workouts: List<LegacyWorkout>,
    ): Int {
        val lastCompleted = workouts.filter { it.endTime > 0 }.maxByOrNull { it.id } ?: return 0
        val anchor = legacyIDs.indexOf(lastCompleted.routineID)
        if (anchor < 0) return 0
        val lastDate =
            Instant.ofEpochMilli(lastCompleted.endTime).atZone(ZoneId.systemDefault()).toLocalDate()
        val elapsedDays = ChronoUnit.DAYS.between(lastDate, startDate()).coerceAtLeast(0)
        var position = anchor
        repeat(elapsedDays.toInt()) {
            position++
            if (items[position % items.size] != null) return position % items.size
        }
        return position % items.size
    }

    /** Inserts the same catalogs a fresh install gets from `DatabaseCallback.onCreate`. */
    private fun seedDefaults(db: SupportSQLiteDatabase) {
        DefaultBodyMeasurements.bodyMeasurements.forEachIndexed { index, measurement ->
            db.execSQL(
                "INSERT INTO body_measurements (id, name, type) VALUES (?, ?, ?)",
                arrayOf<Any?>(
                    index + 1L,
                    json.encodeToString<Name>(measurement.name),
                    measurement.type.name,
                ),
            )
        }
        DefaultExercises.exercises.forEachIndexed { index, exercise ->
            db.execSQL(
                "INSERT INTO exercise (exercise_id, exercise_name, exercise_type, " +
                    "exercise_main_muscles, exercise_secondary_muscles, " +
                    "exercise_tertiary_muscles, exercise_goal) VALUES (?, ?, ?, ?, ?, ?, ?)",
                arrayOf<Any?>(
                    index + 1L,
                    json.encodeToString<Name>(exercise.name),
                    exercise.exerciseType.name,
                    json.encodeToString(exercise.mainMuscles),
                    json.encodeToString(exercise.secondaryMuscles),
                    json.encodeToString(exercise.tertiaryMuscles),
                    defaultGoalJson,
                ),
            )
        }
    }

    private fun readExercises(db: SupportSQLiteDatabase): List<LegacyExercise> =
        db.readRows("Exercise") { cursor ->
            LegacyExercise(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("exercise_id")),
                type = cursor.text("exercise_type"),
                name = cursor.text("exercise_name"),
                goal = cursor.text("exercise_goal"),
                mainMuscles = legacyWords(cursor.text("exercise_main_muscles")),
                secondaryMuscles = legacyWords(cursor.text("exercise_secondary_muscles")),
                tertiaryMuscles = legacyWords(cursor.text("exercise_tertiary_muscles")),
            )
        }

    private fun readModels(db: SupportSQLiteDatabase): List<LegacyModel> =
        db.readRows("SuperExerciseModel") { cursor ->
            LegacyModel(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                exerciseIDs = legacyLongs(cursor.text("exercise_ids")),
            )
        }

    private fun readGoals(db: SupportSQLiteDatabase): List<LegacyExerciseGoal> =
        db.readRows("ExerciseGoal") { cursor ->
            val range =
                cursor
                    .text("rep_range")
                    .filterNot { it == ' ' }
                    .split("..")
                    .mapNotNull(String::toIntOrNull)
            LegacyExerciseGoal(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                routineID = cursor.getLong(cursor.getColumnIndexOrThrow("routine_id")),
                modelID = cursor.getLong(cursor.getColumnIndexOrThrow("exercise_id")),
                sets = cursor.getInt(cursor.getColumnIndexOrThrow("set_count")),
                restSeconds = cursor.getInt(cursor.getColumnIndexOrThrow("break_time")),
                minReps = range.getOrElse(0) { 0 },
                maxReps = range.getOrElse(1) { range.getOrElse(0) { 0 } },
            )
        }

    private fun readRoutines(db: SupportSQLiteDatabase): List<LegacyRoutine> =
        db.readRows("Routine") { cursor ->
            LegacyRoutine(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.text("name"),
                modelIDs = legacyLongs(cursor.text("exercise_ids")),
                order = cursor.getInt(cursor.getColumnIndexOrThrow("order_number")),
            )
        }

    private fun readWorkouts(db: SupportSQLiteDatabase): List<LegacyWorkout> =
        db.readRows("RoutineRecord") { cursor ->
            LegacyWorkout(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                endTime = cursor.getLong(cursor.getColumnIndexOrThrow("end_time")),
                routineID = cursor.getLong(cursor.getColumnIndexOrThrow("routine_id")),
                name = cursor.text("name"),
                modelIDs = legacyLongs(cursor.text("exercise_ids")),
                recordIDs = legacyLongs(cursor.text("record_ids")),
            )
        }

    private fun readRecords(db: SupportSQLiteDatabase): List<LegacyExerciseRecord> =
        db.readRows("ExerciseRecord") { cursor ->
            LegacyExerciseRecord(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("ex_record_id")),
                exerciseID = cursor.getLong(cursor.getColumnIndexOrThrow("ex_record_exercise_id")),
                sets = parseLegacySets(cursor.text("ex_records")),
                comment = cursor.text("ex_comment"),
            )
        }

    private fun readBodyRecords(db: SupportSQLiteDatabase): List<LegacyBodyRecord> =
        db.readRows("BodyRecord") { cursor ->
            LegacyBodyRecord(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("r_id")),
                measurementID = cursor.getLong(cursor.getColumnIndexOrThrow("r_m_id")),
                left = cursor.getDouble(cursor.getColumnIndexOrThrow("r_value_left")),
                right = cursor.getDouble(cursor.getColumnIndexOrThrow("r_value_right")),
                unit = cursor.text("r_unit"),
            )
        }

    private fun <T> SupportSQLiteDatabase.readRows(
        table: String,
        row: (Cursor) -> T,
    ): List<T> =
        query("SELECT * FROM `$table`").use { cursor ->
            buildList { while (cursor.moveToNext()) add(row(cursor)) }
        }

    private fun Cursor.text(column: String): String {
        val index = getColumnIndexOrThrow(column)
        return if (isNull(index)) "" else getString(index)
    }

    private val defaultGoalJson by lazy { json.encodeToString(Goal.default) }

    private companion object {

        val LEGACY_TABLES =
            listOf(
                "ExerciseRecord",
                "ExerciseGoal",
                "SuperExerciseModel",
                "RoutineRecord",
                "Routine",
                "Exercise",
                "BodyRecord",
                "Body",
                "ChangelogItem",
            )

        /**
         * The rewrite's schema, verbatim from the exported `12.json`. Room validates the identity
         * hash after the migration, so this must not drift from the entity declarations.
         */
        val NEW_SCHEMA =
            listOf(
                "CREATE TABLE IF NOT EXISTS `body_measurements` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL)",
                "CREATE TABLE IF NOT EXISTS `body_measurement_entries` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `body_measurement_id` INTEGER NOT NULL, `value` TEXT NOT NULL, `time` TEXT NOT NULL, FOREIGN KEY(`body_measurement_id`) REFERENCES `body_measurements`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE INDEX IF NOT EXISTS `index_body_measurement_entries_id` ON `body_measurement_entries` (`id`)",
                "CREATE INDEX IF NOT EXISTS `index_body_measurement_entries_body_measurement_id` ON `body_measurement_entries` (`body_measurement_id`)",
                "CREATE INDEX IF NOT EXISTS `index_body_measurement_entries_time` ON `body_measurement_entries` (`time`)",
                "CREATE TABLE IF NOT EXISTS `exercise` (`exercise_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exercise_name` TEXT NOT NULL, `exercise_type` TEXT NOT NULL, `exercise_main_muscles` TEXT NOT NULL, `exercise_secondary_muscles` TEXT NOT NULL, `exercise_tertiary_muscles` TEXT NOT NULL, `exercise_goal` TEXT NOT NULL)",
                "CREATE INDEX IF NOT EXISTS `index_exercise_exercise_name` ON `exercise` (`exercise_name`)",
                "CREATE INDEX IF NOT EXISTS `index_exercise_exercise_type` ON `exercise` (`exercise_type`)",
                "CREATE INDEX IF NOT EXISTS `index_exercise_exercise_main_muscles` ON `exercise` (`exercise_main_muscles`)",
                "CREATE TABLE IF NOT EXISTS `routine` (`routine_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routine_name` TEXT NOT NULL, `routine_order_index` INTEGER NOT NULL DEFAULT 0)",
                "CREATE TABLE IF NOT EXISTS `routine_item` (`routine_item_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routine_item_routine_id` INTEGER NOT NULL, `routine_item_order_index` INTEGER NOT NULL, `routine_item_type` TEXT NOT NULL, FOREIGN KEY(`routine_item_routine_id`) REFERENCES `routine`(`routine_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE INDEX IF NOT EXISTS `index_routine_item_routine_item_routine_id` ON `routine_item` (`routine_item_routine_id`)",
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_routine_item_routine_item_routine_id_routine_item_order_index` ON `routine_item` (`routine_item_routine_id`, `routine_item_order_index`)",
                "CREATE TABLE IF NOT EXISTS `exercise_with_routine_item` (`routine_item_id` INTEGER NOT NULL, `exercise_id` INTEGER NOT NULL, `routine_item_exercise_order_index` INTEGER NOT NULL, PRIMARY KEY(`routine_item_id`, `exercise_id`), FOREIGN KEY(`routine_item_id`) REFERENCES `routine_item`(`routine_item_id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exercise_id`) REFERENCES `exercise`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE INDEX IF NOT EXISTS `index_exercise_with_routine_item_exercise_id` ON `exercise_with_routine_item` (`exercise_id`)",
                "CREATE TABLE IF NOT EXISTS `superset` (`superset_routine_item_id` INTEGER NOT NULL, `superset_sets` INTEGER NOT NULL, `superset_rest_time_millis` INTEGER NOT NULL, PRIMARY KEY(`superset_routine_item_id`), FOREIGN KEY(`superset_routine_item_id`) REFERENCES `routine_item`(`routine_item_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE TABLE IF NOT EXISTS `goal` (`goal_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `goal_routine_id` INTEGER NOT NULL, `goal_exercise_id` INTEGER NOT NULL, `goal_min_reps` INTEGER NOT NULL, `goal_max_reps` INTEGER NOT NULL, `goal_sets` INTEGER NOT NULL, `goal_rest_time` INTEGER NOT NULL, `goal_duration_millis` INTEGER NOT NULL, `goal_distance` REAL NOT NULL, `goal_distance_unit` TEXT NOT NULL, `goal_calories` REAL NOT NULL, FOREIGN KEY(`goal_routine_id`) REFERENCES `routine`(`routine_id`) ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`goal_exercise_id`) REFERENCES `exercise`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)",
                "CREATE INDEX IF NOT EXISTS `index_goal_goal_routine_id` ON `goal` (`goal_routine_id`)",
                "CREATE INDEX IF NOT EXISTS `index_goal_goal_exercise_id` ON `goal` (`goal_exercise_id`)",
                "CREATE TABLE IF NOT EXISTS `workout` (`workout_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `workout_routine_id` INTEGER NOT NULL, `workout_name` TEXT NOT NULL, `workout_start_date` TEXT NOT NULL, `workout_end_date` TEXT, `workout_notes` TEXT NOT NULL, `workout_body_weight` TEXT, FOREIGN KEY(`workout_routine_id`) REFERENCES `routine`(`routine_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE INDEX IF NOT EXISTS `index_workout_workout_routine_id` ON `workout` (`workout_routine_id`)",
                "CREATE TABLE IF NOT EXISTS `workout_item` (`workout_item_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `workout_item_workout_id` INTEGER NOT NULL, `workout_item_order_index` INTEGER NOT NULL, `workout_item_type` TEXT NOT NULL, `workout_item_sets` INTEGER, `workout_item_rest_time_millis` INTEGER, FOREIGN KEY(`workout_item_workout_id`) REFERENCES `workout`(`workout_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE INDEX IF NOT EXISTS `index_workout_item_workout_item_workout_id` ON `workout_item` (`workout_item_workout_id`)",
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_workout_item_workout_item_workout_id_workout_item_order_index` ON `workout_item` (`workout_item_workout_id`, `workout_item_order_index`)",
                "CREATE TABLE IF NOT EXISTS `exercise_with_workout_item` (`workout_item_id` INTEGER NOT NULL, `exercise_id` INTEGER NOT NULL, `workout_item_exercise_order_index` INTEGER NOT NULL, `workout_item_exercise_notes` TEXT NOT NULL, PRIMARY KEY(`workout_item_id`, `exercise_id`), FOREIGN KEY(`workout_item_id`) REFERENCES `workout_item`(`workout_item_id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exercise_id`) REFERENCES `exercise`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE INDEX IF NOT EXISTS `index_exercise_with_workout_item_exercise_id` ON `exercise_with_workout_item` (`exercise_id`)",
                "CREATE TABLE IF NOT EXISTS `workout_goal` (`workout_goal_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `workout_goal_workout_id` INTEGER NOT NULL, `workout_goal_exercise_id` INTEGER NOT NULL, `workout_goal_min_reps` INTEGER NOT NULL, `workout_goal_max_reps` INTEGER NOT NULL, `workout_goal_sets` INTEGER NOT NULL, `workout_goal_rest_time` INTEGER NOT NULL, `workout_goal_duration_millis` INTEGER NOT NULL, `workout_goal_distance` REAL NOT NULL, `workout_goal_distance_unit` TEXT NOT NULL, `workout_goal_calories` REAL NOT NULL, FOREIGN KEY(`workout_goal_workout_id`) REFERENCES `workout`(`workout_id`) ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, FOREIGN KEY(`workout_goal_exercise_id`) REFERENCES `exercise`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)",
                "CREATE INDEX IF NOT EXISTS `index_workout_goal_workout_goal_workout_id` ON `workout_goal` (`workout_goal_workout_id`)",
                "CREATE INDEX IF NOT EXISTS `index_workout_goal_workout_goal_exercise_id` ON `workout_goal` (`workout_goal_exercise_id`)",
                "CREATE TABLE IF NOT EXISTS `exercise_set` (`exercise_set_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `exercise_set_workout_id` INTEGER NOT NULL, `exercise_set_exercise_id` INTEGER NOT NULL, `exercise_set_weight` REAL, `exercise_set_weight_unit` TEXT, `exercise_set_reps` INTEGER, `exercise_set_time` INTEGER, `exercise_set_distance` REAL, `exercise_set_distance_unit` TEXT, `exercise_set_kcal` REAL, `exercise_set_notes` TEXT NOT NULL, `workout_exercise_set_index` INTEGER NOT NULL, FOREIGN KEY(`exercise_set_workout_id`) REFERENCES `workout`(`workout_id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`exercise_set_exercise_id`) REFERENCES `exercise`(`exercise_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE INDEX IF NOT EXISTS `index_exercise_set_exercise_set_workout_id` ON `exercise_set` (`exercise_set_workout_id`)",
                "CREATE INDEX IF NOT EXISTS `index_exercise_set_exercise_set_exercise_id` ON `exercise_set` (`exercise_set_exercise_id`)",
                "CREATE TABLE IF NOT EXISTS `plan` (`plan_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `plan_name` TEXT NOT NULL, `plan_description` TEXT NOT NULL, `plan_item_count` INTEGER NOT NULL)",
                "CREATE TABLE IF NOT EXISTS `plan_item` (`plan_item_plan_id` INTEGER NOT NULL, `plan_item_routine_id` INTEGER NOT NULL, `plan_item_order_index` INTEGER NOT NULL, PRIMARY KEY(`plan_item_plan_id`, `plan_item_order_index`), FOREIGN KEY(`plan_item_plan_id`) REFERENCES `plan`(`plan_id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`plan_item_routine_id`) REFERENCES `routine`(`routine_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                "CREATE INDEX IF NOT EXISTS `index_plan_item_plan_item_plan_id` ON `plan_item` (`plan_item_plan_id`)",
                "CREATE INDEX IF NOT EXISTS `index_plan_item_plan_item_routine_id` ON `plan_item` (`plan_item_routine_id`)",
                "CREATE TABLE IF NOT EXISTS `plan_item_schedule` (`plan_item_schedule_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `plan_item_schedule_plan_id` INTEGER NOT NULL, `plan_item_routine_id` INTEGER, `plan_item_schedule_date` TEXT NOT NULL, FOREIGN KEY(`plan_item_routine_id`) REFERENCES `routine`(`routine_id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`plan_item_schedule_plan_id`) REFERENCES `plan`(`plan_id`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                """CREATE VIEW `body_measurements_with_latest_entries` AS SELECT body_measurements.*,
                      latest_body_measurement_entry.id AS bme_id,
                      latest_body_measurement_entry.body_measurement_id as bme_body_measurement_id,
                      latest_body_measurement_entry.value as bme_value,
                      latest_body_measurement_entry.time as bme_time
                 FROM body_measurements
                      LEFT JOIN (SELECT *
                                   FROM body_measurement_entries AS bme1
                                  WHERE bme1.time IN (SELECT MAX(bme2.time)
                                                             FROM body_measurement_entries AS bme2
                                                             WHERE bme2.body_measurement_id = bme1.body_measurement_id
                                                            GROUP BY bme2.body_measurement_id)
                                  GROUP BY bme1.body_measurement_id
                                  ORDER BY bme1.id DESC) AS latest_body_measurement_entry
                             ON body_measurements.id = latest_body_measurement_entry.body_measurement_id""",
                "CREATE VIEW `routine_with_exercise_names` AS SELECT routine_id, routine_name, routine_order_index, COALESCE(GROUP_CONCAT(exercise_name, ', '), '') as exercise_names FROM (SELECT routine.*, exercise.exercise_name, item.routine_item_order_index, membership.routine_item_exercise_order_index FROM routine LEFT JOIN routine_item item ON routine.routine_id = item.routine_item_routine_id LEFT JOIN exercise_with_routine_item membership ON membership.routine_item_id = item.routine_item_id LEFT JOIN exercise ON exercise.exercise_id = membership.exercise_id ORDER BY item.routine_item_routine_id, item.routine_item_order_index, membership.routine_item_exercise_order_index) GROUP BY routine_id",
            )
    }
}
