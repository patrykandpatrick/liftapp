package com.patrykandpatrick.liftapp.domain.muscle

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.plan.ActivePlan
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetPlannedMuscleVolumeUseCaseTest {

    @Test
    fun `A cycle's targets are credited to muscles the same way logged sets are`() = runTest {
        val volume = useCase(planned = listOf(bench(sets = 4))).invoke(START).first()

        assertEquals(4, volume?.wholeSetsOf(Muscle.Chest))
        assertEquals(2, volume?.wholeSetsOf(Muscle.Triceps), "An assistant is credited at half.")
    }

    @Test
    fun `A cycle shorter than a week is restated upward, because a cycle repeats`() = runTest {
        // Four sets over a four-day cycle is seven-quarters of that in a week.
        val volume = useCase(planned = listOf(bench(sets = 4)), dayCount = 4).invoke(START).first()

        assertEquals(7, volume?.wholeSetsOf(Muscle.Chest))
    }

    @Test
    fun `A cycle longer than a week is restated downward`() = runTest {
        val volume =
            useCase(planned = listOf(bench(sets = 14)), dayCount = 14).invoke(START).first()

        assertEquals(7, volume?.wholeSetsOf(Muscle.Chest))
    }

    @Test
    fun `A seven-day cycle is left alone`() = runTest {
        val volume = useCase(planned = listOf(bench(sets = 9))).invoke(START).first()

        assertEquals(9, volume?.wholeSetsOf(Muscle.Chest))
    }

    @Test
    fun `Cardio credits nothing, exactly as it credits nothing when logged`() = runTest {
        val volume =
            useCase(
                    planned =
                        listOf(
                            PlannedExerciseVolume(
                                setCount = 5,
                                exerciseType = ExerciseType.Cardio,
                                primaryMuscles = listOf(Muscle.Quadriceps),
                                secondaryMuscles = emptyList(),
                                tertiaryMuscles = emptyList(),
                            )
                        )
                )
                .invoke(START)
                .first()

        assertEquals(0, volume?.wholeSetsOf(Muscle.Quadriceps))
    }

    @Test
    fun `No active plan means nothing is planned, rather than nothing prescribed`() = runTest {
        val preferences = TestPreferenceRepository()
        val useCase =
            GetPlannedMuscleVolumeUseCase(
                getActivePlan =
                    GetActivePlanUseCase(
                        getPlanUseCase = GetPlanUseCase { flowOf(plan(7)) },
                        activePlan = preferences.activePlan,
                    ),
                getPlannedCycleVolume = { _, _ -> flowOf(emptyList()) },
            )

        assertNull(useCase(START).first())
    }

    @Test
    fun `A date the plan does not cover has nothing planned`() = runTest {
        val volume = useCase(planned = listOf(bench(sets = 4))).invoke(START.plusDays(70)).first()

        assertNull(volume)
    }

    @Test
    fun `The cycle the date falls in is the one folded`() = runTest {
        var askedFor: Int? = null
        val useCase =
            GetPlannedMuscleVolumeUseCase(
                getActivePlan = activePlanUseCase(dayCount = 7),
                getPlannedCycleVolume = { _, cycleIndex ->
                    askedFor = cycleIndex
                    flowOf(listOf(bench(sets = 3)))
                },
            )

        useCase(START.plusDays(15)).first()

        assertEquals(2, askedFor, "Day 15 of a seven-day cycle is the third cycle.")
    }

    private suspend fun useCase(planned: List<PlannedExerciseVolume>, dayCount: Int = 7) =
        GetPlannedMuscleVolumeUseCase(
            getActivePlan = activePlanUseCase(dayCount),
            getPlannedCycleVolume = { _, _ -> flowOf(planned) },
        )

    private suspend fun activePlanUseCase(dayCount: Int): GetActivePlanUseCase {
        val preferences = TestPreferenceRepository()
        preferences.activePlan.set(activePlan())
        return GetActivePlanUseCase(
            getPlanUseCase = GetPlanUseCase { flowOf(plan(dayCount)) },
            activePlan = preferences.activePlan,
        )
    }

    private fun bench(sets: Int) =
        PlannedExerciseVolume(
            setCount = sets,
            exerciseType = ExerciseType.Weight,
            primaryMuscles = listOf(Muscle.Chest),
            secondaryMuscles = listOf(Muscle.Triceps),
            tertiaryMuscles = emptyList(),
        )

    private fun activePlan() = ActivePlan(planID = 1L, startDate = START, cycleCount = 6)

    private fun plan(dayCount: Int) =
        Plan(
            id = 1L,
            name = "Plan",
            description = "",
            items =
                List(dayCount) {
                    Plan.Item.Routine(
                        RoutineWithExercises(
                            id = 1L,
                            name = "Day",
                            items = emptyList(),
                            primaryMuscles = emptyList(),
                            secondaryMuscles = emptyList(),
                            tertiaryMuscles = emptyList(),
                        )
                    )
                },
        )

    private companion object {
        val START: LocalDate = LocalDate.of(2026, 9, 1)
    }
}
