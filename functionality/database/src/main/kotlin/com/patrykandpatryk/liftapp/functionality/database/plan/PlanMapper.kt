package com.patrykandpatryk.liftapp.functionality.database.plan

import com.patrykandpatryk.liftapp.domain.plan.Plan
import com.patrykandpatryk.liftapp.functionality.database.exercise.ExerciseWithGoalDto
import com.patrykandpatryk.liftapp.functionality.database.routine.RoutineMapper
import javax.inject.Inject

class PlanMapper @Inject constructor(private val routineMapper: RoutineMapper) {

    fun toDomain(plans: List<PlanWithRoutine>): List<Plan> =
        plans
            .groupBy { it.plan }
            .map { (plan, items) ->
                val routineIndexes = items.groupBy { it.orderIndex }
                Plan(
                    id = plan.id,
                    name = plan.name,
                    description = plan.description,
                    items =
                        (0 until plan.itemCount).map { index ->
                            val plansWithRoutines = routineIndexes[index]
                            if (plansWithRoutines != null) {
                                val routineWithExercises =
                                    routineMapper.toDomain(
                                        checkNotNull(plansWithRoutines.first().routine),
                                        plansWithRoutines.mapNotNull { planWithRoutine ->
                                            if (planWithRoutine.exercise == null)
                                                return@mapNotNull null
                                            ExerciseWithGoalDto(
                                                exerciseEntity = planWithRoutine.exercise,
                                                goalEntity = planWithRoutine.goalEntity,
                                            )
                                        },
                                    )
                                Plan.Item.Routine(routineWithExercises)
                            } else {
                                Plan.Item.Rest
                            }
                        },
                )
            }

    fun toDomain(schedule: List<ScheduledRoutine>): Plan.Item? {
        if (schedule.isEmpty()) return null
        val routine = schedule.firstOrNull { it.routine != null }?.routine
        if (routine == null) return Plan.Item.Rest

        val routineWithExercises =
            routineMapper.toDomain(
                routine = routine,
                exercises =
                    schedule.mapNotNull { (_, exercise, goal) ->
                        exercise?.let { ExerciseWithGoalDto(it, goal) }
                    },
            )
        return Plan.Item.Routine(routineWithExercises)
    }
}
