@file:Suppress("JavaDefaultMethodsNotOverriddenByDelegation")

package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.feature.workout.model.EditableWorkout.Exercise
import java.io.Serializable
import kotlin.time.Duration

class WorkoutIterator(val items: List<Item>) : List<WorkoutIterator.Item> by items {
    data class Item(val exercise: Exercise, val exerciseIndex: Int, val setIndex: Int) :
        Serializable {
        val set = exercise.sets[setIndex]
        val setCount = exercise.sets.size

        operator fun component4() = set

        val isCompleted: Boolean = set.isCompleted
        val restTime: Duration = exercise.goal.restTime
    }

    fun getItem(exerciseIndex: Int, setIndex: Int): Item =
        items.first { it.exerciseIndex == exerciseIndex && it.setIndex == setIndex }

    fun getNextIncomplete(item: Item? = null): Item? {
        val startIndex = item?.let { items.indexOf(it) + 1 } ?: 0
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
            val items = mutableListOf<Item>()
            workout.exercises.forEachIndexed { exerciseIndex, exercise ->
                exercise.sets.forEachIndexed { setIndex, _ ->
                    items.add(Item(exercise, exerciseIndex, setIndex))
                }
            }
            return WorkoutIterator(items)
        }
    }
}
