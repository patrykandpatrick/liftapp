package com.patrykandpatrick.liftapp.domain.plan

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemType
import com.patrykandpatrick.liftapp.domain.routine.RoutineItemWithExercises
import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatrick.liftapp.domain.target.ExerciseTarget
import com.patrykandpatrick.liftapp.domain.target.GenerationParameters
import com.patrykandpatrick.liftapp.domain.target.TargetGenerator
import com.patrykandpatrick.liftapp.domain.target.TrainingIntent
import com.patrykandpatrick.liftapp.domain.target.UpsertExerciseTargetsContract
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

class SetActivePlanUseCaseTest {

    @Test
    fun `Activating a plan records it and lays out its days`() = runTest {
        val preferences = TestPreferenceRepository()
        val writer = RecordingTargetWriter()

        useCase(preferences, writer)(plan, START_DATE, parameters(cycleCount = 2))

        val active = preferences.activePlan.get().first()
        assertEquals(PLAN_ID, active?.planID)
        assertEquals(START_DATE, active?.startDate)
        assertEquals(2, active?.cycleCount)
    }

    @Test
    fun `Reconfiguring leaves the targets alone, so an edited one survives`() = runTest {
        val writer = RecordingTargetWriter()

        useCase(writer = writer)(plan, START_DATE, parameters())

        assertTrue(writer.replaced.isEmpty(), "Reconfiguring rewrote targets it was not asked to.")
        assertTrue(writer.filledGaps.isNotEmpty(), "A day with no target was left without one.")
    }

    @Test
    fun `Asking to rebuild rewrites every target`() = runTest {
        val writer = RecordingTargetWriter()

        useCase(writer = writer)(plan, START_DATE, parameters(), rebuildTargets = true)

        assertTrue(writer.replaced.isNotEmpty())
        assertTrue(writer.filledGaps.isEmpty(), "A rebuild should not also fill gaps.")
    }

    @Test
    fun `The generated targets cover every cycle the plan was given`() = runTest {
        val writer = RecordingTargetWriter()

        useCase(writer = writer)(plan, START_DATE, parameters(cycleCount = 3))

        assertEquals(setOf(0, 1, 2), writer.filledGaps.map { it.cycleIndex }.toSet())
    }

    private fun useCase(
        preferences: TestPreferenceRepository = TestPreferenceRepository(),
        writer: RecordingTargetWriter = RecordingTargetWriter(),
    ) =
        SetActivePlanUseCase(
            activePlan = preferences.activePlan,
            addPlanItemsScheduleContract = NoOpSchedule,
            generateTargets = TargetGenerator(),
            upsertTargets = writer,
        )

    private fun parameters(cycleCount: Int = 1) =
        GenerationParameters(intent = TrainingIntent.Both, cycleCount = cycleCount)

    private object NoOpSchedule : AddPlanItemsScheduleContract {
        override suspend fun addPlanItemsSchedule(
            plan: Plan,
            startDate: LocalDate,
            cycleCount: Int,
        ) = Unit
    }

    private class RecordingTargetWriter : UpsertExerciseTargetsContract {
        val replaced = mutableListOf<ExerciseTarget>()
        val filledGaps = mutableListOf<ExerciseTarget>()

        override suspend fun upsertTargets(targets: List<ExerciseTarget>) {
            replaced += targets
        }

        override suspend fun addMissingTargets(targets: List<ExerciseTarget>) {
            filledGaps += targets
        }
    }

    private companion object {
        const val PLAN_ID = 1L
        val START_DATE: LocalDate = LocalDate.of(2026, 9, 1)

        val plan =
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
                                    listOf(
                                        RoutineItemWithExercises(
                                            id = 0L,
                                            type = RoutineItemType.Exercise,
                                            exercises =
                                                listOf(
                                                    RoutineExerciseItem(
                                                        id = 6L,
                                                        name = "Bench Press",
                                                        muscles = "Chest",
                                                        type = ExerciseType.Weight,
                                                        role = ExerciseRole.PrimaryCompound,
                                                    )
                                                ),
                                        )
                                    ),
                                primaryMuscles = emptyList(),
                                secondaryMuscles = emptyList(),
                                tertiaryMuscles = emptyList(),
                            )
                        ),
                        Plan.Item.Rest,
                    ),
            )
    }
}
