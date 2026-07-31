package com.patrykandpatrick.liftapp.feature.workout.model

import com.patrykandpatrick.liftapp.core.text.IntTextFieldState
import com.patrykandpatrick.liftapp.core.text.StringTextFieldState
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.Workout
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds
import org.junit.Test

class WorkoutIteratorTest {

    @Test
    fun `superset exercises alternate by round and rest only follows a completed round`() {
        val supersetA = exercise(id = 1, itemID = 10, exerciseOrder = 0, isSuperset = true)
        val supersetB = exercise(id = 2, itemID = 10, exerciseOrder = 1, isSuperset = true)
        val regular = exercise(id = 3, itemID = 20, exerciseOrder = 0, isSuperset = false)

        val iterator = WorkoutIterator.fromWorkout(workout(supersetA, supersetB, regular))

        assertEquals(listOf(1L, 2L, 1L, 2L, 3L, 3L), iterator.map { it.exercise.id })
        assertEquals(listOf(0, 0, 1, 1, 0, 1), iterator.map { it.setIndex })
        assertEquals(
            listOf(0L, 0L, 90L, 0L, 90L, 120L),
            iterator.map { it.restBefore.inWholeSeconds },
        )
    }

    @Test
    fun `superset rest follows a new round when the first exercise has fewer sets`() {
        val supersetA =
            exercise(id = 1, itemID = 10, exerciseOrder = 0, isSuperset = true, setCount = 1)
        val supersetB =
            exercise(id = 2, itemID = 10, exerciseOrder = 1, isSuperset = true, setCount = 2)

        val iterator = WorkoutIterator.fromWorkout(workout(supersetA, supersetB))

        assertEquals(listOf(1L, 2L, 2L), iterator.map { it.exercise.id })
        assertEquals(listOf(0, 0, 1), iterator.map { it.setIndex })
        assertEquals(listOf(0L, 0L, 90L), iterator.map { it.restBefore.inWholeSeconds })
    }

    @Test
    fun `workout groups superset exercises into one ordered item`() {
        val supersetB = exercise(id = 2, itemID = 10, exerciseOrder = 1, isSuperset = true)
        val regular = exercise(id = 3, itemID = 20, exerciseOrder = 0, isSuperset = false)
        val supersetA = exercise(id = 1, itemID = 10, exerciseOrder = 0, isSuperset = true)

        val workout = workout(supersetB, regular, supersetA)

        assertEquals(listOf(10L, 20L), workout.items.map { it.id })
        assertEquals(listOf(1L, 2L), workout.items.first().exercises.map { it.id })
        assertEquals(true, workout.items.first().isSuperset)
    }

    @Test
    fun `next superset exercise still selects the superset page`() {
        val supersetA =
            exercise(
                id = 1,
                itemID = 10,
                exerciseOrder = 0,
                isSuperset = true,
                completed = true,
            )
        val supersetB = exercise(id = 2, itemID = 10, exerciseOrder = 1, isSuperset = true)
        val regular = exercise(id = 3, itemID = 20, exerciseOrder = 0, isSuperset = false)

        val workout = workout(supersetA, supersetB, regular)

        assertEquals(2L, workout.nextIncompleteItem?.exercise?.id)
        assertEquals(0, workout.startPageIndex)
    }

    @Test
    fun `superset progress counts only rounds completed by every exercise`() {
        val supersetA =
            exercise(
                id = 1,
                itemID = 10,
                exerciseOrder = 0,
                isSuperset = true,
                completedSetIndices = setOf(0, 1),
            )
        val supersetB =
            exercise(
                id = 2,
                itemID = 10,
                exerciseOrder = 1,
                isSuperset = true,
                completedSetIndices = setOf(0),
            )
        val supersetC =
            exercise(
                id = 3,
                itemID = 10,
                exerciseOrder = 2,
                isSuperset = true,
                completedSetIndices = setOf(0, 1),
            )

        val item = workout(supersetA, supersetB, supersetC).items.single()

        assertEquals(2, item.setCount)
        assertEquals(1, item.completedSetCount)
        assertEquals(false, item.allSetsCompleted)
    }

    @Test
    fun `missing superset sets do not prevent later rounds from completing`() {
        val shorter =
            exercise(
                id = 1,
                itemID = 10,
                exerciseOrder = 0,
                isSuperset = true,
                setCount = 1,
                completed = true,
            )
        val longer =
            exercise(
                id = 2,
                itemID = 10,
                exerciseOrder = 1,
                isSuperset = true,
                setCount = 2,
                completed = true,
            )

        val item = workout(shorter, longer).items.single()

        assertEquals(2, item.completedSetCount)
        assertEquals(true, item.allSetsCompleted)
    }

    private fun exercise(
        id: Long,
        itemID: Long,
        exerciseOrder: Int,
        isSuperset: Boolean,
        setCount: Int = 2,
        completed: Boolean = false,
        completedSetIndices: Set<Int> = if (completed) (0 until setCount).toSet() else emptySet(),
    ) =
        EditableWorkout.Exercise(
            id = id,
            name = Name.Raw("Exercise $id"),
            exerciseType = ExerciseType.Reps,
            mainMuscles = emptyList(),
            secondaryMuscles = emptyList(),
            tertiaryMuscles = emptyList(),
            goal =
                Workout.Goal.default.copy(restTime = if (isSuperset) 90.seconds else 120.seconds),
            notes = "",
            sets =
                List(setCount) { setIndex ->
                    EditableExerciseSet.Reps(
                        reps = if (setIndex in completedSetIndices) 1 else 0,
                        repsInput =
                            IntTextFieldState(
                                initialValue = if (setIndex in completedSetIndices) "1" else "0"
                            ),
                        notesInput = StringTextFieldState(),
                    ) as EditableExerciseSet<ExerciseSet>
                },
            previousWorkoutSets = emptyList(),
            workoutItemID = itemID,
            workoutItemType =
                if (isSuperset) RoutineItemType.Superset else RoutineItemType.Exercise,
            workoutItemOrder = if (isSuperset) 0 else 1,
            exerciseOrder = exerciseOrder,
        )

    private fun workout(vararg exercises: EditableWorkout.Exercise) =
        EditableWorkout(
            id = 1,
            name = "Workout",
            startDate = LocalDateTime.MIN,
            endDate = null,
            notes = "",
            exercises = exercises.toList(),
            pages = emptyList(),
        )
}
