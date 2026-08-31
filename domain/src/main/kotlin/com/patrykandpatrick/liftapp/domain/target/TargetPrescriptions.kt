package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * The rep-range, RIR and rest tables from `docs/workout-programming.md`, indexed by (role × intent)
 * with type deciding the bodyweight-limited row.
 *
 * One table, two callers: [TargetDefaults] reads it with no intent for a workout run outside any
 * plan, and [TargetGenerator] reads it per plan. Keeping them on one lookup is what stops an
 * unplanned session prescribing something a planned one would not.
 */
internal object TargetPrescriptions {

    /**
     * Type outranks role for [ExerciseType.Calisthenics] and [ExerciseType.Reps]: a weighted
     * pull-up is a primary compound, but what it can be loaded with is bounded by the lifter's body
     * weight, so it takes the bodyweight-limited row rather than the 4–6 one. This is the lookup
     * [ExerciseRole] says is done by type rather than stored.
     */
    fun of(
        exerciseType: ExerciseType,
        role: ExerciseRole?,
        intent: TrainingIntent,
    ): Prescription =
        when (exerciseType) {
            ExerciseType.Cardio,
            ExerciseType.Time -> HELD_OR_TRAVELLED
            ExerciseType.Calisthenics,
            ExerciseType.Reps -> bodyweightLimited(intent)
            ExerciseType.Weight -> loaded(role, intent)
        }

    private fun bodyweightLimited(intent: TrainingIntent): Prescription =
        when (intent) {
            // "Add load, then 5–8" — the reps of a strength set, at the RIR a loaded one gets.
            TrainingIntent.Strength -> Prescription(5, 8, 1..2, MODERATE_REST)
            TrainingIntent.Hypertrophy -> Prescription(8, 20, 0..1, MODERATE_REST)
            TrainingIntent.Both -> Prescription(8, 15, 0..1, MODERATE_REST)
        }

    private fun loaded(role: ExerciseRole?, intent: TrainingIntent): Prescription =
        when (role) {
            ExerciseRole.PrimaryCompound ->
                when (intent) {
                    TrainingIntent.Strength -> Prescription(3, 5, 1..2, HEAVY_COMPOUND_REST)
                    TrainingIntent.Hypertrophy -> Prescription(6, 10, 1..2, MODERATE_REST)
                    TrainingIntent.Both -> Prescription(4, 6, 1..2, HEAVY_COMPOUND_REST)
                }
            ExerciseRole.SecondaryCompound ->
                when (intent) {
                    TrainingIntent.Strength -> Prescription(5, 8, 1..2, MODERATE_REST)
                    TrainingIntent.Hypertrophy -> Prescription(8, 12, 0..2, MODERATE_REST)
                    TrainingIntent.Both -> Prescription(6, 10, 1..2, MODERATE_REST)
                }
            // The strength column is empty for isolation on purpose: single-joint work in low reps
            // is a joint-loading problem with no upside, so a strength block gets the same row.
            ExerciseRole.Isolation -> Prescription(10, 15, 0..1, ISOLATION_REST)
            // Nobody has classified it, so this is the old flat default: wide enough to be wrong
            // about nothing in particular.
            null -> Prescription(8, 12, 1..2, MODERATE_REST)
        }

    private val HEAVY_COMPOUND_REST = 3.minutes
    private val MODERATE_REST = 2.minutes
    private val ISOLATION_REST = 90.seconds

    /** Reps say nothing about a run or a plank, so only the duration and distance fields carry. */
    private val HELD_OR_TRAVELLED =
        Prescription(
            minReps = 0,
            maxReps = 0,
            rirRange = null,
            restTime = MODERATE_REST,
            duration = 5.minutes,
            distance = 0.5,
            calories = 50.0,
        )
}

/**
 * One cell of the tables above. [rirRange] is a band rather than a number because the mesocycle
 * ramp needs both ends: it starts above the band and finishes at its bottom.
 */
internal data class Prescription(
    val minReps: Int,
    val maxReps: Int,
    /** Null where reps in reserve says nothing at all: a run, a held position. */
    val rirRange: IntRange?,
    val restTime: Duration,
    val duration: Duration = Duration.ZERO,
    val distance: Double = 0.0,
    val calories: Double = 0.0,
) {
    fun setTarget(rir: Int?): SetTarget =
        SetTarget(
            minReps = minReps,
            maxReps = maxReps,
            rir = rir,
            duration = duration,
            distance = distance,
            calories = calories,
        )
}
