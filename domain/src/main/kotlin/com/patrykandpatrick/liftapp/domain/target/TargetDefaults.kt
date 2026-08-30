package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * What to prescribe when nothing has been authored — a workout started outside a plan, or an
 * exercise added to one after its targets were generated.
 *
 * These are the "Both" column of the rep-range table in `docs/workout-programming.md`, because an
 * unplanned workout has stated no intent, paired with that document's rest defaults. A plan's own
 * targets come from the generator instead, which indexes the same tables by (role × intent).
 *
 * Progression policy deliberately does not appear. It decides how load is allowed to move between
 * sessions, and these targets carry no load at all — [SetTarget.loadPercent] stays null until a
 * generator that knows the lifter's numbers fills it in.
 */
object TargetDefaults {

    /** The set count the app has always defaulted to, and a reasonable floor for one exercise. */
    const val SET_COUNT = 3

    fun of(
        exerciseType: ExerciseType,
        role: ExerciseRole?,
        setCount: Int = SET_COUNT,
    ): TargetTemplate {
        val prescription = prescriptionFor(exerciseType, role)
        return TargetTemplate(
            sets = List(setCount) { prescription.set },
            restTime = prescription.restTime,
        )
    }

    /**
     * Type outranks role for [ExerciseType.Calisthenics] and [ExerciseType.Reps]: a weighted
     * pull-up is a primary compound, but what it can be loaded with is bounded by the lifter's body
     * weight, so it takes the bodyweight-limited row rather than the 4–6 one. This is the lookup
     * [ExerciseRole] says is done by type rather than stored.
     */
    private fun prescriptionFor(exerciseType: ExerciseType, role: ExerciseRole?): Prescription =
        when (exerciseType) {
            ExerciseType.Cardio,
            ExerciseType.Time -> Prescription(HELD_OR_TRAVELLED, MODERATE_REST)
            ExerciseType.Calisthenics,
            ExerciseType.Reps -> Prescription(SetTarget(8, 15, rir = 1), MODERATE_REST)
            ExerciseType.Weight ->
                when (role) {
                    ExerciseRole.PrimaryCompound ->
                        Prescription(SetTarget(4, 6, rir = 2), HEAVY_COMPOUND_REST)
                    ExerciseRole.SecondaryCompound ->
                        Prescription(SetTarget(6, 10, rir = 2), MODERATE_REST)
                    ExerciseRole.Isolation ->
                        Prescription(SetTarget(10, 15, rir = 1), ISOLATION_REST)
                    // Nobody has classified it, so this is the old flat default: wide enough to be
                    // wrong about nothing in particular.
                    null -> Prescription(SetTarget(8, 12, rir = 2), MODERATE_REST)
                }
        }

    private class Prescription(val set: SetTarget, val restTime: Duration)

    private val HEAVY_COMPOUND_REST = 3.minutes
    private val MODERATE_REST = 2.minutes
    private val ISOLATION_REST = 90.seconds

    /** Reps say nothing about a run or a plank, so only the duration and distance fields carry. */
    private val HELD_OR_TRAVELLED =
        SetTarget(minReps = 0, maxReps = 0, duration = 5.minutes, distance = 0.5, calories = 50.0)
}

/**
 * A prescription with no plan position yet — what [TargetDefaults] and the generator both return.
 */
data class TargetTemplate(val sets: List<SetTarget>, val restTime: Duration) {
    fun at(
        planID: Long,
        cycleIndex: Int,
        planItemOrderIndex: Int,
        exerciseID: Long,
    ): ExerciseTarget =
        ExerciseTarget(
            planID = planID,
            cycleIndex = cycleIndex,
            planItemOrderIndex = planItemOrderIndex,
            exerciseID = exerciseID,
            sets = sets,
            restTime = restTime,
        )
}
