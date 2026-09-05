package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SuggestLoadUseCaseTest {

    private val useCase = SuggestLoadUseCase()

    @Test
    fun `Filling the rep range at target effort adds load`() {
        val suggestion =
            suggest(
                session(100.0, reps = 12, rir = 2),
                session(100.0, reps = 11),
                target = NARROW_TARGET,
            )

        assertIs<SuggestionReason.LoadUp>(suggestion?.reason)
        assertTrue(suggestion!!.load > 100.0, "Hitting the top of the range should add load.")
    }

    @Test
    fun `Reps still to find inside the range holds the load and says why`() {
        val suggestion = suggest(session(100.0, reps = 9, rir = 2), session(100.0, reps = 8))

        val reason = assertIs<SuggestionReason.Hold>(suggestion?.reason)
        assertEquals(9, reason.achievedReps)
        assertEquals(12, reason.maxReps)
        assertEquals(100.0, suggestion!!.load)
    }

    @Test
    fun `Missing the bottom of the range backs the load off`() {
        val suggestion = suggest(session(100.0, reps = 6, rir = 2), session(100.0, reps = 9))

        val reason = assertIs<SuggestionReason.BackOff>(suggestion?.reason)
        assertEquals(6, reason.achievedReps)
        // Two reps short at three percent each is 94 kg, snapped up to what a barbell can hold.
        assertEquals(95.0, suggestion!!.load, ABSOLUTE_TOLERANCE)
    }

    @Test
    fun `A set harder than asked for backs off even inside the range`() {
        val suggestion = suggest(session(100.0, reps = 10, rir = 4), session(100.0, reps = 9))

        assertIs<SuggestionReason.BackOff>(suggestion?.reason)
    }

    @Test
    fun `No single step may exceed a tenth, however far the arithmetic reaches`() {
        // Five reps short is fifteen percent by the back-off rule alone.
        val suggestion = suggest(session(100.0, reps = 3, rir = 2), session(100.0, reps = 9))

        val reason = assertIs<SuggestionReason.Capped>(suggestion?.reason)
        assertIs<SuggestionReason.BackOff>(reason.within)
        assertEquals(90.0, suggestion!!.load, ABSOLUTE_TOLERANCE)
        assertTrue(reason.uncapped < 90.0, "The capped reason should carry what was asked for.")
    }

    @Test
    fun `A load-up across a wide rep range is held to a tenth`() {
        // Dropping from twelve reps to eight at the same effort is worth more than a tenth of the
        // bar on its own, so an ordinary double progression over 8-12 meets the guard every time.
        val suggestion = suggest(session(100.0, reps = 12, rir = 2), session(100.0, reps = 11))

        val reason = assertIs<SuggestionReason.Capped>(suggestion?.reason)
        assertIs<SuggestionReason.LoadUp>(reason.within)
        assertEquals(110.0, suggestion!!.load, ABSOLUTE_TOLERANCE)
        assertTrue(reason.uncapped > 110.0, "The capped reason should carry what was asked for.")
    }

    @Test
    fun `One session is not enough to act on`() {
        assertNull(suggest(session(100.0, reps = 12, rir = 2)))
    }

    @Test
    fun `No sessions at all suggest nothing`() {
        assertNull(suggest())
    }

    @Test
    fun `A reps-only exercise is never given a load`() {
        val suggestion =
            useCase(
                history = listOf(session(100.0, reps = 12, rir = 2), session(100.0, reps = 10)),
                target = TARGET,
                policy = ProgressionPolicy.RepsOnly,
                loadIncrement = 2.5,
                massUnit = MassUnit.Kilograms,
            )

        assertNull(suggestion)
    }

    @Test
    fun `Every suggestion is snapped to what the equipment can actually load`() {
        val suggestion = suggest(session(100.0, reps = 12, rir = 2), session(100.0, reps = 10))

        val remainder = suggestion!!.load % 2.5
        assertTrue(remainder < ABSOLUTE_TOLERANCE, "${suggestion.load} is not a multiple of 2.5.")
    }

    @Test
    fun `Two sessions standing still at target effort is a stall, questioned before anything else`() {
        val suggestion =
            suggest(session(100.0, reps = 9, rir = 2), session(100.0, reps = 9, rir = 2))

        val reason = assertIs<SuggestionReason.Stalled>(suggestion?.reason)
        assertEquals(StallEscalation.CheckEffort, reason.step)
        assertEquals(100.0, suggestion!!.load, "Questioning the effort changes no load.")
    }

    @Test
    fun `A stall that persists deloads before it suggests anything more drastic`() {
        val suggestion =
            suggest(
                session(100.0, reps = 9, rir = 2),
                session(100.0, reps = 9, rir = 2),
                session(100.0, reps = 9, rir = 2),
            )

        val reason = assertIs<SuggestionReason.Stalled>(suggestion?.reason)
        assertEquals(StallEscalation.DeloadExercise, reason.step)
        assertEquals(90.0, suggestion!!.load, ABSOLUTE_TOLERANCE)
    }

    @Test
    fun `Changing the exercise is the last thing suggested, never the first`() {
        val stalled = List(6) { session(100.0, reps = 9, rir = 2) }

        val steps =
            (2..6).map { count ->
                val reason = suggest(*stalled.take(count).toTypedArray())?.reason
                assertIs<SuggestionReason.Stalled>(reason).step
            }

        assertEquals(
            listOf(
                StallEscalation.CheckEffort,
                StallEscalation.DeloadExercise,
                StallEscalation.ReduceVolumeElsewhere,
                StallEscalation.ChangeExercise,
                StallEscalation.ChangeExercise,
            ),
            steps,
        )
    }

    @Test
    fun `Progress on load alone breaks a stall`() {
        val suggestion =
            suggest(session(105.0, reps = 9, rir = 2), session(100.0, reps = 9, rir = 2))

        assertIs<SuggestionReason.Hold>(suggestion?.reason)
    }

    @Test
    fun `The load waits when the plan already added sets this cycle`() {
        val suggestion =
            useCase(
                history = listOf(session(100.0, reps = 12, rir = 2), session(100.0, reps = 10)),
                target = TARGET,
                policy = ProgressionPolicy.DoubleProgression,
                loadIncrement = 2.5,
                massUnit = MassUnit.Kilograms,
                volumeProgressedThisCycle = true,
            )

        assertEquals(SuggestionReason.VolumeProgressedInstead, suggestion?.reason)
        assertEquals(100.0, suggestion?.load)
    }

    @Test
    fun `A bodyweight lift is told what to hang off the bar, not what it moves in total`() {
        val suggestion =
            suggest(
                calisthenics(added = 10.0, bodyWeight = 80.0, reps = 12, rir = 2),
                calisthenics(added = 10.0, bodyWeight = 80.0, reps = 11, rir = 2),
                target = NARROW_TARGET,
            )

        assertIs<SuggestionReason.LoadUp>(suggestion?.reason)
        assertTrue(
            suggestion!!.load in 10.0..19.0,
            "Expected added weight near 10 kg, got ${suggestion.load}.",
        )
    }

    @Test
    fun `Every suggestion carries a reason, whatever the history`() {
        val histories =
            listOf(
                listOf(session(100.0, reps = 12, rir = 2), session(100.0, reps = 10)),
                listOf(session(100.0, reps = 9, rir = 2), session(100.0, reps = 8)),
                listOf(session(100.0, reps = 4, rir = 2), session(100.0, reps = 9)),
                listOf(session(100.0, reps = 9, rir = 2), session(100.0, reps = 9, rir = 2)),
            )

        histories.forEach { history ->
            val suggestion = suggest(*history.toTypedArray())
            assertTrue(suggestion != null, "No suggestion for $history.")
        }
    }

    private fun suggest(
        vararg history: ExerciseSession,
        target: SetTarget = TARGET,
    ): LoadSuggestion? =
        useCase(
            history = history.toList(),
            target = target,
            policy = ProgressionPolicy.DoubleProgression,
            loadIncrement = 2.5,
            massUnit = MassUnit.Kilograms,
        )

    private fun session(load: Double, reps: Int, rir: Int? = 2) =
        ExerciseSession(
            listOf(
                ExerciseSet.Weight(
                    weight = load,
                    reps = reps,
                    weightUnit = MassUnit.Kilograms,
                    rir = rir,
                )
            )
        )

    private fun calisthenics(added: Double, bodyWeight: Double, reps: Int, rir: Int?) =
        ExerciseSession(
            listOf(
                ExerciseSet.Calisthenics(
                    weight = added,
                    bodyWeight = bodyWeight,
                    reps = reps,
                    weightUnit = MassUnit.Kilograms,
                    rir = rir,
                )
            )
        )

    private fun assertEquals(expected: Double, actual: Double, tolerance: Double) {
        assertTrue(
            abs(expected - actual) <= tolerance,
            "Expected $expected but was $actual.",
        )
    }

    private companion object {
        val TARGET = SetTarget(minReps = 8, maxReps = 12, rir = 2)

        /** Narrow enough that an honest double progression clears the ten-percent guard. */
        val NARROW_TARGET = SetTarget(minReps = 10, maxReps = 12, rir = 2)
        const val ABSOLUTE_TOLERANCE = 0.01
    }
}
