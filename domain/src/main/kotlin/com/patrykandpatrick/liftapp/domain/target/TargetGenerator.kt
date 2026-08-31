package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Writes out a whole plan's prescription: one [ExerciseTarget] per (cycle, day, exercise).
 *
 * Everything is generated and stored rather than derived on read, exactly as the schedule already
 * materialises one row per date. That is what makes every cell editable afterwards without any
 * merge logic — an edited target is simply the row that is there.
 *
 * Two things ramp across the cycles, and only these two, because the reference is emphatic that
 * complex periodization does not change outcomes:
 *
 * - **RIR ramps down**, from [GenerationParameters.startingRir] to the bottom of the role's band.
 *   Early cycles buy volume cheaply; the last extracts the most stimulus before a reset.
 * - **Set count ramps up**, roughly doubling by the last cycle, which is the MEV → MRV climb.
 *
 * Deloads and mesocycle segmentation are not here. A plan run long enough to need a second block
 * ramps straight through where it should reset, which is the gap Phase 7 closes with a deload flag
 * on the cycle.
 */
class TargetGenerator @Inject constructor() {

    operator fun invoke(plan: Plan, parameters: GenerationParameters): List<ExerciseTarget> =
        buildList {
            repeat(parameters.cycleCount) { cycleIndex ->
                plan.items.forEachIndexed { itemOrderIndex, item ->
                    val routine = (item as? Plan.Item.Routine)?.routine ?: return@forEachIndexed
                    routine.exercises
                        .distinctBy { it.id }
                        .forEach { exercise ->
                            add(
                                target(
                                    exercise = exercise,
                                    planID = plan.id,
                                    cycleIndex = cycleIndex,
                                    itemOrderIndex = itemOrderIndex,
                                    parameters = parameters,
                                )
                            )
                        }
                }
            }
        }

    private fun target(
        exercise: RoutineExerciseItem,
        planID: Long,
        cycleIndex: Int,
        itemOrderIndex: Int,
        parameters: GenerationParameters,
    ): ExerciseTarget {
        val prescription = TargetPrescriptions.of(exercise.type, exercise.role, parameters.intent)
        val set =
            prescription.setTarget(
                rir = prescription.rirRange?.let { rirFor(cycleIndex, it, parameters) }
            )
        return ExerciseTarget(
            planID = planID,
            cycleIndex = cycleIndex,
            planItemOrderIndex = itemOrderIndex,
            exerciseID = exercise.id,
            sets = List(setCountFor(cycleIndex, parameters)) { set },
            restTime = prescription.restTime,
        )
    }

    /**
     * Evenly spaced from the declared start down to the bottom of the role's band, so a four-cycle
     * plan starting at 3 reads 3, 2, 1, 1 rather than bottoming out in week two.
     *
     * A start already at or below the band's floor is left alone: the user asked for hard sets from
     * the first week, and a ramp that raised RIR to get somewhere to fall from would be inventing
     * easier work than was asked for.
     */
    private fun rirFor(
        cycleIndex: Int,
        rirRange: IntRange,
        parameters: GenerationParameters,
    ): Int {
        val floor = rirRange.first
        val start = parameters.startingRir
        if (start <= floor || parameters.cycleCount <= 1) return minOf(start, rirRange.last)
        val progress = cycleIndex.toDouble() / (parameters.cycleCount - 1)
        return (start - progress * (start - floor)).roundToInt().coerceAtLeast(floor)
    }

    /**
     * Linear from the base count to roughly double it by the last cycle — the MEV → MRV climb, at
     * the grain the app can actually author.
     *
     * The reference states that climb per muscle, not per exercise. Doubling every exercise doubles
     * each muscle too, so the shape survives the translation; what it cannot do is notice that two
     * exercises share a muscle. Phase 7's fold over planned volume is what would.
     */
    private fun setCountFor(cycleIndex: Int, parameters: GenerationParameters): Int {
        val base = parameters.baseSetCount
        if (parameters.cycleCount <= 1) return base
        val progress = cycleIndex.toDouble() / (parameters.cycleCount - 1)
        return base + (progress * base).roundToInt()
    }
}

/**
 * What the plan's author chose, as opposed to what the tables know.
 *
 * [startingRir] is the top of the mesocycle ramp. Three is the reference's figure and the honest
 * default: it leaves room to tighten for three or four more cycles before the sets are all-out.
 */
data class GenerationParameters(
    val intent: TrainingIntent,
    val cycleCount: Int,
    val startingRir: Int = DEFAULT_STARTING_RIR,
    val baseSetCount: Int = TargetDefaults.SET_COUNT,
) {
    companion object {
        const val DEFAULT_STARTING_RIR = 3
    }
}
