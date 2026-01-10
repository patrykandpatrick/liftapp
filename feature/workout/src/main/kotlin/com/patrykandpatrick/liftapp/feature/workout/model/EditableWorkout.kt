package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.workout.ExerciseSet
import com.patrykandpatryk.liftapp.domain.workout.Workout
import java.io.Serializable
import java.time.LocalDateTime

@Stable
data class EditableWorkout(
    val id: Long,
    val name: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime?,
    val notes: String,
    val exercises: List<Exercise>,
    val pages: List<WorkoutPage>,
    val selectedSelectedExerciseAndSet: WorkoutIterator.Item? = null,
) : Serializable {

    val iterator = WorkoutIterator.fromWorkout(this)

    val nextIncompleteItem = iterator.getNextIncomplete()

    val startPageIndex: Int = nextIncompleteItem?.exerciseIndex ?: exercises.size

    val nextExerciseSet: WorkoutIterator.Item? = nextIncompleteItem

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
        val previousWorkoutSets: List<ExerciseSet>,
    ) : Serializable {
        val firstIncompleteSetIndex: Int = sets.indexOfFirst { !it.isCompleted }

        val completedSets = sets.filter { it.isCompleted }

        val completedSetCount: Int = completedSets.size

        val formattedBodyWeight: String? =
            (sets.firstOrNull() as? EditableExerciseSet.Calisthenics)?.formattedBodyWeight
    }
}
