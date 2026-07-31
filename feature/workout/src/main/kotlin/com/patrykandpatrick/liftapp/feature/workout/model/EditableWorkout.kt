package com.patrykandpatrick.liftapp.feature.workout.model

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.Workout
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
    val selectedExerciseAndSet: WorkoutIterator.Item? = null,
) : Serializable {

    val items: List<Item> = groupExercises(exercises)

    val iterator = WorkoutIterator.fromWorkout(this)

    val nextIncompleteItem = iterator.getNextIncomplete()

    val startPageIndex: Int =
        nextIncompleteItem?.let { next ->
            items.indexOfFirst { it.id == next.exercise.workoutItemID }
        } ?: items.size

    val nextExerciseSet: WorkoutIterator.Item? = nextIncompleteItem

    val completedSetCount: Int = exercises.sumOf { it.completedSetCount }

    val summary: WorkoutPage.Summary
        get() = pages.last() as WorkoutPage.Summary

    @Stable
    data class Item(val id: Long, val exercises: List<Exercise>) : Serializable {
        val isSuperset: Boolean = exercises.firstOrNull()?.isSuperset == true

        val setCount: Int = exercises.maxOfOrNull { it.sets.size } ?: 0

        val completedSetCount: Int =
            if (isSuperset) {
                (0 until setCount).count { setIndex ->
                    exercises.all { exercise ->
                        exercise.sets.getOrNull(setIndex)?.isCompleted != false
                    }
                }
            } else {
                exercises.singleOrNull()?.completedSetCount ?: 0
            }

        val allSetsCompleted: Boolean = setCount > 0 && completedSetCount == setCount
    }

    @Stable
    data class Exercise(
        val id: Long,
        val name: Name,
        val exerciseType: ExerciseType,
        val mainMuscles: List<Muscle>,
        val secondaryMuscles: List<Muscle>,
        val tertiaryMuscles: List<Muscle>,
        val goal: Workout.Goal,
        val notes: String,
        val sets: List<EditableExerciseSet<ExerciseSet>>,
        val previousWorkoutSets: List<ExerciseSet>,
        val workoutItemID: Long = id,
        val workoutItemType: RoutineItemType = RoutineItemType.Exercise,
        val workoutItemOrder: Int = 0,
        val exerciseOrder: Int = 0,
    ) : Serializable {
        val isSuperset: Boolean
            get() = workoutItemType == RoutineItemType.Superset

        val firstIncompleteSetIndex: Int = sets.indexOfFirst { !it.isCompleted }

        val completedSets = sets.filter { it.isCompleted }

        val completedSetCount: Int = completedSets.size

        val formattedBodyWeight: String? =
            (sets.firstOrNull() as? EditableExerciseSet.Calisthenics)?.formattedBodyWeight
    }

    companion object {
        fun groupExercises(exercises: List<Exercise>): List<Item> =
            exercises
                .groupBy(Exercise::workoutItemID)
                .values
                .sortedBy { it.first().workoutItemOrder }
                .map { itemExercises ->
                    Item(
                        id = itemExercises.first().workoutItemID,
                        exercises = itemExercises.sortedBy(Exercise::exerciseOrder),
                    )
                }
    }
}
