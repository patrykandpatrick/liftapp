package com.patrykandpatrick.liftapp.functionality.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.json.Json

/**
 * Gives the per-workout snapshot the same shape as the target it is copied from: a list of sets
 * rather than one rep range and a count.
 *
 * Schema 15 left `workout_goal` flat, which was invisible while every generated set was identical
 * and stopped being true the moment the generator started ramping them. A session logged against a
 * ramped target could not be shown what it was actually asked for, because the row it was reading
 * had already averaged the answer.
 *
 * Existing rows are expanded, not discarded: a flat goal of three sets at 8–12 becomes three
 * identical targets, which is what it always meant.
 *
 * **Every statement is idempotent**, because a fresh chain arrives here with the flat columns
 * already gone.
 */
class Migration15To16(private val json: Json) : Migration(15, 16) {

    override fun migrate(db: SupportSQLiteDatabase) {
        // The new column's presence is what says whether this ran.
        if (SET_TARGETS in db.columnTypes("workout_goal")) return

        val rows = db.readFlatGoals()
        db.rebuildTable()
        rows.forEach { row ->
            db.execSQL(
                "INSERT INTO `workout_goal` (`workout_goal_id`, `workout_goal_workout_id`, " +
                    "`workout_goal_exercise_id`, `workout_goal_set_targets`, " +
                    "`workout_goal_rest_time`, `workout_goal_distance_unit`) VALUES (?, ?, ?, ?, ?, ?)",
                arrayOf<Any?>(
                    row.id,
                    row.workoutID,
                    row.exerciseID,
                    json.encodeToString(row.setTargets()),
                    row.restTimeMillis,
                    row.distanceUnit,
                ),
            )
        }
    }

    private fun SupportSQLiteDatabase.readFlatGoals(): List<FlatGoal> =
        query(
                "SELECT `workout_goal_id`, `workout_goal_workout_id`, `workout_goal_exercise_id`, " +
                    "`workout_goal_min_reps`, `workout_goal_max_reps`, `workout_goal_sets`, " +
                    "`workout_goal_rest_time`, `workout_goal_duration_millis`, " +
                    "`workout_goal_distance`, `workout_goal_distance_unit`, `workout_goal_calories` " +
                    "FROM `workout_goal`"
            )
            .use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        add(
                            FlatGoal(
                                id = cursor.getLong(0),
                                workoutID = cursor.getLong(1),
                                exerciseID = cursor.getLong(2),
                                minReps = cursor.getInt(3),
                                maxReps = cursor.getInt(4),
                                sets = cursor.getInt(5),
                                restTimeMillis = cursor.getLong(6),
                                durationMillis = cursor.getLong(7),
                                distance = cursor.getDouble(8),
                                distanceUnit = cursor.getString(9),
                                calories = cursor.getDouble(10),
                            )
                        )
                    }
                }
            }

    private fun SupportSQLiteDatabase.rebuildTable() {
        execSQL("DROP TABLE `workout_goal`")
        execSQL(
            "CREATE TABLE IF NOT EXISTS `workout_goal` (" +
                "`workout_goal_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`workout_goal_workout_id` INTEGER NOT NULL, " +
                "`workout_goal_exercise_id` INTEGER NOT NULL, " +
                "`workout_goal_set_targets` TEXT NOT NULL DEFAULT '[]', " +
                "`workout_goal_rest_time` INTEGER NOT NULL, " +
                "`workout_goal_distance_unit` TEXT NOT NULL, " +
                "FOREIGN KEY(`workout_goal_workout_id`) REFERENCES `workout`(`workout_id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED, " +
                "FOREIGN KEY(`workout_goal_exercise_id`) REFERENCES `exercise`(`exercise_id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE DEFERRABLE INITIALLY DEFERRED)"
        )
        execSQL(
            "CREATE INDEX IF NOT EXISTS `index_workout_goal_workout_goal_workout_id` " +
                "ON `workout_goal` (`workout_goal_workout_id`)"
        )
        execSQL(
            "CREATE INDEX IF NOT EXISTS `index_workout_goal_workout_goal_exercise_id` " +
                "ON `workout_goal` (`workout_goal_exercise_id`)"
        )
    }

    private fun SupportSQLiteDatabase.columnTypes(table: String): Map<String, String> =
        query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            val typeIndex = cursor.getColumnIndexOrThrow("type")
            buildMap {
                while (cursor.moveToNext()) {
                    put(cursor.getString(nameIndex), cursor.getString(typeIndex))
                }
            }
        }

    private class FlatGoal(
        val id: Long,
        val workoutID: Long,
        val exerciseID: Long,
        val minReps: Int,
        val maxReps: Int,
        val sets: Int,
        val restTimeMillis: Long,
        val durationMillis: Long,
        val distance: Double,
        val distanceUnit: String,
        val calories: Double,
    ) {
        /** A flat goal said the same thing about every set, so every set repeats it. */
        fun setTargets(): List<SetTarget> =
            List(sets.coerceAtLeast(1)) {
                SetTarget(
                    minReps = minReps,
                    maxReps = maxReps,
                    duration = durationMillis.milliseconds,
                    distance = distance,
                    calories = calories,
                )
            }
    }

    private companion object {
        const val SET_TARGETS = "workout_goal_set_targets"
    }
}
