package com.patrykandpatrick.liftapp.domain.workout

import com.patrykandpatrick.liftapp.domain.target.SetTarget
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import org.junit.Test

class WorkoutGoalTest {

    @Test
    fun `The set count is the length of the list, never a field that could disagree`() {
        assertEquals(3, goal(sets = 3).sets)
        assertEquals(0, goal(sets = 0).sets)
    }

    @Test
    fun `The rep range spans every set, so no set falls outside what is shown`() {
        val ramped = goal(SetTarget(minReps = 5, maxReps = 5), SetTarget(minReps = 8, maxReps = 12))

        assertEquals(5, ramped.minReps)
        assertEquals(12, ramped.maxReps)
    }

    @Test
    fun `A goal with no sets reads as zero rather than throwing`() {
        val empty = goal(sets = 0)

        assertEquals(0, empty.minReps)
        assertEquals(0, empty.maxReps)
        assertEquals(Duration.ZERO, empty.duration)
        assertEquals(0.0, empty.distance)
        assertEquals(0.0, empty.calories)
        assertNull(empty.targetFor(0))
    }

    @Test
    fun `Growing repeats the last set prescribed rather than inventing a blank one`() {
        val grown =
            goal(SetTarget(minReps = 5, maxReps = 5), SetTarget(minReps = 8, maxReps = 12))
                .withSetCount(4)

        assertEquals(4, grown.sets)
        assertEquals(5..5, grown.setTargets[0].let { it.minReps..it.maxReps })
        assertEquals(8..12, grown.setTargets[1].let { it.minReps..it.maxReps })
        assertEquals(8..12, grown.setTargets[2].let { it.minReps..it.maxReps })
        assertEquals(8..12, grown.setTargets[3].let { it.minReps..it.maxReps })
    }

    @Test
    fun `Shrinking drops from the end, so the sets already prescribed keep their place`() {
        val shrunk =
            goal(
                    SetTarget(minReps = 5, maxReps = 5),
                    SetTarget(minReps = 8, maxReps = 12),
                    SetTarget(minReps = 15, maxReps = 20),
                )
                .withSetCount(2)

        assertEquals(2, shrunk.sets)
        assertEquals(5..5, shrunk.setTargets[0].let { it.minReps..it.maxReps })
        assertEquals(8..12, shrunk.setTargets[1].let { it.minReps..it.maxReps })
    }

    @Test
    fun `Resizing to the count it already has changes nothing`() {
        val original = goal(sets = 3)

        assertSame(original, original.withSetCount(3))
    }

    @Test
    fun `A goal with nothing prescribed cannot be grown, because there is nothing to repeat`() {
        val empty = goal(sets = 0)

        assertSame(empty, empty.withSetCount(3))
    }

    @Test
    fun `Duration, distance and calories come from the first set, which is what a run has one of`() {
        val run =
            goal(
                SetTarget(
                    minReps = 0,
                    maxReps = 0,
                    duration = 5.minutes,
                    distance = 5.0,
                    calories = 300.0,
                )
            )

        assertEquals(5.minutes, run.duration)
        assertEquals(5.0, run.distance)
        assertEquals(300.0, run.calories)
    }

    @Test
    fun `A target past the end is absent, because a set added mid-session was never prescribed`() {
        val two = goal(sets = 2)

        assertNull(two.targetFor(2))
        assertEquals(8, two.targetFor(1)?.minReps)
    }

    private fun goal(vararg sets: SetTarget) =
        Workout.Goal(id = 0, setTargets = sets.toList(), restTime = 2.minutes)

    private fun goal(sets: Int) =
        Workout.Goal(
            id = 0,
            setTargets = List(sets) { SetTarget(minReps = 8, maxReps = 12, rir = 2) },
            restTime = 2.minutes,
        )
}
