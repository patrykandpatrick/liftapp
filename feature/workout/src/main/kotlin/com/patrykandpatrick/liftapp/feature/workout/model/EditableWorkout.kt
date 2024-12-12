package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration

@Stable
data class EditableWorkout(
    val id: Long,
    val name: String,
    val date: LocalDateTime,
    val duration: Duration,
    val notes: String,
    val exercises: List<Exercise>,
) : Serializable {

    val firstIncompleteExerciseIndex: Int =
        exercises.indexOfFirst { it.firstIncompleteSetIndex != -1 }.takeIf { it != -1 }
            ?: exercises.lastIndex

    @Stable
    data class Exercise(
        val id: Long,
        val name: Name,
        val exerciseType: ExerciseType,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle>,
        val tertiaryMuscles: List<Muscle>,
        val goal: Goal,
        val sets: List<EditableExerciseSet>,
    ) : Serializable {
        val firstIncompleteSetIndex: Int = sets.indexOfFirst { !it.isComplete }

        @Stable
        fun isSetActive(set: EditableExerciseSet): Boolean =
            sets.indexOf(set) == firstIncompleteSetIndex

        @Stable
        fun isSetEnabled(set: EditableExerciseSet): Boolean = isSetActive(set) || set.isComplete
    }
}
