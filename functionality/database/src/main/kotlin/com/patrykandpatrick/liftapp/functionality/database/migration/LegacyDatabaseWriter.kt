package com.patrykandpatrick.liftapp.functionality.database.migration

import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.PercentageUnit
import com.patrykandpatrick.liftapp.domain.unit.ShortDistanceUnit
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Writes the published app's rows into this schema. The schema migration replays the live database
 * through it, and the backup importer replays the CSV files of a legacy archive.
 */
class LegacyDatabaseWriter(private val json: Json) {

    fun restoreExercise(db: SupportSQLiteDatabase, exercise: LegacyExercise) {
        val embeddedGoal = parseLegacyGoal(exercise.goal)
        val values =
            arrayOf<Any>(
                exercise.id,
                json.encodeToString<Name>(Name.Raw(exercise.name)),
                exercise.type.ifBlank { "WEIGHT" }.titlecase(),
                json.encodeToString(exercise.mainMuscles.mapNotNull(::muscle)),
                json.encodeToString(exercise.secondaryMuscles.mapNotNull(::muscle)),
                json.encodeToString(exercise.tertiaryMuscles.mapNotNull(::muscle)),
                json.encodeToString(embeddedGoal),
            )
        db.execSQL(
            "INSERT OR IGNORE INTO exercise (exercise_id, exercise_name, exercise_type, " +
                "exercise_main_muscles, exercise_secondary_muscles, " +
                "exercise_tertiary_muscles, exercise_goal) VALUES (?, ?, ?, ?, ?, ?, ?)",
            values,
        )

        // Built-in rows already have localized Resource names in the new database. Keep those;
        // legacy custom exercises have plain names and should overwrite a previous legacy import.
        val nameAssignment = if (exercise.name.startsWith("ex_")) "" else "exercise_name = ?, "
        val updateValues =
            buildList<Any> {
                    if (nameAssignment.isNotEmpty()) add(values[1])
                    addAll(values.slice(2..6))
                    add(exercise.id)
                }
                .toTypedArray()
        db.execSQL(
            "UPDATE exercise SET $nameAssignment" +
                "exercise_type = ?, exercise_main_muscles = ?, exercise_secondary_muscles = ?, " +
                "exercise_tertiary_muscles = ?, exercise_goal = ? WHERE exercise_id = ?",
            updateValues,
        )
    }

    fun restoreRoutine(
        db: SupportSQLiteDatabase,
        routine: LegacyRoutine,
        exerciseIDs: (modelID: Long) -> List<Long>,
        goals: List<LegacyExerciseGoal>,
    ) {
        clearRoutineItems(db, routine.id)
        db.execSQL(
            "INSERT OR REPLACE INTO routine (routine_id, routine_name, routine_order_index) " +
                "VALUES (?, ?, ?)",
            arrayOf<Any?>(routine.id, routine.name, routine.order),
        )
        routine.modelIDs.forEachIndexed { index, modelID ->
            val ids = exerciseIDs(modelID)
            if (ids.isEmpty()) return@forEachIndexed
            val type = if (ids.size == 1) RoutineItemType.Exercise else RoutineItemType.Superset
            val itemID =
                insert(
                    db,
                    "INSERT INTO routine_item (routine_item_routine_id, routine_item_order_index, " +
                        "routine_item_type) VALUES (?, ?, ?)",
                    routine.id,
                    index,
                    type.name,
                )
            ids.forEachIndexed { exerciseIndex, exerciseID ->
                db.execSQL(
                    "INSERT OR REPLACE INTO exercise_with_routine_item " +
                        "(routine_item_id, exercise_id, routine_item_exercise_order_index) " +
                        "VALUES (?, ?, ?)",
                    arrayOf<Any?>(itemID, exerciseID, exerciseIndex),
                )
            }
            val goal = goals.lastOrNull { it.routineID == routine.id && it.modelID == modelID }
            if (type == RoutineItemType.Superset) {
                db.execSQL(
                    "INSERT OR REPLACE INTO superset (superset_routine_item_id, superset_sets, " +
                        "superset_rest_time_millis) VALUES (?, ?, ?)",
                    arrayOf<Any?>(itemID, goal?.sets ?: 3, (goal?.restSeconds ?: 120) * 1_000L),
                )
            }
            ids.forEach { exerciseID -> insertGoal(db, routine.id, exerciseID, goal) }
        }
    }

