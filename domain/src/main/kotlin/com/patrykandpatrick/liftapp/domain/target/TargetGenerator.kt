package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.plan.Plan
import com.patrykandpatrick.liftapp.domain.routine.RoutineExerciseItem
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Writes out a whole plan's prescription: one [ExerciseTarget] per (cycle, day, exercise).
 *
 * Everything is generated and stored rather than derived on read, exactly as the schedule already
 * materialises one row per date. That is what makes every cell editable afterwards without any
 * merge logic — an edited target is simply the row that is there.
 *
 * Two things ramp across a block, and only these two, because the reference is emphatic that
 * complex periodization does not change outcomes:
 *
 * - **RIR ramps down**, from [GenerationParameters.startingRir] to the bottom of the role's band.
 *   Early cycles buy volume cheaply; the last extracts the most stimulus before a reset.
 * - **Set count ramps up**, roughly doubling by the last accumulating cycle — the MEV → MRV climb.
 *
 * Both read [Mesocycle], so a block that ends in a deload starts the next one over rather than
 * ramping straight through. The deload itself is the reference's composition: sets roughly halved,
 * RIR raised well clear of failure, the same days kept, and never nothing.
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
        val isDeload = parameters.mesocycle.isDeload(cycleIndex)
        val position = parameters.mesocycle.accumulationPosition(cycleIndex, parameters.cycleCount)
        val accumulatedSets = setCountFor(position, parameters)
        val set =
            prescription.setTarget(
                rir =
                    prescription.rirRange?.let { range ->
                        if (isDeload) deloadRir(range) else rirFor(position, range, parameters)
                    }
            )
        return ExerciseTarget(
            planID = planID,
            cycleIndex = cycleIndex,
            planItemOrderIndex = itemOrderIndex,
            exerciseID = exercise.id,
            sets = List(if (isDeload) deloadSets(accumulatedSets) else accumulatedSets) { set },
            restTime = prescription.restTime,
        )
    }

    /**
     * Evenly spaced from the declared start down to the bottom of the role's band, so a block of
     * four starting at 3 reads 3, 2, 2, 1 rather than bottoming out in its second week.
     *
     * A start already at or below the band's floor is left alone: the user asked for hard sets from
     * the first week, and a ramp that raised RIR to get somewhere to fall from would be inventing
     * easier work than was asked for.
     */
    private fun rirFor(
        position: AccumulationPosition,
        rirRange: IntRange,
        parameters: GenerationParameters,
    ): Int {
        val floor = rirRange.first
        val start = parameters.startingRir
        if (start <= floor || position.length <= 1) return minOf(start, rirRange.last)
        return (start - position.progress * (start - floor)).roundToInt().coerceAtLeast(floor)
    }

    /**
     * Well clear of failure — the reference's "raise RIR by two or three" read against the bottom
     * of the band, capped at the highest value a lifter can report.
     */
    private fun deloadRir(rirRange: IntRange): Int =
        (rirRange.first + DELOAD_RIR_INCREASE).coerceAtMost(ExerciseSet.RIR_RANGE.last)

    /**
     * Half of what the week before asked for, rounded up — the reference's "cut sets by roughly
     * half", read against the volume actually being carried into the deload.
     *
     * Under the doubling ramp this lands on the volume the block opened at, which the reference's
     * own worked example undercuts: it ramps 10 → 20 and deloads to about 6 rather than 10. The
     * halving rule is the one stated, though, and the week is lighter than its opening week
     * regardless — the load comes down and [deloadRir] backs the effort well off failure.
     *
     * Never none: reduced training preserves adaptation where stopping does not.
     */
    private fun deloadSets(accumulatedSets: Int): Int = ((accumulatedSets + 1) / 2).coerceAtLeast(1)

    /**
     * Linear from the base count to roughly double it by the last accumulating cycle — the MEV →
     * MRV climb, at the grain the app can actually author.
     *
     * The reference states that climb per muscle, not per exercise. Doubling every exercise doubles
     * each muscle too, so the shape survives the translation; what it cannot do is notice that two
     * exercises share a muscle. The planned-volume fold is what would.
     */
    private fun setCountFor(
        position: AccumulationPosition,
        parameters: GenerationParameters,
    ): Int {
        val base = parameters.baseSetCount
        if (position.length <= 1) return base
        return base + (position.progress * base).roundToInt()
    }

    private companion object {
        /** The reference's "+2–3 RIR"; the top of that, because a deload should be unmistakable. */
        const val DELOAD_RIR_INCREASE = 3
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
    val mesocycle: Mesocycle = Mesocycle.Continuous,
) {
    companion object {
        const val DEFAULT_STARTING_RIR = 3
    }
}
