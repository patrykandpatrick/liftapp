package com.patrykandpatrick.liftapp.functionality.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Adds reps in reserve to a logged set. The column is nullable with no default, because a set
 * logged before this version has no reported effort and `0` would claim it was taken to failure.
 */
object Migration12To13 : Migration(12, 13) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `exercise_set` ADD COLUMN `exercise_set_rir` INTEGER")
    }
}
