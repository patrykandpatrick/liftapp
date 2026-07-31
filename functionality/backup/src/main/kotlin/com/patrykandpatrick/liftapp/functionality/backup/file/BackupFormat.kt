package com.patrykandpatrick.liftapp.functionality.backup.file

import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import kotlinx.serialization.Serializable

/**
 * The layout of a `.lfa` file.
 *
 * A backup is a ZIP holding [MANIFEST_NAME] and one CSV per table, under a directory named after
 * the [BackupDataType] the table belongs to. The CSVs carry the table's own column names in their
 * header row, so the format describes the schema it was written from rather than a parallel
 * description of it that could drift.
 */
internal object BackupFormat {

    /** Bumped whenever a written file stops being readable by the previous reader. */
    const val VERSION = 1

    const val EXTENSION = ".lfa"
    const val MIME_TYPE = "application/octet-stream"
    const val CSV_EXTENSION = ".csv"
    const val MANIFEST_NAME = "manifest.json"

    /** The CSV holding the exported DataStore preferences, rather than a database table. */
    const val PREFERENCES_NAME = "preferences"

    fun directory(type: BackupDataType): String = type.name.lowercase()

    fun entryPath(type: BackupDataType, name: String): String =
        "${directory(type)}/$name$CSV_EXTENSION"
}

@Serializable
internal data class BackupManifest(
    val formatVersion: Int,
    val createdAt: String,
    val appVersionName: String,
    val contents: List<BackupDataType>,
)

/**
 * Every table a backup can carry, in an order that satisfies the foreign keys — parents first, so
 * an import can replay the list top to bottom.
 *
 * [BackupDataType.dependencies] is what keeps this consistent: `Workouts` reference the routine
 * they were started from, so a workout backup carries the routine tables too.
 */
internal enum class BackupTable(val tableName: String, val type: BackupDataType) {
    Exercise("exercise", BackupDataType.Routines),
    Routine("routine", BackupDataType.Routines),
    RoutineItem("routine_item", BackupDataType.Routines),
    ExerciseWithRoutineItem("exercise_with_routine_item", BackupDataType.Routines),
    Superset("superset", BackupDataType.Routines),
    Goal("goal", BackupDataType.Routines),
    Plan("plan", BackupDataType.TrainingPlans),
    PlanItem("plan_item", BackupDataType.TrainingPlans),
    PlanItemSchedule("plan_item_schedule", BackupDataType.TrainingPlans),
    Workout("workout", BackupDataType.Workouts),
    WorkoutItem("workout_item", BackupDataType.Workouts),
    ExerciseWithWorkoutItem("exercise_with_workout_item", BackupDataType.Workouts),
    WorkoutGoal("workout_goal", BackupDataType.Workouts),
    ExerciseSet("exercise_set", BackupDataType.Workouts),
    BodyMeasurement("body_measurements", BackupDataType.BodyMeasurements),
    BodyMeasurementEntry("body_measurement_entries", BackupDataType.BodyMeasurements);

    val entryPath: String
        get() = BackupFormat.entryPath(type, tableName)

    /**
     * The `WHERE` clause that narrows this table to a single routine, for the quick export behind
     * the share action. `null` means the table has nothing to do with one routine and is skipped.
     *
     * The clause takes the routine ID as its only bound argument, repeated as many times as it
     * appears — see [routineArgumentCount].
     */
    val routineFilter: String?
        get() =
            when (this) {
                Routine -> "routine_id = ?"
                RoutineItem,
                Goal -> "${routineColumn()} = ?"
                ExerciseWithRoutineItem,
                Superset -> "${routineItemColumn()} IN ($ROUTINE_ITEM_IDS)"
                Exercise -> "exercise_id IN ($ROUTINE_EXERCISE_IDS)"
                else -> null
            }

    val routineArgumentCount: Int
        get() = routineFilter?.count { it == '?' } ?: 0

    private fun routineColumn(): String =
        when (this) {
            RoutineItem -> "routine_item_routine_id"
            Goal -> "goal_routine_id"
            else -> error("$this is not scoped by routine.")
        }

    private fun routineItemColumn(): String =
        when (this) {
            ExerciseWithRoutineItem -> "routine_item_id"
            Superset -> "superset_routine_item_id"
            else -> error("$this is not scoped by routine item.")
        }

    companion object {
        private const val ROUTINE_ITEM_IDS =
            "SELECT routine_item_id FROM routine_item WHERE routine_item_routine_id = ?"

        private const val ROUTINE_EXERCISE_IDS =
            "SELECT exercise_id FROM exercise_with_routine_item WHERE routine_item_id IN " +
                "($ROUTINE_ITEM_IDS)"
    }
}
