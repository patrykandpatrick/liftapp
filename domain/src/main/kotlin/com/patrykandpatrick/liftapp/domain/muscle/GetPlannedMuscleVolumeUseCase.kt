package com.patrykandpatrick.liftapp.domain.muscle

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import com.patrykandpatrick.liftapp.domain.plan.GetActivePlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.PlanSchedule
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * What the active plan asks of each muscle in the cycle a date falls in, as a weekly rate.
 *
 * The same fold as logged volume, over targets rather than over sets that happened — which is what
 * makes the two comparable at all. A plan whose cycle is not seven days long is restated to a week
 * by [MuscleVolume.perWeekOfCycle], because a cycle repeats and so its rate is knowable.
 *
 * Null where there is nothing to plan against: no active plan, or a date the plan does not cover.
 */
class GetPlannedMuscleVolumeUseCase
@Inject
constructor(
    private val getActivePlan: GetActivePlanUseCase,
    private val getPlannedCycleVolume: GetPlannedCycleVolumeContract,
) {
    operator fun invoke(date: LocalDate = LocalDate.now()): Flow<MuscleVolume?> =
        getActivePlan().flatMapLatest { active ->
            val (activePlan, plan) = active ?: return@flatMapLatest flowOf(null)
            val schedule =
                PlanSchedule(
                    startDate = activePlan.startDate,
                    itemCount = plan.items.size,
                    cycleCount = activePlan.cycleCount,
                )
            val position = schedule.positionOf(date) ?: return@flatMapLatest flowOf(null)

            getPlannedCycleVolume
                .getPlannedCycleVolume(activePlan.planID, position.cycleIndex)
                .map { exercises -> exercises.fold().perWeekOfCycle(plan.items.size.toLong()) }
        }

    /** Cardio credits nothing here for the same reason it credits nothing when logged. */
    private fun List<PlannedExerciseVolume>.fold(): MuscleVolume =
        fold(MuscleVolume.Empty) { volume, exercise ->
            if (exercise.exerciseType == ExerciseType.Cardio) {
                volume
            } else {
                volume + MuscleVolume.of(sets = exercise.setCount, muscles = exercise)
            }
        }
}
