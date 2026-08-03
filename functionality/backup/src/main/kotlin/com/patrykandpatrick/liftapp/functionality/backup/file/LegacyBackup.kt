package com.patrykandpatrick.liftapp.functionality.backup.file

import androidx.datastore.preferences.core.MutablePreferences
import androidx.sqlite.db.SupportSQLiteDatabase
import com.patrykandpatrick.liftapp.domain.backup.BackupDataType
import com.patrykandpatrick.liftapp.functionality.database.migration.LegacyBodyRecord
import com.patrykandpatrick.liftapp.functionality.database.migration.LegacyDatabaseWriter
import com.patrykandpatrick.liftapp.functionality.database.migration.LegacyExercise
import com.patrykandpatrick.liftapp.functionality.database.migration.LegacyExerciseGoal
import com.patrykandpatrick.liftapp.functionality.database.migration.LegacyExerciseRecord
import com.patrykandpatrick.liftapp.functionality.database.migration.LegacyModel
import com.patrykandpatrick.liftapp.functionality.database.migration.LegacyRoutine
import com.patrykandpatrick.liftapp.functionality.database.migration.LegacyWorkout
import com.patrykandpatrick.liftapp.functionality.database.migration.legacyLongs
import com.patrykandpatrick.liftapp.functionality.database.migration.legacyWords
import com.patrykandpatrick.liftapp.functionality.database.migration.parseLegacySets
import com.patrykandpatrick.liftapp.functionality.preference.legacy.LegacyPreferences
import java.io.InputStream
import java.io.Reader
import kotlinx.serialization.json.Json

/**
 * Reader and one-way schema migration for backups written by the original LiftApp. The CSV rows are
 * replayed through [LegacyDatabaseWriter], the same conversion an update-in-place database
 * migration uses.
 */
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
        val writer = LegacyDatabaseWriter(json)
        var restored = false
        val routinesWanted = BackupDataType.Routines in types
        val workoutsWanted = BackupDataType.Workouts in types

        if (routinesWanted || workoutsWanted) {
            val exercises =
                rows(LegacyKind.Exercises, routinesWanted, workoutsWanted).map(::exercise)
            exercises.distinctBy { it.id }.forEach { writer.restoreExercise(db, it) }

            val models =
                if (version == 1) {
                    emptyMap()
                } else {
                    rows(LegacyKind.ExerciseModels, routinesWanted, workoutsWanted)
                        .map(::model)
                        .associateBy { it.id }
                }
            // Version 1 predates exercise models: the ID lists hold exercise IDs directly.
            val exerciseIDs = { modelID: Long ->
                if (version == 1) listOf(modelID) else models[modelID]?.exerciseIDs.orEmpty()
            }
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
                    .forEach { writer.restoreRoutine(db, it, exerciseIDs, goals) }
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
                    .forEach { writer.restoreWorkout(db, it, exerciseIDs, goals, records) }
                restored = restored || records.isNotEmpty() || workouts.isNotEmpty()
            }

            if (BackupDataType.TrainingPlans in types && legacyPreferences.planIDs.isNotEmpty()) {
                writer.writePlan(db, LEGACY_PLAN_ID, "Imported plan", legacyPreferences.planIDs)
                restored = true
            }
        }

        if (BackupDataType.BodyMeasurements in types) {
            val bodyRecords = rows(LegacyKind.BodyRecords, false, false)
            bodyRecords.map(::bodyRecord).forEach {
                writer.restoreBodyRecord(db, it)
            }
            restored = restored || bodyRecords.isNotEmpty()
        }
        return restored
    }

    fun preferenceEdit(json: Json): ((MutablePreferences) -> Unit)? =
        legacyPreferences.edit(json).takeIf { BackupDataType.Settings in contents }

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

private fun exercise(row: LegacyRow) =
    LegacyExercise(
        row[0].toLong(),
        row[1],
        row[2],
        row[3],
        legacyWords(row[4]),
        legacyWords(row[5]),
        legacyWords(row[6]),
    )

private fun model(row: LegacyRow) = LegacyModel(row[0].toLong(), legacyLongs(row[1]))

private fun routine(row: LegacyRow) =
    LegacyRoutine(row[0].toLong(), row[1], legacyLongs(row[2]), row[3].toInt())

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
        legacyLongs(row[4]),
        legacyLongs(row[5]),
    )

private fun exerciseRecord(row: LegacyRow) =
    LegacyExerciseRecord(row[0].toLong(), row[1].toLong(), parseLegacySets(row[2]), row[3])

private fun bodyRecord(row: LegacyRow) =
    LegacyBodyRecord(row[0].toLong(), row[1].toLong(), row[2].toDouble(), row[3].toDouble(), row[4])

private fun String.key(): String = lowercase().replace('-', '_')
