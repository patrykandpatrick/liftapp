package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
    fun `A set stopped short of the target effort counts the reps it had left`() {
        // Ten reps with four in reserve is twelve at the two asked for: the top of the range.
        val suggestion =
            suggest(
                session(100.0, reps = 10, rir = 4),
                session(100.0, reps = 9),
                target = NARROW_TARGET,
            )

        val reason = assertIs<SuggestionReason.LoadUp>(suggestion?.reason)
        assertTrue(reason.easierThanAsked, "The reps alone would not have earned the load-up.")
    }

    @Test
    fun `A set taken past the target effort backs off even inside the range`() {
        // Nine reps at failure is seven at the two in reserve asked for, one short of the range.
        val suggestion = suggest(session(100.0, reps = 9, rir = 0), session(100.0, reps = 8))

        val reason = assertIs<SuggestionReason.BackOff>(suggestion?.reason)
        assertTrue(reason.harderThanAsked, "The reps alone would have held.")
        assertEquals(97.5, suggestion!!.load, ABSOLUTE_TOLERANCE)
    }

    @Test
    fun `Topping the range past the target effort holds rather than adds`() {
        val suggestion = suggest(session(100.0, reps = 12, rir = 0), session(100.0, reps = 10))

        val reason = assertIs<SuggestionReason.Hold>(suggestion?.reason)
        assertTrue(reason.harderThanAsked, "The reps alone would have added load.")
        assertEquals(100.0, suggestion!!.load)
    }

    @Test
    fun `Reps that reach the range without help from the effort say so`() {
        val suggestion = suggest(session(100.0, reps = 9, rir = 2), session(100.0, reps = 8))

        val reason = assertIs<SuggestionReason.Hold>(suggestion?.reason)
        assertFalse(reason.harderThanAsked)
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
        // Dropping from twelve reps to eight at the same effort is worth a little more than a tenth
        // of the bar on its own, so an ordinary double progression over 8-12 meets the guard.
        val suggestion = suggest(session(100.0, reps = 12, rir = 2), session(100.0, reps = 11))

        assertEquals(110.0, suggestion!!.load, ABSOLUTE_TOLERANCE)
    }

    @Test
    fun `A cap the equipment's step rounds away is not reported`() {
        // The arithmetic asks for about 110.2 kg, which snaps to the 110 kg the cap allows anyway.
        val suggestion = suggest(session(100.0, reps = 12, rir = 2), session(100.0, reps = 11))

        assertIs<SuggestionReason.LoadUp>(suggestion?.reason)
    }

    @Test
    fun `A load-up off one unusually good set is held to a tenth and says what it wanted`() {
        val suggestion =
            suggest(
                session(100.0, reps = 12, rir = 2),
                session(100.0, reps = 11),
                session(120.0, reps = 10, rir = 2),
            )

        val reason = assertIs<SuggestionReason.Capped>(suggestion?.reason)
        assertIs<SuggestionReason.LoadUp>(reason.within)
        assertEquals(110.0, suggestion!!.load, ABSOLUTE_TOLERANCE)
        assertTrue(reason.uncapped > 110.0, "The capped reason should carry what was asked for.")
        assertTrue(reason.uncapped % 2.5 < ABSOLUTE_TOLERANCE, "${reason.uncapped} is unsnapped.")
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
    fun `A bodyweight lift is told what to hang off the bar, not what it moves in total`() {
        val suggestion =
            suggest(
                calisthenics(added = 10.0, bodyWeight = 80.0, reps = 12, rir = 2),
                calisthenics(added = 10.0, bodyWeight = 80.0, reps = 11, rir = 2),
                target = NARROW_TARGET,
            )

        val reason = assertIs<SuggestionReason.LoadUp>(suggestion?.reason)
        assertTrue(
            suggestion!!.load in 10.0..19.0,
            "Expected added weight near 10 kg, got ${suggestion.load}.",
        )
        assertEquals(10.0, reason.previousLoad, "The reason should speak in added weight too.")
    }

    @Test
    fun `A capped bodyweight suggestion names its uncapped figure in added weight`() {
        val suggestion =
            suggest(
                calisthenics(added = 10.0, bodyWeight = 80.0, reps = 12, rir = 2),
                calisthenics(added = 10.0, bodyWeight = 80.0, reps = 11, rir = 2),
                calisthenics(added = 30.0, bodyWeight = 80.0, reps = 10, rir = 2),
            )

        val reason = assertIs<SuggestionReason.Capped>(suggestion?.reason)
        assertTrue(reason.uncapped in 20.0..60.0, "Expected added weight, got ${reason.uncapped}.")
        assertTrue(reason.uncapped % 2.5 < ABSOLUTE_TOLERANCE, "${reason.uncapped} is unsnapped.")
        assertEquals(10.0, assertIs<SuggestionReason.LoadUp>(reason.within).previousLoad)
    }

    @Test
    fun `A bodyweight lift with nothing hung off it has nothing to back off`() {
        val suggestion =
            suggest(
                calisthenics(added = 0.0, bodyWeight = 80.0, reps = 5, rir = 2),
                calisthenics(added = 0.0, bodyWeight = 80.0, reps = 9, rir = 2),
            )

        assertNull(suggestion)
    }

    @Test
    fun `A pound user's load is snapped to round pounds, not converted kilograms`() {
        // Two reps short of 225 lb is 211.5 lb. A 2.5 kg step is 5 lb in a pound gym, not 5.5 lb
        // and not the 2.5 lb the raw kilogram figure would read as.
        val suggestion =
            suggest(
                session(225.0, reps = 6, rir = 2),
                session(225.0, reps = 9),
                massUnit = MassUnit.Pounds,
            )

        assertEquals(210.0, suggestion!!.load, ABSOLUTE_TOLERANCE)
    }

    @Test
    fun `A deload week takes a tenth off the last normal week`() {
        val suggestion =
            suggest(session(100.0, reps = 12), session(100.0, reps = 11), isDeloadWeek = true)

        val reason = assertIs<SuggestionReason.Deload>(suggestion?.reason)
        assertEquals(100.0, reason.previousLoad)
        assertEquals(90.0, suggestion!!.load, ABSOLUTE_TOLERANCE)
    }

    @Test
    fun `A deload week needs only one session to come down from`() {
        val suggestion = suggest(session(100.0, reps = 10), isDeloadWeek = true)

        assertIs<SuggestionReason.Deload>(suggestion?.reason)
    }

    @Test
    fun `The week after a deload progresses from the week before it`() {
        val history = arrayOf(session(100.0, reps = 12, rir = 2), session(100.0, reps = 11))
        val withDeload = arrayOf(session(90.0, reps = 8, rir = 4, isDeload = true), *history)

        assertEquals(
            suggest(*history, target = NARROW_TARGET),
            suggest(*withDeload, target = NARROW_TARGET),
        )
    }

    @Test
    fun `A deload week comes down from the last normal week, not an earlier deload`() {
        val suggestion =
            suggest(
                session(90.0, reps = 8, rir = 4, isDeload = true),
                session(100.0, reps = 10),
                isDeloadWeek = true,
            )

        assertEquals(100.0, assertIs<SuggestionReason.Deload>(suggestion?.reason).previousLoad)
    }

    @Test
    fun `One session is one short of a suggestion, and a deload session does not count`() {
        val policy = ProgressionPolicy.DoubleProgression

        assertTrue(useCase.isOneSessionShort(listOf(session(100.0, reps = 10)), policy))
        assertFalse(
            useCase.isOneSessionShort(
                listOf(session(100.0, reps = 10), session(100.0, reps = 9)),
                policy,
            )
        )
        assertFalse(
            useCase.isOneSessionShort(
                listOf(session(100.0, reps = 10, isDeload = true)),
                policy,
            )
        )
        assertFalse(
            useCase.isOneSessionShort(
                listOf(session(100.0, reps = 10)),
                ProgressionPolicy.RepsOnly,
            )
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

    @Test
    fun `A percentage is taken of the best one-rep max the recent sessions show`() {
        val history = listOf(session(100.0, reps = 5, rir = 2), session(90.0, reps = 5, rir = 2))
        val oneRepMax =
            OneRepMaxCalculator.estimatedOneRepMax(100.0, reps = 5, rir = 2, MassUnit.Kilograms)

        val suggestion =
            useCase.forPercent(history, percent = 80.0, loadIncrement = 2.5, MassUnit.Kilograms)

        val reason = assertIs<SuggestionReason.PercentOfOneRepMax>(suggestion?.reason)
        assertEquals(oneRepMax, reason.oneRepMax, ABSOLUTE_TOLERANCE)
        assertEquals(
            OneRepMaxCalculator.roundToIncrement(oneRepMax * 0.8, 2.5),
            suggestion!!.load,
            ABSOLUTE_TOLERANCE,
        )
    }

    @Test
    fun `A percentage needs only one session to estimate from`() {
        val suggestion =
            useCase.forPercent(
                listOf(session(100.0, reps = 5)),
                percent = 75.0,
                loadIncrement = 2.5,
                MassUnit.Kilograms,
            )

        assertIs<SuggestionReason.PercentOfOneRepMax>(suggestion?.reason)
    }

    @Test
    fun `A bodyweight lift is given no percentage, which would be of its whole mass`() {
        val suggestion =
            useCase.forPercent(
                listOf(calisthenics(added = 10.0, bodyWeight = 80.0, reps = 8, rir = 2)),
                percent = 75.0,
                loadIncrement = 2.5,
                MassUnit.Kilograms,
            )

        assertNull(suggestion)
    }

    @Test
    fun `A percentage is one session short only when there is none`() {
        val policy = ProgressionPolicy.RepsOnly

        assertTrue(useCase.isOneSessionShort(emptyList(), policy, isPercent = true))
        assertFalse(
            useCase.isOneSessionShort(listOf(session(100.0, reps = 5)), policy, isPercent = true)
        )
    }

    private fun suggest(
        vararg history: ExerciseSession,
        target: SetTarget = TARGET,
        massUnit: MassUnit = MassUnit.Kilograms,
        isDeloadWeek: Boolean = false,
    ): LoadSuggestion? =
        useCase(
            history = history.toList(),
            target = target,
            policy = ProgressionPolicy.DoubleProgression,
            loadIncrement = 2.5,
            massUnit = massUnit,
            isDeloadWeek = isDeloadWeek,
        )

    private fun session(load: Double, reps: Int, rir: Int? = 2, isDeload: Boolean = false) =
        ExerciseSession(
            sets =
                listOf(
                    ExerciseSet.Weight(
                        weight = load,
                        reps = reps,
                        weightUnit = MassUnit.Kilograms,
                        rir = rir,
                    )
                ),
            isDeload = isDeload,
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
