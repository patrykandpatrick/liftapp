package com.patrykandpatrick.liftapp.functionality.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.serialization.json.Json

/**
 * Moves prescription out of the exercise and the routine and into the plan.
 *
 * A goal was one rep range and a set count, shared by every set and hung off `(routine, exercise)`.
 * A target is a list — one entry per set — hung off `(plan, cycle, day, exercise)`, which is what
 * makes a volume ramp across weeks something the plan can hold rather than something code derives.
 *
 * Nothing is thrown away: every goal being dropped is expanded into an equivalent target, repeated
 * across the cycles the plan was scheduled for, so a plan mid-run keeps prescribing what it did. A
 * routine that belongs to no plan has nowhere to put a target and loses its goal — the app defaults
 * answer for it at workout start instead.
 *
 * **Every statement is idempotent**, because a fresh 11 → 12 → … → 15 chain arrives here with the
 * goal tables already written and has to reach the same place as an upgrading device.
 */
class Migration14To15(private val json: Json) : Migration(14, 15) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.createTargetTable()
        db.addSchedulePosition()
        db.backfillSchedulePosition()
        db.generateTargetsFromGoals()
        db.dropPrescriptionFromExerciseAndRoutine()
    }

    private fun SupportSQLiteDatabase.createTargetTable() {
        execSQL(
            "CREATE TABLE IF NOT EXISTS `exercise_target` (" +
                "`exercise_target_plan_id` INTEGER NOT NULL, " +
                "`exercise_target_cycle_index` INTEGER NOT NULL, " +
                "`exercise_target_item_order_index` INTEGER NOT NULL, " +
                "`exercise_target_exercise_id` INTEGER NOT NULL, " +
                "`exercise_target_sets` TEXT NOT NULL, " +
                "`exercise_target_rest_time` INTEGER NOT NULL, " +
                "`exercise_target_distance_unit` TEXT NOT NULL, " +
                "PRIMARY KEY(`exercise_target_plan_id`, `exercise_target_cycle_index`, " +
                "`exercise_target_item_order_index`, `exercise_target_exercise_id`), " +
                "FOREIGN KEY(`exercise_target_plan_id`) REFERENCES `plan`(`plan_id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE , " +
                "FOREIGN KEY(`exercise_target_exercise_id`) REFERENCES `exercise`(`exercise_id`) " +
                "ON UPDATE NO ACTION ON DELETE CASCADE )"
        )
        execSQL(
            "CREATE INDEX IF NOT EXISTS `index_exercise_target_exercise_target_plan_id` " +
                "ON `exercise_target` (`exercise_target_plan_id`)"
        )
        execSQL(
            "CREATE INDEX IF NOT EXISTS `index_exercise_target_exercise_target_exercise_id` " +
                "ON `exercise_target` (`exercise_target_exercise_id`)"
        )
    }

    private fun SupportSQLiteDatabase.addSchedulePosition() {
        val existing = columnNames("plan_item_schedule")
        if (CYCLE_INDEX !in existing) {
            execSQL(
                "ALTER TABLE `plan_item_schedule` ADD COLUMN `$CYCLE_INDEX` INTEGER NOT NULL DEFAULT 0"
            )
        }
        if (ITEM_ORDER_INDEX !in existing) {
            execSQL(
                "ALTER TABLE `plan_item_schedule` ADD COLUMN `$ITEM_ORDER_INDEX` INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    /**
     * The columns are new, so every existing row reads as cycle 0 day 0. The schedule was written
     * one day at a time from the plan's start date, so its date order *is* its position order.
     */
    private fun SupportSQLiteDatabase.backfillSchedulePosition() {
        planItemCounts().forEach { (planID, itemCount) ->
            if (itemCount <= 0) return@forEach
            val ids =
                query(
                        "SELECT `plan_item_schedule_id` FROM `plan_item_schedule` " +
                            "WHERE `plan_item_schedule_plan_id` = ? " +
                            "ORDER BY `plan_item_schedule_date`, `plan_item_schedule_id`",
                        arrayOf<Any?>(planID),
                    )
                    .use { cursor ->
                        buildList { while (cursor.moveToNext()) add(cursor.getLong(0)) }
                    }

            ids.forEachIndexed { dayIndex, id ->
                execSQL(
                    "UPDATE `plan_item_schedule` SET `$CYCLE_INDEX` = ?, `$ITEM_ORDER_INDEX` = ? " +
                        "WHERE `plan_item_schedule_id` = ?",
                    arrayOf<Any?>(dayIndex / itemCount, dayIndex % itemCount, id),
                )
            }
        }
    }

    private fun SupportSQLiteDatabase.generateTargetsFromGoals() {
        // A second run has nothing to read: the goals were dropped by the first.
        if (!hasTable("goal")) return

        planItemCounts().forEach { (planID, itemCount) ->
            if (itemCount <= 0) return@forEach
            val scheduledDays = scheduledDayCount(planID)
            // A plan nobody has started yet still gets one cycle's worth, so activating it later
            // finds a prescription rather than a hole.
            val cycleCount = maxOf(1, scheduledDays / itemCount)

            routinesByItemOrder(planID).forEach { (itemOrderIndex, routineID) ->
                val goals = goalsOf(routineID)
                exercisesOf(routineID).forEach { exerciseID ->
                    val goal = goals[exerciseID] ?: embeddedGoalOf(exerciseID) ?: LegacyGoal.default
                    val sets = json.encodeToString(goal.toSetTargets())
                    repeat(cycleCount) { cycleIndex ->
                        execSQL(
                            "INSERT OR REPLACE INTO `exercise_target` (" +
                                "`exercise_target_plan_id`, `exercise_target_cycle_index`, " +
                                "`exercise_target_item_order_index`, `exercise_target_exercise_id`, " +
                                "`exercise_target_sets`, `exercise_target_rest_time`, " +
                                "`exercise_target_distance_unit`) VALUES (?, ?, ?, ?, ?, ?, ?)",
                            arrayOf<Any?>(
                                planID,
                                cycleIndex,
                                itemOrderIndex,
                                exerciseID,
                                sets,
                                goal.restTime.inWholeMilliseconds,
                                json.encodeToString(goal.distanceUnit),
                            ),
                        )
                    }
                }
            }
        }
    }

    /** Every set alike: a goal said nothing about one set differing from the next. */
    private fun LegacyGoal.toSetTargets(): List<SetTarget> =
        List(sets.coerceAtLeast(1)) {
            SetTarget(
                minReps = minReps,
                maxReps = maxReps,
                duration = duration,
                distance = distance,
                calories = calories,
            )
        }

    /**
     * `exercise` is rebuilt rather than altered: `ALTER TABLE … DROP COLUMN` needs SQLite 3.35, and
     * which SQLite a device has is a function of its Android version.
     */
    private fun SupportSQLiteDatabase.dropPrescriptionFromExerciseAndRoutine() {
        execSQL("DROP TABLE IF EXISTS `goal`")

        if (EXERCISE_GOAL !in columnNames("exercise")) return
        execSQL(
            "CREATE TABLE IF NOT EXISTS `exercise_new` (" +
                "`exercise_id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "`exercise_name` TEXT NOT NULL, `exercise_type` TEXT NOT NULL, " +
                "`exercise_main_muscles` TEXT NOT NULL, `exercise_secondary_muscles` TEXT NOT NULL, " +
                "`exercise_tertiary_muscles` TEXT NOT NULL, `exercise_role` TEXT, " +
                "`exercise_progression_policy` TEXT NOT NULL DEFAULT 'DoubleProgression', " +
                "`exercise_load_increment` REAL)"
        )
        execSQL(
            "INSERT INTO `exercise_new` SELECT `exercise_id`, `exercise_name`, `exercise_type`, " +
                "`exercise_main_muscles`, `exercise_secondary_muscles`, `exercise_tertiary_muscles`, " +
                "`exercise_role`, `exercise_progression_policy`, `exercise_load_increment` " +
                "FROM `exercise`"
        )
        execSQL("DROP TABLE `exercise`")
        execSQL("ALTER TABLE `exercise_new` RENAME TO `exercise`")
        // Dropping the table dropped its indices with it.
        execSQL(
            "CREATE INDEX IF NOT EXISTS `index_exercise_exercise_name` ON `exercise` (`exercise_name`)"
        )
        execSQL(
            "CREATE INDEX IF NOT EXISTS `index_exercise_exercise_type` ON `exercise` (`exercise_type`)"
        )
        execSQL(
            "CREATE INDEX IF NOT EXISTS `index_exercise_exercise_main_muscles` " +
                "ON `exercise` (`exercise_main_muscles`)"
        )
    }

    private fun SupportSQLiteDatabase.planItemCounts(): Map<Long, Int> =
        query("SELECT `plan_id`, `plan_item_count` FROM `plan`").use { cursor ->
            buildMap { while (cursor.moveToNext()) put(cursor.getLong(0), cursor.getInt(1)) }
        }

    private fun SupportSQLiteDatabase.scheduledDayCount(planID: Long): Int =
        query(
                "SELECT COUNT(*) FROM `plan_item_schedule` WHERE `plan_item_schedule_plan_id` = ?",
                arrayOf<Any?>(planID),
            )
            .use { cursor -> if (cursor.moveToNext()) cursor.getInt(0) else 0 }

    private fun SupportSQLiteDatabase.routinesByItemOrder(planID: Long): List<Pair<Int, Long>> =
        query(
                "SELECT `plan_item_order_index`, `plan_item_routine_id` FROM `plan_item` " +
                    "WHERE `plan_item_plan_id` = ?",
                arrayOf<Any?>(planID),
            )
            .use { cursor ->
                buildList { while (cursor.moveToNext()) add(cursor.getInt(0) to cursor.getLong(1)) }
            }

    private fun SupportSQLiteDatabase.exercisesOf(routineID: Long): List<Long> =
        query(
                "SELECT DISTINCT membership.`exercise_id` FROM `exercise_with_routine_item` AS membership " +
                    "INNER JOIN `routine_item` AS item ON membership.`routine_item_id` = item.`routine_item_id` " +
                    "WHERE item.`routine_item_routine_id` = ?",
                arrayOf<Any?>(routineID),
            )
            .use { cursor -> buildList { while (cursor.moveToNext()) add(cursor.getLong(0)) } }

    private fun SupportSQLiteDatabase.goalsOf(routineID: Long): Map<Long, LegacyGoal> =
        query(
                "SELECT `goal_exercise_id`, `goal_min_reps`, `goal_max_reps`, `goal_sets`, " +
                    "`goal_rest_time`, `goal_duration_millis`, `goal_distance`, " +
                    "`goal_distance_unit`, `goal_calories` FROM `goal` WHERE `goal_routine_id` = ?",
                arrayOf<Any?>(routineID),
            )
            .use { cursor ->
                buildMap {
                    while (cursor.moveToNext()) {
                        put(
                            cursor.getLong(0),
                            LegacyGoal(
                                minReps = cursor.getInt(1),
                                maxReps = cursor.getInt(2),
                                sets = cursor.getInt(3),
                                restTime = cursor.getLong(4).milliseconds,
                                duration = cursor.getLong(5).milliseconds,
                                distance = cursor.getDouble(6),
                                distanceUnit = json.decodeFromString(cursor.getString(7)),
                                calories = cursor.getDouble(8),
                            ),
                        )
                    }
                }
            }

    private fun SupportSQLiteDatabase.embeddedGoalOf(exerciseID: Long): LegacyGoal? {
        if (EXERCISE_GOAL !in columnNames("exercise")) return null
        return query(
                "SELECT `exercise_goal` FROM `exercise` WHERE `exercise_id` = ?",
                arrayOf<Any?>(exerciseID),
            )
            .use { cursor ->
                if (cursor.moveToNext()) json.decodeFromString<LegacyGoal>(cursor.getString(0))
                else null
            }
    }

    private fun SupportSQLiteDatabase.hasTable(name: String): Boolean =
        query(
                "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?",
                arrayOf<Any?>(name),
            )
            .use { cursor -> cursor.moveToNext() }

    private fun SupportSQLiteDatabase.columnNames(table: String): Set<String> =
        query("PRAGMA table_info(`$table`)").use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            buildSet { while (cursor.moveToNext()) add(cursor.getString(nameIndex)) }
        }

    private companion object {
        const val CYCLE_INDEX = "plan_item_schedule_cycle_index"
        const val ITEM_ORDER_INDEX = "plan_item_schedule_item_order_index"
        const val EXERCISE_GOAL = "exercise_goal"
    }
}
