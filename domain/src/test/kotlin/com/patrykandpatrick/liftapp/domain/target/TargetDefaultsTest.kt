package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import org.junit.Test

class TargetDefaultsTest {

    @Test
    fun `Rep range follows the role, so a heavy compound is not prescribed a curl's reps`() {
        val primary = TargetDefaults.of(ExerciseType.Weight, ExerciseRole.PrimaryCompound)
        val isolation = TargetDefaults.of(ExerciseType.Weight, ExerciseRole.Isolation)

        assertEquals(4..6, primary.sets.first().let { it.minReps..it.maxReps })
        assertEquals(10..15, isolation.sets.first().let { it.minReps..it.maxReps })
    }

    @Test
    fun `Rest falls as the load does`() {
        assertEquals(
            3.minutes,
            TargetDefaults.of(ExerciseType.Weight, ExerciseRole.PrimaryCompound).restTime,
        )
        assertEquals(
            2.minutes,
            TargetDefaults.of(ExerciseType.Weight, ExerciseRole.SecondaryCompound).restTime,
        )
        assertEquals(
            90.seconds,
            TargetDefaults.of(ExerciseType.Weight, ExerciseRole.Isolation).restTime,
        )
    }

    @Test
    fun `RIR tightens as the load lightens`() {
        val primary = TargetDefaults.of(ExerciseType.Weight, ExerciseRole.PrimaryCompound)
        val isolation = TargetDefaults.of(ExerciseType.Weight, ExerciseRole.Isolation)

        assertEquals(2, primary.sets.first().rir)
        assertEquals(1, isolation.sets.first().rir)
    }

    @Test
    fun `A weighted pull-up takes the bodyweight-limited range, not its role's`() {
        val pullUp = TargetDefaults.of(ExerciseType.Calisthenics, ExerciseRole.PrimaryCompound)

        assertEquals(8..15, pullUp.sets.first().let { it.minReps..it.maxReps })
    }

    @Test
    fun `An unclassified exercise still gets a usable prescription`() {
        val unclassified = TargetDefaults.of(ExerciseType.Weight, role = null)

        assertEquals(8..12, unclassified.sets.first().let { it.minReps..it.maxReps })
        assertEquals(TargetDefaults.SET_COUNT, unclassified.sets.size)
    }

    @Test
    fun `Cardio is prescribed a distance and a duration rather than reps`() {
        val run = TargetDefaults.of(ExerciseType.Cardio, role = null).sets.first()

        assertEquals(0, run.maxReps)
        assertTrue(run.distance > 0)
        assertTrue(run.duration > 0.minutes)
    }

    @Test
    fun `Load is never prescribed, because nothing here knows the lifter's numbers`() {
        ExerciseType.entries.forEach { type ->
            (ExerciseRole.entries + null).forEach { role ->
                TargetDefaults.of(type, role).sets.forEach { set ->
                    assertNull(set.loadPercent, "$type/$role prescribes a load.")
                }
            }
        }
    }

    @Test
    fun `The set count is the length of the list, which is what a volume ramp needs`() {
        val template = TargetDefaults.of(ExerciseType.Weight, ExerciseRole.Isolation, setCount = 5)
        val target = template.at(planID = 1, cycleIndex = 2, planItemOrderIndex = 0, exerciseID = 9)

        assertEquals(5, target.setCount)
        assertEquals(5, target.sets.size)
    }

    @Test
    fun `Every combination answers, so no exercise can be left without a prescription`() {
        ExerciseType.entries.forEach { type ->
            (ExerciseRole.entries + null).forEach { role ->
                val template = TargetDefaults.of(type, role)
                assertTrue(template.sets.isNotEmpty(), "$type/$role prescribes no sets.")
                assertNotNull(template.restTime)
            }
        }
    }
}