    fun restoreWorkout(
        db: SupportSQLiteDatabase,
        workout: LegacyWorkout,
        exerciseIDs: (modelID: Long) -> List<Long>,
        goals: List<LegacyExerciseGoal>,
        records: Map<Long, LegacyExerciseRecord>,
    ) {
        db.execSQL(
            "INSERT OR IGNORE INTO routine (routine_id, routine_name, routine_order_index) " +
                "VALUES (?, ?, 0)",
            arrayOf<Any?>(workout.routineID, workout.name),
        )
        clearWorkout(db, workout.id)
        val bodyWeight =
            workout.recordIDs
                .asSequence()
                .mapNotNull(records::get)
                .mapNotNull { record ->
                    record.sets.bodyWeight()?.let { value ->
                        val unit =
                            if (record.sets.unit.equals("lb", true)) {
                                MassUnit.Pounds
                            } else {
                                MassUnit.Kilograms
                            }
                        BodyMeasurementValue.SingleValue(value, unit)
                    }
                }
                .firstOrNull()
                ?.let { json.encodeToString<BodyMeasurementValue>(it) }
        db.execSQL(
            "INSERT OR REPLACE INTO workout (workout_id, workout_routine_id, workout_name, " +
                "workout_start_date, workout_end_date, workout_notes, workout_body_weight) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)",
            arrayOf<Any?>(
                workout.id,
                workout.routineID,
                workout.name,
                date(workout.id),
                workout.endTime.takeIf { it > 0 }?.let(::date),
                "",
                bodyWeight,
            ),
        )

        val remainingRecords = workout.recordIDs.mapNotNull(records::get).toMutableList()
        workout.modelIDs.forEachIndexed { index, modelID ->
            val ids = exerciseIDs(modelID)
            if (ids.isEmpty()) return@forEachIndexed
            val modelRecords = ids.mapNotNull { exerciseID ->
                remainingRecords
                    .firstOrNull { it.exerciseID == exerciseID }
                    ?.also(remainingRecords::remove)
            }
            val goal = goals.lastOrNull {
                it.routineID == workout.routineID && it.modelID == modelID
            }
            val type = if (ids.size == 1) RoutineItemType.Exercise else RoutineItemType.Superset
            val itemID =
                insert(
                    db,
                    "INSERT INTO workout_item (workout_item_workout_id, workout_item_order_index, " +
                        "workout_item_type, workout_item_sets, workout_item_rest_time_millis) " +
                        "VALUES (?, ?, ?, ?, ?)",
                    workout.id,
                    index,
                    type.name,
                    if (type == RoutineItemType.Superset) goal?.sets ?: 3 else null,
                    if (type == RoutineItemType.Superset) {
                        (goal?.restSeconds ?: 120) * 1_000L
                    } else {
                        null
                    },
                )
            ids.forEachIndexed { exerciseIndex, exerciseID ->
                val record = modelRecords.firstOrNull { it.exerciseID == exerciseID }
                db.execSQL(
                    "INSERT OR REPLACE INTO exercise_with_workout_item (workout_item_id, exercise_id, " +
                        "workout_item_exercise_order_index, workout_item_exercise_notes) " +
                        "VALUES (?, ?, ?, ?)",
                    arrayOf<Any?>(itemID, exerciseID, exerciseIndex, record?.comment.orEmpty()),
                )
                insertWorkoutGoal(db, workout.id, exerciseID, goal)
                record?.sets?.sets?.forEachIndexed { setIndex, set ->
                    restoreSet(db, workout.id, exerciseID, setIndex, set, record.sets)
                }
            }
        }
    }

    fun restoreBodyRecord(db: SupportSQLiteDatabase, record: LegacyBodyRecord) {
        val newID =
            when (record.measurementID) {
                in 1..5 -> record.measurementID
                in 7..11 -> record.measurementID - 1
                else -> return
            }
        val type = bodyType(record.measurementID)
        val fallbackName = bodyName(record.measurementID)
        db.execSQL(
            "INSERT OR IGNORE INTO body_measurements (id, name, type) VALUES (?, ?, ?)",
            arrayOf<Any?>(newID, json.encodeToString<Name>(Name.Raw(fallbackName)), type.name),
        )
        val unit =
            when (type) {
                BodyMeasurementType.Weight ->
                    if (record.unit.equals("lb", true)) MassUnit.Pounds else MassUnit.Kilograms
                BodyMeasurementType.Percentage -> PercentageUnit
                else ->
                    if (record.unit.equals("in", true)) {
                        ShortDistanceUnit.Inch
                    } else {
                        ShortDistanceUnit.Centimeter
                    }
            }
        val value =
            if (type == BodyMeasurementType.LengthTwoSides) {
                BodyMeasurementValue.DoubleValue(record.left, record.right, unit)
            } else {
                BodyMeasurementValue.SingleValue(record.left, unit)
            }
        db.execSQL(
            "INSERT OR REPLACE INTO body_measurement_entries " +
                "(id, body_measurement_id, value, time) VALUES (?, ?, ?, ?)",
            arrayOf<Any?>(
                record.id,
                newID,
                json.encodeToString<BodyMeasurementValue>(value),
                date(record.id),
            ),
        )
    }

