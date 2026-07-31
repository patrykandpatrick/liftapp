package com.patrykandpatrick.liftapp.functionality.database.migration

import com.patrykandpatrick.liftapp.domain.goal.Goal
import kotlin.time.Duration.Companion.seconds

/**
 * Rows written by the published app, whether read from its live database during the schema
 * migration or from the CSV files inside one of its backup archives.
 */
data class LegacyExercise(
    val id: Long,
    val type: String,
    val name: String,
    val goal: String,
    val mainMuscles: List<String>,
    val secondaryMuscles: List<String>,
    val tertiaryMuscles: List<String>,
)

/** One exercise slot of a routine; more than one exercise ID means a superset. */
data class LegacyModel(val id: Long, val exerciseIDs: List<Long>)

data class LegacyRoutine(val id: Long, val name: String, val modelIDs: List<Long>, val order: Int)

data class LegacyExerciseGoal(
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

/** A `RoutineRecord` row: the ID doubles as the start time, and `endTime` 0 means unfinished. */
data class LegacyWorkout(
    val id: Long,
    val endTime: Long,
    val routineID: Long,
    val name: String,
    val modelIDs: List<Long>,
    val recordIDs: List<Long>,
)

data class LegacyExerciseRecord(
    val id: Long,
    val exerciseID: Long,
    val sets: LegacySets,
    val comment: String,
)

data class LegacyBodyRecord(
    val id: Long,
    val measurementID: Long,
    val left: Double,
    val right: Double,
    val unit: String,
)

data class LegacySet(
    val weight: Double? = null,
    val reps: Int? = null,
    val seconds: Long? = null,
    val distance: Double? = null,
    val bodyWeight: Double? = null,
)

data class LegacySets(
    val type: String,
    val unit: String,
    val sets: List<LegacySet>,
    val comments: Map<Int, String>,
) {
    fun bodyWeight(): Double? = sets.firstNotNullOfOrNull {
        it.bodyWeight?.takeIf { value -> value > 0 }
    }
}

/** Splits one of the published app's space-separated list columns. */
fun legacyWords(text: String?): List<String> =
    text.orEmpty().trim().split(WHITESPACE).filter(String::isNotEmpty)

/** Reads one of the published app's space-separated ID list columns. */
fun legacyLongs(text: String?): List<Long> = legacyWords(text).mapNotNull(String::toLongOrNull)

/**
 * Reads an `ExerciseRecord.ex_records` value: `TYPE unit goalSets` followed by a fixed number of
 * values per set, and a pseudo-XML comment block keyed by set index.
 */
fun parseLegacySets(source: String): LegacySets {
    val commentsText = source.substringAfter("<comments>", "").substringBefore("</comments>")
    val values = legacyWords(source.substringBefore("<comments>"))
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

/** Reads an `Exercise.exercise_goal` value: `sets minReps maxReps breakTime`. */
fun parseLegacyGoal(text: String): Goal {
    val values = legacyWords(text).mapNotNull(String::toIntOrNull)
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

private val WHITESPACE = Regex("\\s+")
