package com.patrykandpatrick.liftapp.functionality.backup.file

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatrick.liftapp.domain.backup.AutoBackupSettings
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.domain.backup.BackupInterval
import com.patrykandpatrick.liftapp.domain.backup.BackupRetention
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.goal.Goal
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.theme.Theme
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.PercentageUnit
import com.patrykandpatrick.liftapp.domain.unit.ShortDistanceUnit
import java.io.InputStream
import java.io.Reader
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** Reader and one-way schema migration for backups written by the original LiftApp. */
internal class LegacyBackup
private constructor(
    private val version: Int,
    private val files: Map<LegacyFile, String>,
    private val preferencesXml: String?,
) {

    private val legacyPreferences by lazy { LegacyPreferences.read(preferencesXml.orEmpty()) }

    val contents: Set<BackupDataType> = buildSet {
        if (files.keys.any { it.parent == LegacyParent.Routines }) add(BackupDataType.Routines)
        if (files.keys.any { it.parent == LegacyParent.Workouts }) {
            add(BackupDataType.Routines)
            add(BackupDataType.Workouts)
        }
        if (files.keys.any { it.parent == LegacyParent.Body }) {
            add(BackupDataType.BodyMeasurements)
        }
        if (legacyPreferences.hasSettings) add(BackupDataType.Settings)
        if (
            legacyPreferences.planIDs.isNotEmpty() &&
                files.keys.any { it.parent == LegacyParent.Routines }
        ) {
            add(BackupDataType.TrainingPlans)
        }
    }

    fun restore(db: SupportSQLiteDatabase, types: Set<BackupDataType>, json: Json): Boolean {
        var restored = false
        val routinesWanted = BackupDataType.Routines in types
        val workoutsWanted = BackupDataType.Workouts in types

        if (routinesWanted || workoutsWanted) {
            val exercises =
                rows(LegacyKind.Exercises, routinesWanted, workoutsWanted).map(::exercise)
            exercises.distinctBy { it.id }.forEach { restoreExercise(db, it, json) }

            val models =
                if (version == 1) emptyMap()
                else
                    rows(LegacyKind.ExerciseModels, routinesWanted, workoutsWanted)
                        .map(::model)
                        .associateBy { it.id }
            val goals =
                rows(LegacyKind.ExerciseGoals, routinesWanted, workoutsWanted)
                    .map(::exerciseGoal)
                    .distinctBy { Triple(it.routineID, it.modelID, it.id) }

            if (routinesWanted) {
                val routines =
                    rows(
                        LegacyKind.Routines,
                        includeRoutines = true,
                        includeWorkouts = workoutsWanted,
                    )
                routines
                    .map(::routine)
                    .distinctBy { it.id }
                    .forEach { restoreRoutine(db, it, models, goals, json) }
                restored = restored || exercises.isNotEmpty() || routines.isNotEmpty()
            }

            if (workoutsWanted) {
                val records =
                    rows(LegacyKind.ExerciseRecords, false, true)
                        .map(::exerciseRecord)
                        .associateBy { it.id }
                val workouts = rows(LegacyKind.RoutineRecords, false, true)
                workouts
                    .map(::workout)
                    .distinctBy { it.id }
                    .forEach { restoreWorkout(db, it, models, goals, records, json) }
                restored = restored || records.isNotEmpty() || workouts.isNotEmpty()
            }

            if (BackupDataType.TrainingPlans in types && legacyPreferences.planIDs.isNotEmpty()) {
                restorePlan(db, legacyPreferences.planIDs)
                restored = true
            }
        }

        if (BackupDataType.BodyMeasurements in types) {
            val bodyRecords = rows(LegacyKind.BodyRecords, false, false)
            bodyRecords.map(::bodyRecord).forEach {
                restoreBodyRecord(db, it, json)
            }
            restored = restored || bodyRecords.isNotEmpty()
        }
        return restored
    }

    fun preferenceEdit(json: Json): ((MutablePreferences) -> Unit)? =
        legacyPreferences.edit(json).takeIf { BackupDataType.Settings in contents }

    private fun restorePlan(db: SupportSQLiteDatabase, planIDs: List<Long>) {
        db.execSQL(
            "INSERT OR REPLACE INTO plan " +
                "(plan_id, plan_name, plan_description, plan_item_count) VALUES (?, ?, ?, ?)",
            arrayOf<Any?>(LEGACY_PLAN_ID, "Imported plan", "", planIDs.size),
        )
        db.execSQL(
            "DELETE FROM plan_item WHERE plan_item_plan_id = ?",
            arrayOf<Any?>(LEGACY_PLAN_ID),
        )
        db.execSQL(
            "DELETE FROM plan_item_schedule WHERE plan_item_schedule_plan_id = ?",
            arrayOf<Any?>(LEGACY_PLAN_ID),
        )
        planIDs.forEachIndexed { index, routineID ->
            if (routineID <= 0 || !routineExists(db, routineID)) return@forEachIndexed
            db.execSQL(
                "INSERT INTO plan_item " +
                    "(plan_item_plan_id, plan_item_routine_id, plan_item_order_index) " +
                    "VALUES (?, ?, ?)",
                arrayOf<Any?>(LEGACY_PLAN_ID, routineID, index),
            )
        }
    }

    private fun routineExists(db: SupportSQLiteDatabase, routineID: Long): Boolean =
        db.query("SELECT 1 FROM routine WHERE routine_id = ?", arrayOf<Any?>(routineID)).use {
            it.moveToFirst()
        }

    private fun restoreExercise(db: SupportSQLiteDatabase, exercise: LegacyExercise, json: Json) {
        val embeddedGoal = exercise.goal.toGoal()
        val values =
            arrayOf<Any>(
                exercise.id,
                json.encodeToString<Name>(Name.Raw(exercise.name)),
                exercise.type.titlecase(),
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

    private fun restoreRoutine(
        db: SupportSQLiteDatabase,
        routine: LegacyRoutine,
        models: Map<Long, LegacyModel>,
        goals: List<LegacyExerciseGoal>,
        json: Json,
    ) {
        clearRoutineItems(db, routine.id)
        db.execSQL(
            "INSERT OR REPLACE INTO routine (routine_id, routine_name, routine_order_index) " +
                "VALUES (?, ?, ?)",
            arrayOf<Any?>(routine.id, routine.name, routine.order),
        )
        routine.modelIDs.forEachIndexed { index, modelID ->
            val exerciseIDs = exerciseIDs(modelID, models)
            if (exerciseIDs.isEmpty()) return@forEachIndexed
            val type =
                if (exerciseIDs.size == 1) RoutineItemType.Exercise else RoutineItemType.Superset
            val itemID =
                insert(
                    db,
                    "INSERT INTO routine_item (routine_item_routine_id, routine_item_order_index, " +
                        "routine_item_type) VALUES (?, ?, ?)",
                    routine.id,
                    index,
                    type.name,
                )
            exerciseIDs.forEachIndexed { exerciseIndex, exerciseID ->
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
            exerciseIDs.forEach { exerciseID -> insertGoal(db, routine.id, exerciseID, goal, json) }
        }
    }

    private fun restoreWorkout(
        db: SupportSQLiteDatabase,
        workout: LegacyWorkout,
        models: Map<Long, LegacyModel>,
        goals: List<LegacyExerciseGoal>,
        records: Map<Long, LegacyExerciseRecord>,
        json: Json,
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
                            if (record.sets.unit.equals("lb", true)) MassUnit.Pounds
                            else MassUnit.Kilograms
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
            val exerciseIDs = exerciseIDs(modelID, models)
            if (exerciseIDs.isEmpty()) return@forEachIndexed
            val modelRecords = exerciseIDs.mapNotNull { exerciseID ->
                remainingRecords
                    .firstOrNull { it.exerciseID == exerciseID }
                    ?.also(remainingRecords::remove)
            }
            val goal = goals.lastOrNull {
                it.routineID == workout.routineID && it.modelID == modelID
            }
            val type =
                if (exerciseIDs.size == 1) RoutineItemType.Exercise else RoutineItemType.Superset
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
                    if (type == RoutineItemType.Superset) (goal?.restSeconds ?: 120) * 1_000L
                    else null,
                )
            exerciseIDs.forEachIndexed { exerciseIndex, exerciseID ->
                val record = modelRecords.firstOrNull { it.exerciseID == exerciseID }
                db.execSQL(
                    "INSERT OR REPLACE INTO exercise_with_workout_item (workout_item_id, exercise_id, " +
                        "workout_item_exercise_order_index, workout_item_exercise_notes) " +
                        "VALUES (?, ?, ?, ?)",
                    arrayOf<Any?>(itemID, exerciseID, exerciseIndex, record?.comment.orEmpty()),
                )
                insertWorkoutGoal(db, workout.id, exerciseID, goal, json)
                record?.sets?.sets?.forEachIndexed { setIndex, set ->
                    restoreSet(db, workout.id, exerciseID, setIndex, set, record.sets, json)
                }
            }
        }
    }

    private fun restoreSet(
        db: SupportSQLiteDatabase,
        workoutID: Long,
        exerciseID: Long,
        index: Int,
        set: LegacySet,
        sets: LegacySets,
        json: Json,
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

    private fun restoreBodyRecord(db: SupportSQLiteDatabase, record: LegacyBodyRecord, json: Json) {
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
                    if (record.unit.equals("in", true)) ShortDistanceUnit.Inch
                    else ShortDistanceUnit.Centimeter
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
        json: Json,
    ) {
        val goal = legacy?.toGoal() ?: Goal.default
        db.execSQL(
            "INSERT INTO goal (goal_routine_id, goal_exercise_id, goal_min_reps, goal_max_reps, " +
                "goal_sets, goal_rest_time, goal_duration_millis, goal_distance, goal_distance_unit, " +
                "goal_calories) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            goalValues(routineID, exerciseID, goal, json),
        )
    }

    private fun insertWorkoutGoal(
        db: SupportSQLiteDatabase,
        workoutID: Long,
        exerciseID: Long,
        legacy: LegacyExerciseGoal?,
        json: Json,
    ) {
        val goal = legacy?.toGoal() ?: Goal.default
        db.execSQL(
            "INSERT INTO workout_goal (workout_goal_workout_id, workout_goal_exercise_id, " +
                "workout_goal_min_reps, workout_goal_max_reps, workout_goal_sets, " +
                "workout_goal_rest_time, workout_goal_duration_millis, workout_goal_distance, " +
                "workout_goal_distance_unit, workout_goal_calories) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            goalValues(workoutID, exerciseID, goal, json),
        )
    }

    private fun goalValues(
        parentID: Long,
        exerciseID: Long,
        goal: Goal,
        json: Json,
    ): Array<Any?> =
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

    private fun exerciseIDs(modelID: Long, models: Map<Long, LegacyModel>): List<Long> =
        if (version == 1) listOf(modelID) else models[modelID]?.exerciseIDs.orEmpty()

    private fun rows(
        kind: LegacyKind,
        includeRoutines: Boolean,
        includeWorkouts: Boolean,
    ): List<LegacyRow> =
        files
            .asSequence()
            .filter { (file, _) ->
                file.kind == kind &&
                    ((includeRoutines && file.parent == LegacyParent.Routines) ||
                        (includeWorkouts && file.parent == LegacyParent.Workouts) ||
                        (kind == LegacyKind.BodyRecords && file.parent == LegacyParent.Body))
            }
            .flatMap { (_, text) -> LegacyCsv.read(text.reader()).drop(1) }
            .filter { it.isNotEmpty() }
            .map(::LegacyRow)
            .toList()

    companion object {
        fun read(stream: InputStream): LegacyBackup? {
            var version = 1
            val files = mutableMapOf<LegacyFile, String>()
            var preferencesXml: String? = null
            runCatching {
                readArchive(stream) { path, reader ->
                    val baseName = path.substringAfterLast('/').substringBeforeLast('.')
                    val parentName = path.substringBeforeLast('/', "").substringAfterLast('/')
                    if (baseName.equals("version", true)) {
                        version = reader.readText().trim().toIntOrNull() ?: 1
                    } else if (
                        parentName.equals("shared_prefs", true) &&
                            baseName.contains("_preferences", ignoreCase = true)
                    ) {
                        preferencesXml = reader.readText()
                    } else {
                        LegacyFile.from(path)?.let { files[it] = reader.readText() }
                    }
                    ArchiveStep.Continue
                }
            }
                .getOrElse {
                    return null
                }
            if (version !in 1..2 || (files.isEmpty() && preferencesXml == null)) return null
            if (files.any { (file, text) -> !file.hasExpectedHeader(text) }) return null
            return LegacyBackup(version, files, preferencesXml).takeIf { it.contents.isNotEmpty() }
        }

        private const val LEGACY_PLAN_ID = -1L
    }
}

private enum class LegacyParent {
    Routines,
    Workouts,
    Body,
}

private enum class LegacyKind {
    Exercises,
    ExerciseRecords,
    ExerciseModels,
    ExerciseGoals,
    Routines,
    RoutineRecords,
    BodyRecords,
}

private data class LegacyFile(val parent: LegacyParent, val kind: LegacyKind) {
    fun hasExpectedHeader(text: String): Boolean {
        val actual =
            runCatching { LegacyCsv.read(text.reader()).firstOrNull() }.getOrNull() ?: return false
        val expected =
            when (kind) {
                LegacyKind.Exercises ->
                    listOf("exercise_id", "exercise_type", "exercise_name", "exercise_goal")
                LegacyKind.ExerciseRecords ->
                    listOf("ex_record_id", "ex_record_exercise_id", "ex_records")
                LegacyKind.ExerciseModels -> listOf("id", "exercise_ids")
                LegacyKind.ExerciseGoals ->
                    listOf(
                        "id",
                        "routine_id",
                        "exercise_id",
                        "set_count",
                        "break_time",
                        "rep_range",
                    )
                LegacyKind.Routines -> listOf("id", "name", "exercise_ids", "order_number")
                LegacyKind.RoutineRecords ->
                    listOf("id", "end_time", "routine_id", "name", "exercise_ids", "record_ids")
                LegacyKind.BodyRecords ->
                    listOf("r_id", "r_m_id", "r_value_left", "r_value_right", "r_unit")
            }
        return actual.size >= expected.size &&
            expected.indices.all { actual[it].equals(expected[it], ignoreCase = true) }
    }

    companion object {
        fun from(path: String): LegacyFile? {
            val pieces = path.replace('\\', '/').split('/').filter(String::isNotBlank)
            if (pieces.size < 2) return null
            val parent =
                when (pieces[pieces.lastIndex - 1].key()) {
                    "routines_data",
                    "training_plans" -> LegacyParent.Routines
                    "workouts_data",
                    "workouts" -> LegacyParent.Workouts
                    "body_data",
                    "body" -> LegacyParent.Body
                    else -> return null
                }
            val kind =
                when (pieces.last().substringBeforeLast('.').key()) {
                    "exercises" -> LegacyKind.Exercises
                    "exercise_records" -> LegacyKind.ExerciseRecords
                    "exercise_models" -> LegacyKind.ExerciseModels
                    "exercise_goals" -> LegacyKind.ExerciseGoals
                    "routines",
                    "plans" -> LegacyKind.Routines
                    "routine_records",
                    "plan_records" -> LegacyKind.RoutineRecords
                    "body_records" -> LegacyKind.BodyRecords
                    else -> return null
                }
            if (parent == LegacyParent.Body && kind != LegacyKind.BodyRecords) return null
            return LegacyFile(parent, kind)
        }
    }
}

/** Conventional CSV used by FastCSV in the old application. */
internal object LegacyCsv {
    fun read(reader: Reader): Sequence<List<String>> = sequence {
        val field = StringBuilder()
        var row = mutableListOf<String>()
        var quoted = false
        while (true) {
            var value = reader.read()
            var char = value.takeIf { it >= 0 }?.toChar()

            if (quoted && char == '"') {
                value = reader.read()
                char = value.takeIf { it >= 0 }?.toChar()
                if (char == '"') {
                    field.append('"')
                    continue
                }
                quoted = false
            } else if (quoted && char != null) {
                field.append(char)
                continue
            }

            if (char == null) {
                if (quoted) throw CsvFormatException("The legacy CSV ends inside a quoted value.")
                if (field.isNotEmpty() || row.isNotEmpty()) {
                    row.add(field.toString())
                    yield(row)
                }
                return@sequence
            }
            when {
                char == '"' && field.isEmpty() -> quoted = true
                char == ',' -> {
                    row.add(field.toString())
                    field.setLength(0)
                }
                char == '\n' -> {
                    row.add(field.toString())
                    yield(row)
                    row = mutableListOf()
                    field.setLength(0)
                }
                char == '\r' -> Unit
                else -> field.append(char)
            }
        }
    }
}

private class LegacyRow(private val values: List<String>) {
    operator fun get(index: Int): String = values.getOrElse(index) { "" }
}

private data class LegacyExercise(
    val id: Long,
    val type: String,
    val name: String,
    val goal: String,
    val mainMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val tertiaryMuscles: List<String>,
)

private data class LegacyModel(val id: Long, val exerciseIDs: List<Long>)

private data class LegacyRoutine(
    val id: Long,
    val name: String,
    val modelIDs: List<Long>,
    val order: Int,
)

private data class LegacyExerciseGoal(
    val id: Long,
    val routineID: Long,
    val modelID: Long,
    val sets: Int,
    val restSeconds: Int,
    val minReps: Int,
    val maxReps: Int,
) {
    fun toGoal() =
        Goal(
            minReps = minReps,
            maxReps = maxReps,
            sets = sets,
            restTime = restSeconds.seconds,
            duration = Goal.default.duration,
            distance = Goal.default.distance,
            distanceUnit = Goal.default.distanceUnit,
            calories = Goal.default.calories,
        )
}

private data class LegacyWorkout(
    val id: Long,
    val endTime: Long,
    val routineID: Long,
    val name: String,
    val modelIDs: List<Long>,
    val recordIDs: List<Long>,
)

private data class LegacyExerciseRecord(
    val id: Long,
    val exerciseID: Long,
    val sets: LegacySets,
    val comment: String,
)

private data class LegacyBodyRecord(
    val id: Long,
    val measurementID: Long,
    val left: Double,
    val right: Double,
    val unit: String,
)

private data class LegacyPreferences(private val values: Map<String, String>) {
    val planIDs: List<Long> =
        Regex("-?\\d+").findAll(values[OLD_PLAN_IDS].orEmpty()).map { it.value.toLong() }.toList()

    val hasSettings: Boolean = values.keys.any { it in SUPPORTED_SETTING_KEYS }

    fun edit(json: Json): (MutablePreferences) -> Unit = { preferences ->
        values[OLD_MASS_UNIT]?.let { value ->
            preferences[stringPreferencesKey(NEW_MASS_UNIT)] =
                if (value == "1") MassUnit.Pounds.name else MassUnit.Kilograms.name
        }
        values[OLD_DISTANCE_UNIT]?.let { value ->
            preferences[stringPreferencesKey(NEW_DISTANCE_UNIT)] =
                if (value == "1") LongDistanceUnit.Mile.name else LongDistanceUnit.Kilometer.name
        }
        values[OLD_THEME]?.let { value ->
            preferences[stringPreferencesKey(NEW_THEME)] =
                when (value) {
                    "1" -> Theme.Light
                    "2" -> Theme.Dark
                    else -> Theme.FollowSystem
                }.name
        }
        values[OLD_FIRST_DAY]?.toIntOrNull()?.toDayOfWeek()?.let { day ->
            preferences[stringPreferencesKey(NEW_FIRST_DAY)] = day.name
        }
        if (values.keys.any { it in OLD_AUTO_BACKUP_KEYS }) {
            val intervalDays = values[OLD_BACKUP_INTERVAL]?.toIntOrNull()
            val retentionDays = values[OLD_BACKUP_RETENTION]?.toIntOrNull()
            val settings =
                AutoBackupSettings(
                    enabled = false,
                    destination = null,
                    interval =
                        BackupInterval.entries.firstOrNull { it.days == intervalDays }
                            ?: BackupInterval.Daily,
                    retention =
                        BackupRetention.entries.firstOrNull { it.days == retentionDays }
                            ?: BackupRetention.TwoWeeks,
                )
            preferences[stringPreferencesKey(NEW_AUTO_BACKUP)] = json.encodeToString(settings)
        }
    }

    companion object {
        fun read(xml: String): LegacyPreferences {
            if (!xml.contains("<map")) return LegacyPreferences(emptyMap())
            val values = mutableMapOf<String, String>()
            STRING_ENTRY.findAll(xml).forEach { match ->
                val name = NAME_ATTRIBUTE.find(match.groupValues[1])?.groupValues?.get(1)
                if (name != null) values[xmlDecode(name)] = xmlDecode(match.groupValues[2])
            }
            BOOLEAN_ENTRY.findAll(xml).forEach { match ->
                val attributes = match.groupValues[1]
                val name = NAME_ATTRIBUTE.find(attributes)?.groupValues?.get(1)
                val value = VALUE_ATTRIBUTE.find(attributes)?.groupValues?.get(1)
                if (name != null && value != null) values[xmlDecode(name)] = xmlDecode(value)
            }
            return LegacyPreferences(values)
        }

        private val STRING_ENTRY =
            Regex("<string\\b([^>]*)>(.*?)</string>", setOf(RegexOption.DOT_MATCHES_ALL))
        private val BOOLEAN_ENTRY = Regex("<boolean\\b([^>]*)/?>")
        private val NAME_ATTRIBUTE = Regex("\\bname=\"([^\"]*)\"")
        private val VALUE_ATTRIBUTE = Regex("\\bvalue=\"([^\"]*)\"")

        private const val OLD_PLAN_IDS = "plan_ids"
        private const val OLD_MASS_UNIT = "list_preference_weight"
        private const val OLD_DISTANCE_UNIT = "list_preference_distance"
        private const val OLD_THEME = "key_app_theme"
        private const val OLD_FIRST_DAY = "list_preference_first_day_of_week"
        private const val OLD_BACKUP_ENABLED = "key_auto_backup_enabled"
        private const val OLD_BACKUP_DESTINATION = "key_auto_backup_dir"
        private const val OLD_BACKUP_INTERVAL = "key_auto_backup_frequency"
        private const val OLD_BACKUP_RETENTION = "key_auto_backup_delete"

        private const val NEW_MASS_UNIT = "mass_unit"
        private const val NEW_DISTANCE_UNIT = "distance_unit"
        private const val NEW_THEME = "theme"
        private const val NEW_FIRST_DAY = "first_day_of_week"
        private const val NEW_AUTO_BACKUP = "auto_backup"

        private val OLD_AUTO_BACKUP_KEYS =
            setOf(
                OLD_BACKUP_ENABLED,
                OLD_BACKUP_DESTINATION,
                OLD_BACKUP_INTERVAL,
                OLD_BACKUP_RETENTION,
            )
        private val SUPPORTED_SETTING_KEYS =
            OLD_AUTO_BACKUP_KEYS + setOf(OLD_MASS_UNIT, OLD_DISTANCE_UNIT, OLD_THEME, OLD_FIRST_DAY)
    }
}

private data class LegacySet(
    val weight: Double? = null,
    val reps: Int? = null,
    val seconds: Long? = null,
    val distance: Double? = null,
    val bodyWeight: Double? = null,
)

private data class LegacySets(
    val type: String,
    val unit: String,
    val sets: List<LegacySet>,
    val comments: Map<Int, String>,
) {
    fun bodyWeight(): Double? = sets.firstNotNullOfOrNull {
        it.bodyWeight?.takeIf { value -> value > 0 }
    }
}

private fun exercise(row: LegacyRow) =
    LegacyExercise(
        row[0].toLong(),
        row[1],
        row[2],
        row[3],
        row[4].words(),
        row[5].words(),
        row[6].words(),
    )

private fun model(row: LegacyRow) = LegacyModel(row[0].toLong(), row[1].longs())

private fun routine(row: LegacyRow) =
    LegacyRoutine(row[0].toLong(), row[1], row[2].longs(), row[3].toInt())

private fun exerciseGoal(row: LegacyRow): LegacyExerciseGoal {
    val range = row[5].filterNot { it == ' ' }.split("..").mapNotNull(String::toIntOrNull)
    return LegacyExerciseGoal(
        row[0].toLong(),
        row[1].toLong(),
        row[2].toLong(),
        row[3].toInt(),
        row[4].toInt(),
        range.getOrElse(0) { 0 },
        range.getOrElse(1) { range.getOrElse(0) { 0 } },
    )
}

private fun workout(row: LegacyRow) =
    LegacyWorkout(
        row[0].toLong(),
        row[1].toLong(),
        row[2].toLong(),
        row[3],
        row[4].longs(),
        row[5].longs(),
    )

private fun exerciseRecord(row: LegacyRow) =
    LegacyExerciseRecord(row[0].toLong(), row[1].toLong(), parseSets(row[2]), row[3])

private fun bodyRecord(row: LegacyRow) =
    LegacyBodyRecord(row[0].toLong(), row[1].toLong(), row[2].toDouble(), row[3].toDouble(), row[4])

private fun parseSets(source: String): LegacySets {
    val commentsText = source.substringAfter("<comments>", "").substringBefore("</comments>")
    val values = source.substringBefore("<comments>").trim().words()
    if (values.size < 3) return LegacySets("WEIGHT", "kg", emptyList(), emptyMap())
    val type = values[0]
    val unit = values[1]
    val data = values.drop(3)
    val width =
        when (type) {
            "WEIGHT",
            "CARDIO" -> 2
            "CALISTHENICS" -> 3
            else -> 1
        }
    val sets =
        data.chunked(width).mapNotNull { fields ->
            if (fields.size != width) return@mapNotNull null
            when (type) {
                "WEIGHT" ->
                    LegacySet(weight = fields[0].toDoubleOrNull(), reps = fields[1].toIntOrNull())
                "CALISTHENICS" ->
                    LegacySet(
                        weight = fields[0].toDoubleOrNull(),
                        bodyWeight = fields[1].toDoubleOrNull(),
                        reps = fields[2].toIntOrNull(),
                    )
                "REPS" -> LegacySet(reps = fields[0].toIntOrNull())
                "TIME" -> LegacySet(seconds = fields[0].toLongOrNull())
                "CARDIO" ->
                    LegacySet(
                        distance = fields[0].toDoubleOrNull(),
                        seconds = fields[1].toLongOrNull(),
                    )
                else -> null
            }
        }
    val comments =
        Regex("<c(\\d+)>(.*?)</c\\1>", setOf(RegexOption.DOT_MATCHES_ALL))
            .findAll(commentsText)
            .associate { it.groupValues[1].toInt() to it.groupValues[2] }
    return LegacySets(type, unit, sets, comments)
}

private fun String.toGoal(): Goal {
    val values = words().mapNotNull(String::toIntOrNull)
    return Goal(
        minReps = values.getOrElse(1) { 8 },
        maxReps = values.getOrElse(2) { 12 },
        sets = values.getOrElse(0) { 3 },
        restTime = values.getOrElse(3) { 120 }.seconds,
        duration = Goal.default.duration,
        distance = Goal.default.distance,
        distanceUnit = Goal.default.distanceUnit,
        calories = Goal.default.calories,
    )
}

private fun muscle(value: String): Muscle? =
    Muscle.entries.firstOrNull { it.name.equals(value.replace("_", ""), ignoreCase = true) }

private fun String.titlecase(): String = lowercase().replaceFirstChar(Char::titlecase)

private fun String.words(): List<String> = trim().split(Regex("\\s+")).filter(String::isNotEmpty)

private fun String.longs(): List<Long> = words().mapNotNull(String::toLongOrNull)

private fun String.key(): String = lowercase().replace('-', '_')

private fun xmlDecode(value: String): String =
    value
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")

private fun Int.toDayOfWeek(): DayOfWeek? =
    when (this) {
        1 -> DayOfWeek.SUNDAY
        2 -> DayOfWeek.MONDAY
        3 -> DayOfWeek.TUESDAY
        4 -> DayOfWeek.WEDNESDAY
        5 -> DayOfWeek.THURSDAY
        6 -> DayOfWeek.FRIDAY
        7 -> DayOfWeek.SATURDAY
        else -> null
    }

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

private fun insert(db: SupportSQLiteDatabase, sql: String, vararg values: Any?): Long {
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
