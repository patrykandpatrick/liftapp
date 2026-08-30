package com.patrykandpatrick.liftapp.domain.muscle

import kotlin.test.assertEquals
import org.junit.Test

class VolumeBandTest {

    private val maintenance = VolumeBand.MAINTENANCE_VOLUME

    private val adaptive = VolumeBand.ADAPTIVE_VOLUME

    private val recoverable = VolumeBand.RECOVERABLE_VOLUME

    @Test
    fun `Each landmark opens its own band`() {
        assertEquals(VolumeBand.BelowMaintenance, VolumeBand.of(0.0))
        assertEquals(VolumeBand.Maintenance, VolumeBand.of(maintenance))
        assertEquals(VolumeBand.Adaptive, VolumeBand.of(adaptive))
        assertEquals(VolumeBand.Maximum, VolumeBand.of(recoverable))
    }

    @Test
    fun `A landmark not quite reached stays in the band below`() {
        assertEquals(VolumeBand.BelowMaintenance, VolumeBand.of(maintenance - 0.25))
        assertEquals(VolumeBand.Maintenance, VolumeBand.of(adaptive - 0.25))
        assertEquals(VolumeBand.Adaptive, VolumeBand.of(recoverable - 0.25))
    }

    @Test
    fun `The bands run in order and cover the whole range`() {
        val bands = generateSequence(0.0) { it + 0.25 }.take(200).map(VolumeBand::of).toList()

        assertEquals(VolumeBand.entries, bands.distinct())
    }

    @Test
    fun `A band never contradicts the set count shown beside it`() {
        // Five and three quarter sets is reported as six, so it has to read as six sets' worth of
        // stimulus as well.
        val volume =
            MuscleVolume.of(
                sets = 23,
                primaryMuscles = emptyList(),
                secondaryMuscles = emptyList(),
                tertiaryMuscles = listOf(Muscle.RearDelts),
            )

        assertEquals(6, volume.wholeSetsOf(Muscle.RearDelts))
        assertEquals(VolumeBand.Maintenance, volume.bandOf(Muscle.RearDelts))
    }

    @Test
    fun `A muscle reads the band its own set count falls in`() {
        val volume =
            MuscleVolume.of(
                sets = 12,
                primaryMuscles = listOf(Muscle.Chest),
                secondaryMuscles = listOf(Muscle.Triceps),
                tertiaryMuscles = emptyList(),
            )

        // Twelve sets of bench: the chest is in the productive band, and the triceps get half
        // credit for each of them, which lands them exactly on maintenance.
        assertEquals(VolumeBand.Adaptive, volume.bandOf(Muscle.Chest))
        assertEquals(VolumeBand.Maintenance, volume.bandOf(Muscle.Triceps))
        assertEquals(VolumeBand.BelowMaintenance, volume.bandOf(Muscle.Lats))
    }
}
