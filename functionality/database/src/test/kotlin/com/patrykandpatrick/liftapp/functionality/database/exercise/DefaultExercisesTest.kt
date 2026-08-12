package com.patrykandpatrick.liftapp.functionality.database.exercise

import com.patrykandpatrick.liftapp.domain.exercise.Exercise
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.exercise.carriesLoad
import com.patrykandpatrick.liftapp.domain.exercise.hasRole
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.functionality.database.string.ExerciseStringResource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

/**
 * Guards the hand-authored classification. `Migration13To14` replays this list onto every upgrading
 * device by ID, so a gap here is a gap on a user's phone.
 */
class DefaultExercisesTest {

    private val exercises = DefaultExercises.exercises

    private fun seeded(resource: ExerciseStringResource): Exercise.Insert = exercises.single {
        (it.name as? Name.Resource)?.resource == resource
    }

    @Test
    fun `Every exercise that can have a role has one, and no other exercise claims one`() {
        exercises.forEach { exercise ->
            if (exercise.exerciseType.hasRole) {
                assertNotNull(exercise.role, "${exercise.name} has no role.")
            } else {
                assertNull(exercise.role, "${exercise.name} is ${exercise.exerciseType}.")
            }
        }
    }

    @Test
    fun `Nothing that carries no external load is offered a load progression`() {
        exercises
            .filterNot { it.exerciseType.carriesLoad }
            .forEach { exercise ->
                assertEquals(
                    ProgressionPolicy.RepsOnly,
                    exercise.progressionPolicy,
                    "${exercise.name} is ${exercise.exerciseType}.",
                )
            }
    }

    @Test
    fun `A load increment is present exactly where a load can be added`() {
        exercises
            .filter { it.exerciseType.carriesLoad }
            .forEach { exercise ->
                val increment = exercise.loadIncrement
                assertNotNull(increment, "${exercise.name} has no load increment.")
                assertTrue(increment > 0, "${exercise.name} has a non-positive increment.")
            }
    }

    // The published app used the same IDs for these rows, and both the legacy migration and
    // Migration13To14 address them positionally. Inserting or reordering an entry silently
    // reclassifies somebody else's exercise.
    @Test
    fun `The catalog stays append-only`() {
        assertEquals(101, exercises.size)
        assertEquals(ExerciseType.Cardio, exercises.first().exerciseType)
        assertEquals(listOf(Muscle.Quadriceps), exercises.last().mainMuscles)
    }

    @Test
    fun `The deltoid is never left aggregated`() {
        val delts = setOf(Muscle.FrontDelts, Muscle.SideDelts, Muscle.RearDelts)
        val exercisesWithDelts = exercises.filter { exercise ->
            (exercise.mainMuscles + exercise.secondaryMuscles + exercise.tertiaryMuscles).any {
                it in delts
            }
        }

        assertTrue(exercisesWithDelts.size > 40, "Only ${exercisesWithDelts.size} touch the delts.")
        exercisesWithDelts.forEach { exercise ->
            val assigned =
                (exercise.mainMuscles + exercise.secondaryMuscles + exercise.tertiaryMuscles)
                    .filter { it in delts }
            assertEquals(1, assigned.distinct().size, "${exercise.name} names several delt heads.")
        }
    }

    // The published app had these two transposed. Abduction takes the leg away from the midline
    // and adduction pulls it back, so each works the group it is named after.
    @Test
    fun `The hip machines name the muscle they actually work`() {
        assertEquals(
            listOf(Muscle.Abductors),
            seeded(ExerciseStringResource.CableHipAbduction).mainMuscles,
        )
        assertEquals(
            listOf(Muscle.Adductors),
            seeded(ExerciseStringResource.CableHipAdduction).mainMuscles,
        )
    }

    @Test
    fun `Primary compounds are the exception, not the rule`() {
        val byRole = exercises.groupingBy { it.role }.eachCount()

        assertEquals(11, byRole[null], "Cardio and Time entries.")
        assertTrue(
            byRole.getValue(ExerciseRole.PrimaryCompound) <
                byRole.getValue(ExerciseRole.SecondaryCompound),
            "More primary compounds than secondary ones: $byRole",
        )
    }
}
