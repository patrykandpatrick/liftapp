package com.patrykandpatryk.liftapp.domain.workout

import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration

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
    ) : Serializable {
        val firstIncompleteSetIndex: Int = sets.indexOfFirst { !it.isComplete }
    }
}
