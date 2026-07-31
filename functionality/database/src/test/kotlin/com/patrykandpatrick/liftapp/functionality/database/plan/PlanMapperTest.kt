package com.patrykandpatrick.liftapp.functionality.database.plan

import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.functionality.database.exercise.ExerciseMapper
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineEntity
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineMapper
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class PlanMapperTest {

    private val mapper =
        PlanMapper(
            RoutineMapper(
                json = Json,
                stringProvider = TestStringProvider,
                exerciseMapper = ExerciseMapper(TestStringProvider),
            )
        )

    @Test
    fun `A plan reads back the same routine on every day it was put on`() {
        val routine = RoutineEntity(id = ROUTINE_ID, name = "Push", orderIndex = 0)
        val plan = PlanEntity(id = PLAN_ID, name = "Plan", description = "", itemCount = 3)

        val plans =
            mapper.toDomain(
                listOf(
                    planWithRoutine(plan, routine, orderIndex = 0),
                    planWithRoutine(plan, routine, orderIndex = 2),
                )
            )

        val items = plans.single().items
        assertEquals(3, items.size)
        assertIs<Plan.Item.Routine>(items[0])
        // The day the plan left empty.
        assertIs<Plan.Item.Rest>(items[1])
        assertIs<Plan.Item.Routine>(items[2])
        assertEquals(ROUTINE_ID, (items[0] as Plan.Item.Routine).routine.id)
        assertEquals(ROUTINE_ID, (items[2] as Plan.Item.Routine).routine.id)
    }

    private fun planWithRoutine(plan: PlanEntity, routine: RoutineEntity, orderIndex: Int) =
        PlanWithRoutine(
            plan = plan,
            orderIndex = orderIndex,
            routine = routine,
            exercise = null,
            goalEntity = null,
        )

    private companion object {
        const val PLAN_ID = 1L
        const val ROUTINE_ID = 5L
    }
}
