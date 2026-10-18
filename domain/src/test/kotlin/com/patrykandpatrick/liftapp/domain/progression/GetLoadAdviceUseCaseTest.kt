package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.exerciseset.ExerciseSetGroup
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import com.patrykandpatrick.liftapp.domain.workout.Workout
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetLoadAdviceUseCaseTest {

    @Test
    fun `An exercise with history gets a suggestion`() = runTest {
        val advice = useCase(history = twoSessions()).invoke(workout())

        assertIs<LoadSuggestion>(advice[BENCH_ID]?.get(0))
    }

    @Test
    fun `One session of history says one more is needed`() = runTest {
        val advice = useCase(history = twoSessions().take(1)).invoke(workout())

        assertEquals(LoadAdvice.OneMoreSession, advice[BENCH_ID]?.get(0))
    }

    @Test
    fun `No history says nothing at all`() = runTest {
        val advice = useCase(history = emptyList()).invoke(workout())

        assertTrue(advice.isEmpty())
    }

    @Test
    fun `A finished workout gets no advice`() = runTest {
        val advice =
            useCase(history = twoSessions()).invoke(workout(endDate = WORKOUT_START.plusHours(1)))

        assertTrue(advice.isEmpty())
    }

    @Test
    fun `An exercise without a weight to set gets no advice`() = runTest {
        val advice =
            useCase(history = twoSessions()).invoke(workout(exerciseType = ExerciseType.Reps))

        assertTrue(advice.isEmpty())
    }

    @Test
    fun `The history asked for ends where this workout began`() = runTest {
        var askedBefore: LocalDateTime? = null
        val useCase =
            useCase(
                getRecentSessions = { _, before, _ ->
                    askedBefore = before
                    emptyList()
                }
            )

        useCase(workout())

        assertEquals(WORKOUT_START, askedBefore)
    }

    @Test
    fun `A workout in the plan's deload week is told to deload`() = runTest {
        // A two-cycle block of one-day cycles: the second day is the deload.
        val advice =
            useCase(history = twoSessions(), deloadEveryCycles = 2)
                .invoke(workout(start = PLAN_START.plusDays(1).atTime(18, 0)))

        val suggestion = assertIs<LoadSuggestion>(advice[BENCH_ID]?.get(0))
        assertIs<SuggestionReason.Deload>(suggestion.reason)
    }

    @Test
    fun `A past session in a deload week is read past`() = runTest {
        val deloadDay = PLAN_START.plusDays(1).atTime(18, 0)
        val history = listOf(group(load = 90.0, reps = 8, date = deloadDay)) + twoSessions()

        val advice =
            useCase(history = history, deloadEveryCycles = 2)
                .invoke(workout(start = PLAN_START.plusDays(2).atTime(18, 0)))

        val suggestion = assertIs<LoadSuggestion>(advice[BENCH_ID]?.get(0))
        assertTrue(suggestion.load >= 100.0, "Progressed from the deload's 90 kg instead.")
    }

    @Test
    fun `Only the first set gets the loop's advice, and the rest carry its weight`() = runTest {
        val advice = useCase(history = twoSessions()).invoke(workout())

        assertEquals(setOf(0), advice[BENCH_ID]?.keys)
    }

    @Test
    fun `A set given a percentage of one-rep max gets its own load`() = runTest {
        val goal =
            Workout.Goal.default.copy(
                setTargets =
                    listOf(
                        SetTarget(minReps = 5, maxReps = 5, rir = 2, loadPercent = 70.0),
                        SetTarget(minReps = 5, maxReps = 5, rir = 2, loadPercent = 80.0),
                    )
            )

        val advice = useCase(history = twoSessions()).invoke(workout(goal = goal))

        val first = assertIs<LoadSuggestion>(advice[BENCH_ID]?.get(0))
        val second = assertIs<LoadSuggestion>(advice[BENCH_ID]?.get(1))
        assertIs<SuggestionReason.PercentOfOneRepMax>(first.reason)
        assertTrue(second.load > first.load, "80% should ask for more than 70%.")
    }

    @Test
    fun `A percentage with no history to estimate from says one more session is needed`() =
        runTest {
            val goal =
                Workout.Goal.default.copy(
                    setTargets = listOf(SetTarget(minReps = 5, maxReps = 5, loadPercent = 75.0))
                )

            val advice = useCase(history = emptyList()).invoke(workout(goal = goal))

            assertEquals(LoadAdvice.OneMoreSession, advice[BENCH_ID]?.get(0))
        }

    private suspend fun useCase(
        history: List<ExerciseSetGroup> = emptyList(),
        deloadEveryCycles: Int? = null,
        getRecentSessions: GetRecentSessionsContract = GetRecentSessionsContract { _, _, _ ->
            history
        },
    ): GetLoadAdviceUseCase {
        val preferences = TestPreferenceRepository()
        preferences.activePlan.set(
            ActivePlan(
                planID = 1L,
                startDate = PLAN_START,
                cycleCount = 10,
                deloadEveryCycles = deloadEveryCycles,
            )
        )
        return GetLoadAdviceUseCase(
            suggestLoad = SuggestLoadUseCase(),
            getRecentSessions = getRecentSessions,
            getActivePlan =
                GetActivePlanUseCase(
                    getPlanUseCase =
                        GetPlanUseCase {
                            flowOf(
                                Plan(
                                    id = 1L,
                                    name = null,
                                    description = "",
                                    items = listOf(Plan.Item.Rest),
                                )
                            )
                        },
                    activePlan = preferences.activePlan,
                ),
            preferenceRepository = preferences,
        )
    }

    private fun workout(
        start: LocalDateTime = WORKOUT_START,
        endDate: LocalDateTime? = null,
        exerciseType: ExerciseType = ExerciseType.Weight,
        goal: Workout.Goal = Workout.Goal.default,
    ) =
        Workout(
            id = 1L,
            routineID = 1L,
            name = "Push",
            startDate = start,
            endDate = endDate,
            notes = "",
            exercises =
                listOf(
                    Workout.Exercise(
                        id = BENCH_ID,
                        name = Name.Raw("Bench press"),
                        exerciseType = exerciseType,
                        mainMuscles = emptyList(),
                        secondaryMuscles = emptyList(),
                        tertiaryMuscles = emptyList(),
                        goal = goal,
                        sets = emptyList(),
                        loadIncrement = 2.5,
                    )
                ),
        )

    private fun twoSessions() =
        listOf(
            group(load = 100.0, reps = 9, date = PLAN_START.minusDays(7).atTime(18, 0)),
            group(load = 100.0, reps = 8, date = PLAN_START.minusDays(14).atTime(18, 0)),
        )

    private fun group(load: Double, reps: Int, date: LocalDateTime) =
        ExerciseSetGroup(
            workoutID = date.dayOfYear.toLong(),
            workoutName = "Push",
            exerciseID = BENCH_ID,
            sets =
                listOf(
                    ExerciseSet.Weight(
                        weight = load,
                        reps = reps,
                        weightUnit = MassUnit.Kilograms,
                        rir = 2,
                    )
                ),
            workoutStartDate = date,
        )

    private companion object {
        const val BENCH_ID = 3L
        val PLAN_START: LocalDate = LocalDate.of(2026, 9, 1)
        val WORKOUT_START: LocalDateTime = PLAN_START.atTime(18, 0)
    }
}
