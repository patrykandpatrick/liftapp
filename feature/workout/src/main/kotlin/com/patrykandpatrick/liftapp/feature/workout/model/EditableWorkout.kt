package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.time.Duration

@Stable
data class EditableWorkout(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?,
    val notes: String,
    val exercises: List<Exercise>,
    val pages: List<WorkoutPage>,
) : Serializable {

    val firstIncompleteExerciseIndex: Int =
        exercises.indexOfFirst { it.firstIncompleteSetIndex != -1 }.takeIf { it != -1 }
            ?: exercises.lastIndex

    val nextExerciseSet: NextExerciseSet? =
        getNextExerciseSet(exercises, firstIncompleteExerciseIndex)

    val completedSetCount: Int = exercises.sumOf { it.completedSetCount }

    val summary: WorkoutPage.Summary = pages.last() as WorkoutPage.Summary

    @Stable
    data class Exercise(
        val id: Long,
        val name: Name,
        val exerciseType: ExerciseType,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle>,
        val tertiaryMuscles: List<Muscle>,
        val goal: Workout.Goal,
        val sets: List<EditableExerciseSet<ExerciseSet>>,
    ) : Serializable {
        val firstIncompleteSetIndex: Int = sets.indexOfFirst { !it.isCompleted }

        val firstIncompleteOrLastSetIndex: Int =
            firstIncompleteSetIndex.let { if (it == -1) sets.lastIndex else it }

        val completedSetCount: Int = sets.count { it.isCompleted }
    }

    data class NextExerciseSet(val exercise: Exercise, val setIndex: Int) {
        val set = exercise.sets[setIndex]

        val restTime: Duration = exercise.goal.restTime
    }

    companion object {
        private fun getNextExerciseSet(exercises: List<Exercise>, index: Int): NextExerciseSet? {
            val exercise = exercises.getOrNull(index) ?: return null
            val setIndex = exercise.firstIncompleteSetIndex
            if (setIndex == -1) return null
            return NextExerciseSet(exercise, setIndex)
        }
    }
}