    /**
     * Writes the plan recreated from the published app's `plan_ids` preference, replacing any
     * previous version of it. Returns one entry per legacy position: the routine ID when the
     * routine exists, or null for a rest day or a routine that is gone.
     */
    fun writePlan(
        db: SupportSQLiteDatabase,
        planID: Long,
        name: String,
        legacyIDs: List<Long>,
    ): List<Long?> {
        db.execSQL(
            "INSERT OR REPLACE INTO plan " +
                "(plan_id, plan_name, plan_description, plan_item_count) VALUES (?, ?, ?, ?)",
            arrayOf<Any?>(planID, name, "", legacyIDs.size),
        )
        db.execSQL("DELETE FROM plan_item WHERE plan_item_plan_id = ?", arrayOf<Any?>(planID))
        db.execSQL(
            "DELETE FROM plan_item_schedule WHERE plan_item_schedule_plan_id = ?",
            arrayOf<Any?>(planID),
        )
        val items = legacyIDs.map { id -> id.takeIf { it > 0 && routineExists(db, it) } }
        items.forEachIndexed { index, routineID ->
            if (routineID == null) return@forEachIndexed
            db.execSQL(
                "INSERT INTO plan_item " +
                    "(plan_item_plan_id, plan_item_routine_id, plan_item_order_index) " +
                    "VALUES (?, ?, ?)",
                arrayOf<Any?>(planID, routineID, index),
            )
        }
        return items
    }

    private fun routineExists(db: SupportSQLiteDatabase, routineID: Long): Boolean =
        db.query("SELECT 1 FROM routine WHERE routine_id = ?", arrayOf<Any?>(routineID)).use {
            it.moveToFirst()
        }

