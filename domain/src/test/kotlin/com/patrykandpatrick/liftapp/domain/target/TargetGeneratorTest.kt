package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemWithExercises
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import org.junit.Test

class TargetGeneratorTest {

    private val generator = TargetGenerator()

    @Test
    fun `A target is written for every cycle, day and exercise`() {
        val targets = generator(twoDayPlan(), parameters(cycleCount = 3))

        // Four exercises on the training day, none on the rest day, times three cycles.
        assertEquals(12, targets.size)
        assertEquals(setOf(0, 1, 2), targets.map { it.cycleIndex }.toSet())
        assertEquals(setOf(0), targets.map { it.planItemOrderIndex }.toSet())
    }

    @Test
    fun `Rest days get nothing, because there is nothing to prescribe`() {
        val targets = generator(twoDayPlan(), parameters(cycleCount = 1))

        assertTrue(targets.none { it.planItemOrderIndex == REST_DAY_INDEX })
    }

    @Test
    fun `Intent picks the rep range, so a strength block is not prescribed a growth block's reps`() {
        val strength = generator(twoDayPlan(), parameters(intent = TrainingIntent.Strength))
        val hypertrophy = generator(twoDayPlan(), parameters(intent = TrainingIntent.Hypertrophy))

        assertEquals(3..5, strength.forExercise(BENCH_ID).sets.first().repRange)
        assertEquals(6..10, hypertrophy.forExercise(BENCH_ID).sets.first().repRange)
    }

    @Test
    fun `Role picks the rep range within one intent`() {
        val targets = generator(twoDayPlan(), parameters(intent = TrainingIntent.Hypertrophy))

        assertEquals(6..10, targets.forExercise(BENCH_ID).sets.first().repRange)
        assertEquals(10..15, targets.forExercise(CURL_ID).sets.first().repRange)
    }

    @Test
    fun `RIR ramps down across the cycles rather than bottoming out at once`() {
        val targets = generator(twoDayPlan(), parameters(cycleCount = 4, startingRir = 3))

        val rirByCycle = (0..3).map { targets.forExercise(BENCH_ID, cycle = it).sets.first().rir }

        assertEquals(listOf(3, 2, 2, 1), rirByCycle)
    }

    @Test
    fun `The ramp finishes at the floor of the role's band, never below it`() {
        val targets = generator(twoDayPlan(), parameters(cycleCount = 5, startingRir = 4))

        val last = targets.forExercise(BENCH_ID, cycle = 4).sets.first()

        assertEquals(1, last.rir, "A primary compound's band bottoms out at 1 RIR.")
    }

    @Test
    fun `A start already at the floor stays there instead of being ramped up to fall from`() {
        val targets = generator(twoDayPlan(), parameters(cycleCount = 4, startingRir = 0))

        val rirByCycle = (0..3).map { targets.forExercise(BENCH_ID, cycle = it).sets.first().rir }

        assertEquals(listOf(0, 0, 0, 0), rirByCycle)
    }

    @Test
    fun `Set count climbs toward roughly double by the last cycle`() {
        val targets = generator(twoDayPlan(), parameters(cycleCount = 4, baseSetCount = 3))

        val setsByCycle = (0..3).map { targets.forExercise(BENCH_ID, cycle = it).setCount }

        assertEquals(listOf(3, 4, 5, 6), setsByCycle)
    }

    @Test
    fun `A single-cycle plan ramps nothing, because there is nowhere to ramp to`() {
        val targets = generator(twoDayPlan(), parameters(cycleCount = 1, startingRir = 3))

        val only = targets.forExercise(BENCH_ID)

        assertEquals(3, only.setCount)
        assertEquals(2, only.sets.first().rir, "Clamped to the band the role allows.")
    }

    @Test
    fun `Rest time comes from the role, not from the intent`() {
        val targets = generator(twoDayPlan(), parameters(intent = TrainingIntent.Hypertrophy))

        assertEquals(2.minutes, targets.forExercise(BENCH_ID).restTime)
        assertEquals(90L, targets.forExercise(CURL_ID).restTime.inWholeSeconds)
    }

    @Test
    fun `Cardio is never given reps in reserve, which would mean nothing for a run`() {
        val targets = generator(twoDayPlan(), parameters())

        targets.forExercise(RUN_ID).sets.forEach { assertNull(it.rir) }
    }

    @Test
    fun `An unclassified exercise still gets a target rather than being skipped`() {
        val targets = generator(twoDayPlan(), parameters(intent = TrainingIntent.Strength))

        assertEquals(8..12, targets.forExercise(UNCLASSIFIED_ID).sets.first().repRange)
    }

    @Test
    fun `Load is left unprescribed, because nothing here knows the lifter's numbers`() {
        val targets = generator(twoDayPlan(), parameters())

        assertTrue(targets.all { target -> target.sets.all { it.loadPercent == null } })
    }

    @Test
    fun `An exercise listed twice in a routine is prescribed once`() {
        val plan = twoDayPlan(exercises = listOf(bench(), bench()))

        val targets = generator(plan, parameters(cycleCount = 1))

        assertEquals(1, targets.size)
    }

    private val SetTarget.repRange: IntRange
        get() = minReps..maxReps

    private fun List<ExerciseTarget>.forExercise(id: Long, cycle: Int = 0): ExerciseTarget =
        single {
            it.exerciseID == id && it.cycleIndex == cycle
        }

    private fun parameters(
        intent: TrainingIntent = TrainingIntent.Both,
        cycleCount: Int = 1,
        startingRir: Int = GenerationParameters.DEFAULT_STARTING_RIR,
        baseSetCount: Int = TargetDefaults.SET_COUNT,
    ) = GenerationParameters(intent, cycleCount, startingRir, baseSetCount)

    private fun bench() =
        RoutineExerciseItem(
            BENCH_ID,
            "Bench Press",
            "Chest",
            ExerciseType.Weight,
            ExerciseRole.PrimaryCompound,
        )

    private fun twoDayPlan(
        exercises: List<RoutineExerciseItem> = listOf(bench(), curl(), run(), unclassified())
    ): Plan =
        Plan(
            id = PLAN_ID,
            name = "Upper/Lower",
            description = "",
            items =
                listOf(
                    Plan.Item.Routine(
                        RoutineWithExercises(
                            id = 1L,
                            name = "Push",
                            items =
                                exercises.mapIndexed { index, exercise ->
                                    RoutineItemWithExercises(
                                        id = index.toLong(),
                                        type = RoutineItemType.Exercise,
                                        exercises = listOf(exercise),
                                    )
                                },
                            primaryMuscles = emptyList(),
                            secondaryMuscles = emptyList(),
                            tertiaryMuscles = emptyList(),
                        )
                    ),
                    Plan.Item.Rest,
                ),
        )

    private fun curl() =
        RoutineExerciseItem(CURL_ID, "Curl", "Biceps", ExerciseType.Weight, ExerciseRole.Isolation)

    private fun run() =
        RoutineExerciseItem(RUN_ID, "Long run", "Legs", ExerciseType.Cardio, role = null)

    private fun unclassified() =
        RoutineExerciseItem(UNCLASSIFIED_ID, "My lift", "Chest", ExerciseType.Weight, role = null)

    private companion object {
        const val PLAN_ID = 1L
        const val BENCH_ID = 6L
        const val CURL_ID = 4L
        const val RUN_ID = 12L
        const val UNCLASSIFIED_ID = 99L
        const val REST_DAY_INDEX = 1
    }
}
