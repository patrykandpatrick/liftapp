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
                                Plan.Item.RoutineItem(routineWithExercises)
                            } else {
                                Plan.Item.RestItem
                            }
                        },
                )
            }
}
