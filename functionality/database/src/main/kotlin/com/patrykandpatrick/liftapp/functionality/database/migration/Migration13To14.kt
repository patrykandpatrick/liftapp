package com.patrykandpatrick.liftapp.functionality.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.functionality.database.converter.MuscleListSerializer
import com.patrykandpatrick.liftapp.functionality.database.exercise.DefaultExercises
import kotlinx.serialization.json.Json

/**
 * Classifies exercises, and splits `Muscle.Shoulders` into the three deltoid heads.
 *
 * Three columns arrive on `exercise`: a nullable role, a progression policy, and the equipment's
 * smallest load step. A plain SQL `DEFAULT` would have covered the schema change but not the data —
 * every one of the 101 seeded rows on an upgrading device was written at version 12 and would have
 * collapsed onto that single default — so the built-in rows are replayed from [DefaultExercises] by
 * ID. Rows the user created keep a null role: the muscle-count guess is good enough to offer in the
 * editor and not good enough to write behind their back.
 *
 * The same pass rewrites the three muscle JSON columns. Built-in rows take the hand-authored delt
 * assignment from [DefaultExercises]; everything else has `Shoulders` mapped to `FrontDelts`, which
 * is what [MuscleListSerializer.ALIASES] does for archives this migration can never reach.
 *
 * **Every statement here is idempotent**, because a fresh 11 → 12 → 13 → 14 chain seeds the catalog
 * from the same file and arrives with the new values already in place.
 */
class Migration13To14(private val json: Json) : Migration(13, 14) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.addColumns()
        db.replaySeededDefaults()
        db.rewriteRemainingLegacyMuscles()
    }

    private fun SupportSQLiteDatabase.addColumns() {
        val existing = columnNames("exercise")
        if ("exercise_role" !in existing) {
            execSQL("ALTER TABLE `exercise` ADD COLUMN `exercise_role` TEXT")
        }
        if ("exercise_progression_policy" !in existing) {
            execSQL(
                "ALTER TABLE `exercise` ADD COLUMN `exercise_progression_policy` TEXT NOT NULL " +
                    "DEFAULT 'DoubleProgression'"
            )
        }
        if ("exercise_load_increment" !in existing) {
            execSQL("ALTER TABLE `exercise` ADD COLUMN `exercise_load_increment` REAL")
        }
    }

    /**
     * Writes the authored classification and muscle lists onto the built-in rows, which hold IDs 1
     * through 101 in [DefaultExercises] order.
     *
     * The muscle lists are replaced rather than patched, because this is the pass that re-authors
     * them: `Shrugs` moves off the shoulders and onto the traps, where it always belonged. That
     * costs a user any edit they had made to a built-in exercise's muscle lists — the alternative
     * costs every legacy-upgraded device its per-exercise delt assignment, since the published
     * app's annotations overwrite these rows on the way through [Migration11To12].
     */
    private fun SupportSQLiteDatabase.replaySeededDefaults() {
        DefaultExercises.exercises.forEachIndexed { index, exercise ->
            execSQL(
                "UPDATE `exercise` SET `exercise_role` = ?, `exercise_progression_policy` = ?, " +
                    "`exercise_load_increment` = ?, `exercise_main_muscles` = ?, " +
                    "`exercise_secondary_muscles` = ?, `exercise_tertiary_muscles` = ? " +
                    "WHERE `exercise_id` = ?",
                arrayOf<Any?>(
                    exercise.role?.name,
                    exercise.progressionPolicy.name,
                    exercise.loadIncrement,
                    encode(exercise.mainMuscles),
                    encode(exercise.secondaryMuscles),
                    encode(exercise.tertiaryMuscles),
                    index + 1L,
                ),
            )
        }
    }

    /**
     * Everything the replay did not cover: rows the user created, and rows a legacy import wrote
     * over a built-in ID. Decoding through [MuscleListSerializer] resolves `Shoulders` and drops
     * anything else it no longer recognizes; re-encoding writes only current names, which is what
     * makes a second run a no-op.
     */
    private fun SupportSQLiteDatabase.rewriteRemainingLegacyMuscles() {
        val seededIDs = 1L..DefaultExercises.exercises.size.toLong()
        val rows =
            query("SELECT `exercise_id`, $MUSCLE_COLUMNS_SQL FROM `exercise`").use { cursor ->
                buildList {
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(0)
                        if (id in seededIDs) continue
                        val muscles = List(MUSCLE_COLUMNS.size) { cursor.getString(it + 1) }
                        if (muscles.none { it.containsLegacyName() }) continue
                        add(id to muscles)
                    }
                }
            }

        rows.forEach { (id, muscles) ->
            val arguments =
                buildList<Any?> {
                        muscles.forEach { column -> add(encode(decode(column))) }
                        add(id)
                    }
                    .toTypedArray()
            execSQL(
                "UPDATE `exercise` SET " +
                    MUSCLE_COLUMNS.joinToString { "`$it` = ?" } +
                    " WHERE `exercise_id` = ?",
                arguments,
            )
        }
    }

    private fun String.containsLegacyName(): Boolean =
        MuscleListSerializer.ALIASES.keys.any { alias -> contains("\"$alias\"") }

    private fun decode(value: String): List<Muscle> =
        json.decodeFromString(MuscleListSerializer, value)

    private fun encode(muscles: List<Muscle>): String =
        json.encodeToString(MuscleListSerializer, muscles)

    private fun SupportSQLiteDatabase.columnNames(table: String): Set<String> =
        query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            buildSet { while (cursor.moveToNext()) add(cursor.getString(nameIndex)) }
        }

    private companion object {
        val MUSCLE_COLUMNS =
            listOf(
                "exercise_main_muscles",
                "exercise_secondary_muscles",
                "exercise_tertiary_muscles",
            )

        val MUSCLE_COLUMNS_SQL = MUSCLE_COLUMNS.joinToString { "`$it`" }
    }
}
