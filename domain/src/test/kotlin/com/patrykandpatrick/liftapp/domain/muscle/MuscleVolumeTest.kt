package com.patrykandpatrick.liftapp.domain.muscle

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test

class MuscleVolumeTest {

    @Test
    fun `Each tier is credited at its own fraction`() {
        val volume =
            MuscleVolume.of(
                sets = 4,
                primaryMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.FrontDelts),
            )

        assertEquals(4.0, volume[Muscle.Chest])
        assertEquals(2.0, volume[Muscle.Triceps])
        assertEquals(1.0, volume[Muscle.FrontDelts])
    }

    @Test
    fun `A muscle the exercise does not name receives nothing`() {
        val volume =
            MuscleVolume.of(
                sets = 4,
                primaryMuscles = listOf(Muscle.Chest),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            )

        assertEquals(0.0, volume[Muscle.Lats])
        assertEquals(listOf(Muscle.Chest), volume.workedMuscles)
    }

    @Test
    fun `A muscle named twice is credited at its highest tier only`() {
        val volume =
            MuscleVolume.of(
                sets = 2,
                primaryMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Lats, Muscle.Biceps),
                tertiaryMuscles = listOf(Muscle.Lats),
            )

        assertEquals(2.0, volume[Muscle.Lats])
        assertEquals(1.0, volume[Muscle.Biceps])
    }

    @Test
    fun `No sets is no volume`() {
        val volume =
            MuscleVolume.of(
                sets = 0,
                primaryMuscles = listOf(Muscle.Chest),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            )

        assertEquals(MuscleVolume.Empty, volume)
        assertTrue(volume.workedMuscles.isEmpty())
    }

    @Test
    fun `Exercises sharing a muscle add up on it`() {
        // Three sets of bench and three of overhead press: the front delts take the press
        // directly and the bench indirectly, which is the whole reason the split exists.
        val bench =
            MuscleVolume.of(
                sets = 3,
                primaryMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.FrontDelts, Muscle.Triceps),
                tertiaryMuscles = emptyList(),
            )
        val overheadPress =
            MuscleVolume.of(
                sets = 3,
                primaryMuscles = listOf(Muscle.FrontDelts),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = listOf(Muscle.SideDelts),
            )

        val volume = bench + overheadPress

        assertEquals(3.0, volume[Muscle.Chest])
        assertEquals(4.5, volume[Muscle.FrontDelts])
        assertEquals(3.0, volume[Muscle.Triceps])
        assertEquals(0.75, volume[Muscle.SideDelts])
    }

    @Test
    fun `Empty is the identity of addition`() {
        val volume =
            MuscleVolume.of(
                sets = 1,
                primaryMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            )

        assertEquals(volume, volume + MuscleVolume.Empty)
        assertEquals(volume, MuscleVolume.Empty + volume)
        assertEquals(MuscleVolume.Empty, MuscleVolume.Empty + MuscleVolume.Empty)
    }

    @Test
    fun `Worked muscles run from most to least worked`() {
        val volume =
            MuscleVolume.of(
                sets = 4,
                primaryMuscles = listOf(Muscle.Quadriceps),
                secondaryMuscles = listOf(Muscle.Glutes),
                tertiaryMuscles = listOf(Muscle.Calves),
            )

        assertEquals(listOf(Muscle.Quadriceps, Muscle.Glutes, Muscle.Calves), volume.workedMuscles)
    }

    @Test
    fun `Muscles worked equally are ordered the same way every read`() {
        val volume =
            MuscleVolume.of(
                sets = 2,
                primaryMuscles = listOf(Muscle.Triceps, Muscle.Chest, Muscle.FrontDelts),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            )

        assertEquals(
            listOf(Muscle.Chest, Muscle.FrontDelts, Muscle.Triceps),
            volume.workedMuscles,
        )
    }

    @Test
    fun `A week-long window is already a weekly rate`() {
        val volume =
            MuscleVolume.of(
                sets = 9,
                primaryMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = emptyList(),
            )

        assertEquals(volume, volume.perWeek(days = 7))
    }

    @Test
    fun `A longer window divides down to the weeks it holds`() {
        val volume =
            MuscleVolume.of(
                sets = 12,
                primaryMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = emptyList(),
            )

        // Four weeks to the day, so a fortnight's worth of chest work reads as three sets a week.
        val perWeek = volume.perWeek(days = 28)

        assertEquals(3.0, perWeek[Muscle.Chest])
        assertEquals(1.5, perWeek[Muscle.Triceps])
    }

    @Test
    fun `A window shorter than a week is reported as it stands`() {
        val volume =
            MuscleVolume.of(
                sets = 3,
                primaryMuscles = listOf(Muscle.Lats),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = emptyList(),
            )

        // Three sets across three days is three sets, not the twenty-one a week at that rate
        // would be: the count happened and the rate is a guess.
        assertEquals(3.0, volume.perWeek(days = 3)[Muscle.Lats])
        assertEquals(volume, volume.perWeek(days = 1))
    }

    @Test
    fun `Nothing worked stays nothing at any window length`() {
        assertEquals(MuscleVolume.Empty, MuscleVolume.Empty.perWeek(days = 30))
    }

    @Test
    fun `A window has to span at least a day`() {
        assertFailsWith<IllegalArgumentException> { MuscleVolume.Empty.perWeek(days = 0) }
    }

    @Test
    fun `A container is credited the same as its three lists`() {
        val container =
            object : MuscleContainer {
                override val primaryMuscles = listOf(Muscle.Lats)
                override val secondaryMuscles = listOf(Muscle.Biceps)
                override val tertiaryMuscles = listOf(Muscle.RearDelts)
            }

        assertEquals(
            MuscleVolume.of(
                sets = 3,
                primaryMuscles = container.primaryMuscles,
                secondaryMuscles = container.secondaryMuscles,
                tertiaryMuscles = container.tertiaryMuscles,
            ),
            MuscleVolume.of(sets = 3, muscles = container),
        )
    }

    @Test
    fun `Muscles that got nothing are named, because a gap is the finding`() {
        val volume =
            MuscleVolume.of(
                sets = 3,
                primaryMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = emptyList(),
            )

        val untrained = volume.untrainedMuscles

        assertFalse(Muscle.Chest in untrained)
        assertFalse(Muscle.Triceps in untrained)
        assertTrue(Muscle.Calves in untrained)
    }

    @Test
    fun `Worked and untrained together account for every muscle, once each`() {
        val volume =
            MuscleVolume.of(
                sets = 3,
                primaryMuscles = listOf(Muscle.Lats),
                secondaryMuscles = listOf(Muscle.Biceps),
                tertiaryMuscles = emptyList(),
            )

        val all = volume.workedMuscles + volume.untrainedMuscles

        assertEquals(Muscle.entries.size, all.size)
        assertEquals(Muscle.entries.toSet(), all.toSet())
    }

    @Test
    fun `Nothing logged leaves every muscle untrained`() {
        assertEquals(Muscle.entries, MuscleVolume.Empty.untrainedMuscles)
    }

    @Test
    fun `Work too slight to round up to a set still counts as trained`() {
        val volume =
            MuscleVolume.of(
                sets = 1,
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = listOf(Muscle.Forearms),
            )

        assertEquals(0, volume.wholeSetsOf(Muscle.Forearms))
        assertFalse(Muscle.Forearms in volume.untrainedMuscles)
    }
}
