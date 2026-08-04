package com.patrykandpatrick.liftapp.functionality.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Development installs of the rewrite created this schema under version 1 before the version was
 * raised past the published app's 11. Only the schedule indexes were added afterward. (A version-1
 * database of the published app also lands here, but the published app itself had no migration path
 * from below 6, so those installs could not update in either app.)
 */
object Migration1To12 : Migration(1, 12) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.createPlanItemScheduleIndexes()
    }
}

internal fun SupportSQLiteDatabase.createPlanItemScheduleIndexes() {
    execSQL(
        "CREATE INDEX IF NOT EXISTS `index_plan_item_schedule_plan_id` " +
            "ON `plan_item_schedule` (`plan_item_schedule_plan_id`)"
    )
    execSQL(
        "CREATE INDEX IF NOT EXISTS `index_plan_item_routine_id` " +
            "ON `plan_item_schedule` (`plan_item_routine_id`)"
    )
}