    private fun restoreSet(
        db: SupportSQLiteDatabase,
        workoutID: Long,
        exerciseID: Long,
        index: Int,
        set: LegacySet,
        sets: LegacySets,
    ) {
        val massUnit = if (sets.unit.equals("lb", true)) MassUnit.Pounds else MassUnit.Kilograms
        val distanceUnit =
            if (sets.unit.equals("mi", true) || sets.unit.equals("mile", true)) {
                LongDistanceUnit.Mile
            } else {
                LongDistanceUnit.Kilometer
            }
        db.execSQL(
            "INSERT INTO exercise_set (exercise_set_workout_id, exercise_set_exercise_id, " +
                "exercise_set_weight, exercise_set_weight_unit, exercise_set_reps, exercise_set_time, " +
                "exercise_set_distance, exercise_set_distance_unit, exercise_set_kcal, " +
                "exercise_set_notes, workout_exercise_set_index) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            arrayOf<Any?>(
                workoutID,
                exerciseID,
                set.weight,
                set.weight?.let { json.encodeToString(massUnit) },
                set.reps,
                set.seconds?.times(1_000L),
                set.distance,
                set.distance?.let { json.encodeToString(distanceUnit) },
                null,
                sets.comments[index].orEmpty(),
                index,
            ),
        )
    }

    private fun clearRoutineItems(db: SupportSQLiteDatabase, routineID: Long) {
        val ids = "SELECT routine_item_id FROM routine_item WHERE routine_item_routine_id = ?"
        db.execSQL(
            "DELETE FROM exercise_with_routine_item WHERE routine_item_id IN ($ids)",
            arrayOf<Any?>(routineID),
        )
        db.execSQL(
            "DELETE FROM superset WHERE superset_routine_item_id IN ($ids)",
            arrayOf<Any?>(routineID),
        )
        db.execSQL(
            "DELETE FROM routine_item WHERE routine_item_routine_id = ?",
            arrayOf<Any?>(routineID),
        )
        db.execSQL("DELETE FROM goal WHERE goal_routine_id = ?", arrayOf<Any?>(routineID))
    }

    private fun clearWorkout(db: SupportSQLiteDatabase, workoutID: Long) {
        val ids = "SELECT workout_item_id FROM workout_item WHERE workout_item_workout_id = ?"
        db.execSQL(
            "DELETE FROM exercise_with_workout_item WHERE workout_item_id IN ($ids)",
            arrayOf<Any?>(workoutID),
        )
        db.execSQL(
            "DELETE FROM workout_item WHERE workout_item_workout_id = ?",
            arrayOf<Any?>(workoutID),
        )
        db.execSQL(
            "DELETE FROM workout_goal WHERE workout_goal_workout_id = ?",
            arrayOf<Any?>(workoutID),
        )
        db.execSQL(
            "DELETE FROM exercise_set WHERE exercise_set_workout_id = ?",
            arrayOf<Any?>(workoutID),
        )
    }

    private fun insertGoal(
        db: SupportSQLiteDatabase,
        routineID: Long,
        exerciseID: Long,
        legacy: LegacyExerciseGoal?,
    ) {
        val goal = legacy?.toGoal() ?: Goal.default
        db.execSQL(
            "INSERT INTO goal (goal_routine_id, goal_exercise_id, goal_min_reps, goal_max_reps, " +
                "goal_sets, goal_rest_time, goal_duration_millis, goal_distance, goal_distance_unit, " +
                "goal_calories) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            goalValues(routineID, exerciseID, goal),
        )
    }

    private fun insertWorkoutGoal(
        db: SupportSQLiteDatabase,
        workoutID: Long,
        exerciseID: Long,
        legacy: LegacyExerciseGoal?,
    ) {
        val goal = legacy?.toGoal() ?: Goal.default
        db.execSQL(
            "INSERT INTO workout_goal (workout_goal_workout_id, workout_goal_exercise_id, " +
                "workout_goal_min_reps, workout_goal_max_reps, workout_goal_sets, " +
                "workout_goal_rest_time, workout_goal_duration_millis, workout_goal_distance, " +
                "workout_goal_distance_unit, workout_goal_calories) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            goalValues(workoutID, exerciseID, goal),
        )
    }

    private fun goalValues(parentID: Long, exerciseID: Long, goal: Goal): Array<Any?> =
        arrayOf<Any?>(
            parentID,
            exerciseID,
            goal.minReps,
            goal.maxReps,
            goal.sets,
            goal.restTime.inWholeMilliseconds,
            goal.duration.inWholeMilliseconds,
            goal.distance,
            json.encodeToString(goal.distanceUnit),
            goal.calories,
        )
}

private fun muscle(value: String): Muscle? =
    Muscle.entries.firstOrNull { it.name.equals(value.replace("_", ""), ignoreCase = true) }

private fun String.titlecase(): String = lowercase().replaceFirstChar(Char::titlecase)

private fun date(epochMillis: Long): String =
    LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault()).toString()

private fun bodyType(oldID: Long): BodyMeasurementType =
    when (oldID) {
        1L -> BodyMeasurementType.Weight
        2L,
        3L -> BodyMeasurementType.Percentage
        4L,
        5L,
        10L,
        11L -> BodyMeasurementType.LengthTwoSides
        else -> BodyMeasurementType.Length
    }

private fun bodyName(oldID: Long): String =
    when (oldID) {
        1L -> "Body weight"
        2L -> "Fat percentage"
        3L -> "Muscle percentage"
        4L -> "Forearm circumference"
        5L -> "Bicep circumference"
        7L -> "Chest circumference"
        8L -> "Ab circumference"
        9L -> "Glute circumference"
        10L -> "Thigh circumference"
        else -> "Calf circumference"
    }

internal fun insert(db: SupportSQLiteDatabase, sql: String, vararg values: Any?): Long {
    val statement = db.compileStatement(sql)
    values.forEachIndexed { index, value ->
        val position = index + 1
        when (value) {
            null -> statement.bindNull(position)
            is String -> statement.bindString(position, value)
            is Long -> statement.bindLong(position, value)
            is Int -> statement.bindLong(position, value.toLong())
            is Double -> statement.bindDouble(position, value)
            else -> error("Unsupported SQLite value ${value::class}.")
        }
    }
    return statement.executeInsert()
}
