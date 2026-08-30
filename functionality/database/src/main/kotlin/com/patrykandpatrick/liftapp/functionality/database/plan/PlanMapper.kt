package com.patrykandpatrick.liftapp.functionality.database.plan

import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.functionality.database.routine.RoutineMapper
import javax.inject.Inject

class PlanMapper @Inject constructor(private val routineMapper: RoutineMapper) {

    fun toDomain(plans: List<PlanWithRoutine>): List<Plan> =
        plans
            .groupBy { it.plan }
            .map { (plan, items) ->
                val routineIndexes = items.groupBy { it.orderIndex }
                Plan(
                    id = plan.id,
                    name = plan.name.takeUnless { it.isBlank() },
                    description = plan.description,
                    items =
                        (0 until plan.itemCount).map { index ->
                            val plansWithRoutines = routineIndexes[index]
                            if (plansWithRoutines != null) {
                                val routineWithExercises =
                                    routineMapper.toDomainFlat(
                                        checkNotNull(plansWithRoutines.first().routine),
                                        plansWithRoutines.mapNotNull { it.exercise },
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
            routineMapper.toDomainFlat(
                routine = routine,
                exercises = schedule.mapNotNull { it.exercise },
            )
        return Plan.Item.Routine(routineWithExercises)
    }
}
