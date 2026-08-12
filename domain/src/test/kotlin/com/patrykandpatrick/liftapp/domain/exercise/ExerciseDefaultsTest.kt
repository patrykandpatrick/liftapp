package com.patrykandpatrick.liftapp.domain.exercise

import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test

class ExerciseDefaultsTest {

    @Test
    fun `Only a type that logs no effort against a muscle is denied a role`() {
        assertEquals(
            listOf(ExerciseType.Cardio, ExerciseType.Time),
            ExerciseType.entries.filterNot { it.hasRole },
        )
    }

    @Test
    fun `Only a type whose sets record a weight can carry a load`() {
        assertEquals(
            listOf(ExerciseType.Weight, ExerciseType.Calisthenics),
            ExerciseType.entries.filter { it.carriesLoad },
        )
    }

    @Test
    fun `A type with no role suggests none, however many muscles are listed`() {
        ExerciseType.entries
            .filterNot { it.hasRole }
            .forEach { type ->
                (0..10).forEach { muscleCount ->
                    assertNull(suggestExerciseRole(type, muscleCount))
                }
            }
    }

    @Test
    fun `A type that carries no load has only one possible progression`() {
        ExerciseType.entries
            .filterNot { it.carriesLoad }
            .forEach { type ->
                assertEquals(ProgressionPolicy.RepsOnly, defaultProgressionPolicy(type), type.name)
            }
    }

    @Test
    fun `The muscle count separates the three roles at three and at six`() {
        assertEquals(
            listOf(
                ExerciseRole.Isolation,
                ExerciseRole.Isolation,
                ExerciseRole.Isolation,
                ExerciseRole.Isolation,
                ExerciseRole.SecondaryCompound,
                ExerciseRole.SecondaryCompound,
                ExerciseRole.SecondaryCompound,
                ExerciseRole.PrimaryCompound,
                ExerciseRole.PrimaryCompound,
            ),
            (0..8).map { suggestExerciseRole(ExerciseType.Weight, it) },
        )
    }

    // The cases the reference calls out as the heuristic's characteristic failures. They pin the
    // behaviour rather than endorse it: most of these guesses are wrong, which is exactly why the
    // guess is only ever pre-selected and never stored.
    @Test
    fun `The known misclassifications still misclassify`() {
        assertEquals(ExerciseRole.Isolation, weightRole(muscleCount = 1)) // Good mornings
        assertEquals(ExerciseRole.Isolation, weightRole(muscleCount = 2)) // Leg press
        assertEquals(ExerciseRole.Isolation, weightRole(muscleCount = 3)) // Lat pulldown
        assertEquals(ExerciseRole.SecondaryCompound, weightRole(muscleCount = 4)) // Barbell rows
        assertEquals(ExerciseRole.SecondaryCompound, weightRole(muscleCount = 6)) // Leg raise
    }

    @Test
    fun `Counting muscles counts all three tiers`() {
        val benchPress =
            Exercise(
                id = 1,
                displayName = "Bench press",
                name = Name.Raw("Bench press"),
                exerciseType = ExerciseType.Weight,
                primaryMuscles = listOf(Muscle.Chest, Muscle.Triceps),
                secondaryMuscles = listOf(Muscle.FrontDelts),
                tertiaryMuscles = listOf(Muscle.Forearms, Muscle.Abs, Muscle.Lats),
            )

        assertEquals(
            weightRole(muscleCount = 6),
            suggestExerciseRole(ExerciseType.Weight, benchPress),
        )
    }

    @Test
    fun `Only types that can carry load start on a load-based progression`() {
        ExerciseType.entries
            .filter { it.carriesLoad }
            .forEach { type ->
                assertEquals(
                    ProgressionPolicy.DoubleProgression,
                    defaultProgressionPolicy(type),
                    type.name,
                )
            }
    }

    private fun weightRole(muscleCount: Int) = suggestExerciseRole(ExerciseType.Weight, muscleCount)
}
