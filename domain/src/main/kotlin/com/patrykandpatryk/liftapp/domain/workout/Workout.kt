package com.patrykandpatryk.liftapp.domain.workout

import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class Workout(
    val id: Long,
    val name: String,
    val date: LocalDateTime,
    val duration: Duration,
    val notes: String,
    val exercises: List<Exercise>,
) : Serializable {
    data class Exercise(
        val id: Long,
        val name: Name,
        val exerciseType: ExerciseType,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle>,
        val tertiaryMuscles: List<Muscle>,
        val goal: Goal,
        val sets: List<ExerciseSet>,
    ) : Serializable

    data class Goal(
        val id: Long,
        val minReps: Int,
        val maxReps: Int,
        val sets: Int,
        val restTime: Duration,
        val duration: Duration,
        val distance: Double,
        val distanceUnit: LongDistanceUnit,
        val calories: Double,
    ) {
        companion object {
            val default =
                Goal(0, 8, 12, 3, 2L.minutes, 5L.minutes, 0.5, LongDistanceUnit.Kilometer, 50.0)
        }
    }
}
