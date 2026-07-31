package com.patrykandpatrick.liftapp.functionality.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * The migrations the published app shipped, carried over verbatim. Android may hand this app a
 * database at any version the published app supported — an update in place delivers 11, but an
 * auto-backup restored onto a new device can be as old as 6 — and every such database has to reach
 * 11 before [Migration11To12] converts it. Versions below 6 had no migration path in the published
 * app either.
 */
object PublishedMigrations {

    /** `TrainingPlan` became `Sequence`, `PlanRecord` became `SequenceRecord`. */
    val MIGRATION_6_7: Migration =
        object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE Sequence (id INTEGER PRIMARY KEY NOT NULL,name TEXT NOT NULL," +
                        "exercise_ids Text NOT NULL,order_number INTEGER NOT NULL )"
                )
                db.execSQL("INSERT INTO Sequence SELECT * FROM TrainingPlan")
                db.execSQL("DROP TABLE TrainingPlan")
                db.execSQL("DROP INDEX index_PlanRecord_record_plan_id")
                db.execSQL(
                    "CREATE TABLE SequenceRecord (id INTEGER PRIMARY KEY NOT NULL," +
                        "end_time INTEGER NOT NULL,plan_id INTEGER NOT NULL,name TEXT NOT NULL," +
                        "exercise_ids Text NOT NULL,record_ids TEXT NOT NULL)"
                )
                db.execSQL("CREATE INDEX index_SequenceRecord_plan_id ON SequenceRecord (plan_id)")
                db.execSQL("INSERT INTO SequenceRecord SELECT * FROM PlanRecord")
                db.execSQL("DROP TABLE PlanRecord")
            }
        }

    /** `Sequence` became `Routine`, `SequenceRecord` became `RoutineRecord`. */
    val MIGRATION_7_8: Migration =
        object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE Routine (id INTEGER PRIMARY KEY NOT NULL,name TEXT NOT NULL," +
                        "exercise_ids Text NOT NULL,order_number INTEGER NOT NULL )"
                )
                db.execSQL("INSERT INTO Routine SELECT * FROM Sequence")
                db.execSQL("DROP TABLE Sequence")
                db.execSQL("DROP INDEX index_SequenceRecord_plan_id")
                db.execSQL(
                    "CREATE TABLE RoutineRecord (id INTEGER PRIMARY KEY NOT NULL," +
                        "end_time INTEGER NOT NULL,routine_id INTEGER NOT NULL,name TEXT NOT NULL," +
                        "exercise_ids Text NOT NULL,record_ids TEXT NOT NULL)"
                )
                db.execSQL("CREATE INDEX index_RoutineRecord_plan_id ON RoutineRecord (routine_id)")
                db.execSQL("INSERT INTO RoutineRecord SELECT * FROM SequenceRecord")
                db.execSQL("DROP TABLE SequenceRecord")
            }
        }

    /** The published shortcut straight from 6 to 8, skipping the `Sequence` naming. */
    val MIGRATION_6_8: Migration =
        object : Migration(6, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE Routine (id INTEGER PRIMARY KEY NOT NULL,name TEXT NOT NULL," +
                        "exercise_ids Text NOT NULL,order_number INTEGER NOT NULL )"
                )
                db.execSQL("INSERT INTO Routine SELECT * FROM TrainingPlan")
                db.execSQL("DROP TABLE TrainingPlan")
                db.execSQL("DROP INDEX index_PlanRecord_record_plan_id")
                db.execSQL(
                    "CREATE TABLE RoutineRecord (id INTEGER PRIMARY KEY NOT NULL," +
                        "end_time INTEGER NOT NULL,routine_id INTEGER NOT NULL,name TEXT NOT NULL," +
                        "exercise_ids Text NOT NULL,record_ids TEXT NOT NULL)"
                )
                db.execSQL("CREATE INDEX index_RoutineRecord_plan_id ON RoutineRecord (routine_id)")
                db.execSQL("INSERT INTO RoutineRecord SELECT * FROM PlanRecord")
                db.execSQL("DROP TABLE PlanRecord")
            }
        }

    /** Added the changelog cache. */
    val MIGRATION_8_9: Migration =
        object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE ChangelogItem (id INTEGER PRIMARY KEY NOT NULL," +
                        "rolloutDate TEXT NOT NULL,releaseName TEXT NOT NULL,content TEXT NOT NULL )"
                )
            }
        }

    /** Added routine strategies and per-routine exercise goals. */
    val MIGRATION_9_10: Migration =
        object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE Routine ADD COLUMN strategy TEXT NOT NULL DEFAULT 'NORMAL'")
                db.execSQL(
                    "CREATE TABLE ExerciseGoal " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "routine_id INTEGER NOT NULL," +
                        "exercise_id INTEGER NOT NULL DEFAULT '0'," +
                        "set_count INTEGER NOT NULL," +
                        "break_time INTEGER NOT NULL," +
                        "rep_range TEXT NOT NULL DEFAULT '0..0' " +
                        ")"
                )
                db.execSQL(
                    "CREATE INDEX index_ExerciseGoal_routine_id ON ExerciseGoal (routine_id)"
                )
            }
        }

    /**
     * Moved exercise lists behind `SuperExerciseModel`. Before 11, `Routine.exercise_ids` and
     * `RoutineRecord.exercise_ids` held raw `Exercise` IDs, with `&` joining the members of a
     * superset (`"12 34&35 40"`); this replaces each token with a model row holding the IDs.
     * Routines always get fresh models; records reuse a model whose ID list matches exactly.
     */
    val MIGRATION_10_11: Migration =
        object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE SuperExerciseModel " +
                        "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        "exercise_ids TEXT NOT NULL DEFAULT '')"
                )
                var availableModelID = availableModelID(db)
                readIDStrings(db, "Routine").forEach { (routineID, exerciseInfo) ->
                    val modelIDs =
                        exerciseInfo.split(" ").map { token ->
                            val modelID = availableModelID++
                            insertModel(db, modelID, token)
                            modelID
                        }
                    updateIDString(db, "Routine", routineID, modelIDs)
                }
                readIDStrings(db, "RoutineRecord").forEach { (recordID, exerciseInfo) ->
                    val modelIDs =
                        exerciseInfo.split(" ").map { token ->
                            findModel(db, token)
                                ?: run {
                                    val modelID = availableModelID++
                                    insertModel(db, modelID, token)
                                    modelID
                                }
                        }
                    updateIDString(db, "RoutineRecord", recordID, modelIDs)
                }
            }

            private fun readIDStrings(
                db: SupportSQLiteDatabase,
                table: String,
            ): List<Pair<Long, String>> =
                db.query("SELECT id, exercise_ids FROM $table").use { cursor ->
                    buildList {
                        while (cursor.moveToNext()) {
                            add(cursor.getLong(0) to cursor.getString(1))
                        }
                    }
                }

            private fun availableModelID(db: SupportSQLiteDatabase): Long =
                db.query("SELECT id FROM SuperExerciseModel ORDER BY id DESC LIMIT 1").use {
                    if (it.moveToNext()) it.getLong(0) + 1 else 0L
                }

            /** A token is one exercise ID, or several joined with `&` for a superset. */
            private fun insertModel(db: SupportSQLiteDatabase, modelID: Long, token: String) {
                val exerciseIDs = token.split("&").joinToString(" ") { it.toLongOrNull().toSafe() }
                db.execSQL(
                    "INSERT INTO SuperExerciseModel (id, exercise_ids) VALUES (?, ?)",
                    arrayOf<Any?>(modelID, exerciseIDs),
                )
            }

            private fun findModel(db: SupportSQLiteDatabase, token: String): Long? =
                db.query(
                        "SELECT id FROM SuperExerciseModel WHERE exercise_ids = ? LIMIT 1",
                        arrayOf<Any?>(token.replace("&", " ")),
                    )
                    .use { if (it.moveToNext()) it.getLong(0) else null }

            private fun updateIDString(
                db: SupportSQLiteDatabase,
                table: String,
                rowID: Long,
                modelIDs: List<Long>,
            ) {
                db.execSQL(
                    "UPDATE $table SET exercise_ids = ? WHERE id = ?",
                    arrayOf<Any?>(modelIDs.joinToString(" "), rowID),
                )
            }

            private fun Long?.toSafe(): String = (this ?: 0L).toString()
        }
}
