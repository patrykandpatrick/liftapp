@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout.Exercise
import kotlin.time.Duration

class WorkoutIterator(val items: List<Item>) : List<WorkoutIterator.Item> by items {
    data class Item(
        val exercise: Exercise,
        val exerciseIndex: Int,
        val setIndex: Int,
        val restBefore: Duration = Duration.ZERO,
    ) {
        val set = exercise.sets[setIndex]

        val isCompleted: Boolean = set.isCompleted
    }

    fun getItem(exerciseIndex: Int, setIndex: Int): Item = items.first {
        it.exerciseIndex == exerciseIndex && it.setIndex == setIndex
    }

    fun getNextIncomplete(item: Item? = null): Item? {
        val startIndex =
            item?.let { selected ->
                items.indexOfFirst {
                    it.exerciseIndex == selected.exerciseIndex && it.setIndex == selected.setIndex
                } + 1
            } ?: 0
        for (i in startIndex until items.size) {
            val currentItem = items[i]
            if (!currentItem.isCompleted) {
                return currentItem
            }
        }
        return null
    }

    companion object {
        fun fromWorkout(workout: EditableWorkout): WorkoutIterator {
            val indexedExercises = workout.exercises.withIndex().toList()
            val orderedItems =
                indexedExercises
                    .groupBy { it.value.workoutItemID }
                    .values
                    .sortedBy { exercises -> exercises.first().value.workoutItemOrder }

            val itemsWithoutRest = buildList {
                orderedItems.forEach { itemExercises ->
                    val orderedExercises = itemExercises.sortedBy { it.value.exerciseOrder }
                    if (orderedExercises.first().value.isSuperset) {
                        val setCount = orderedExercises.maxOf { it.value.sets.size }
                        repeat(setCount) { setIndex ->
                            orderedExercises.forEach { (exerciseIndex, exercise) ->
                                if (setIndex < exercise.sets.size) {
                                    add(Item(exercise, exerciseIndex, setIndex))
                                }
                            }
                        }
                    } else {
                        orderedExercises.forEach { (exerciseIndex, exercise) ->
                            exercise.sets.indices.forEach { setIndex ->
                                add(Item(exercise, exerciseIndex, setIndex))
                            }
                        }
                    }
                }
            }

            val items = itemsWithoutRest.mapIndexed { index, item ->
                val previous = itemsWithoutRest.getOrNull(index - 1)
                item.copy(restBefore = restBetween(previous, item))
            }

            return WorkoutIterator(items)
        }

        private fun restBetween(previous: Item?, current: Item): Duration {
            if (previous == null) return Duration.ZERO
            if (previous.exercise.workoutItemID != current.exercise.workoutItemID) {
                return previous.exercise.goal.restTime
            }
            if (!current.exercise.isSuperset) return current.exercise.goal.restTime
            return if (current.setIndex != previous.setIndex) {
                current.exercise.goal.restTime
            } else {
                Duration.ZERO
            }
        }
    }
}
