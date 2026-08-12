package com.patrykandpatrick.liftapp.domain.exercise

import com.patrykandpatrick.liftapp.domain.muscle.MuscleContainer

private const val ISOLATION_MAX_MUSCLES = 3
private const val SECONDARY_COMPOUND_MAX_MUSCLES = 6

/**
 * Whether an [ExerciseRole] means anything for this type. A run and a timed hold have no role, so
 * asking for one is a question with no right answer — the editor hides the field rather than offer
 * a choice whose only correct value is "none".
 */
val ExerciseType.hasRole: Boolean
    get() =
        when (this) {
            ExerciseType.Weight,
            ExerciseType.Calisthenics,
            ExerciseType.Reps -> true

            ExerciseType.Cardio,
            ExerciseType.Time -> false
        }

/**
 * Whether a set of this type records an external load. `Reps` does not — `ExerciseSet.Reps` carries
 * reps and nothing else — so its only possible progression is reps, the same as for `Cardio` and
 * `Time`. Only where this is true is [ProgressionPolicy] a real choice.
 */
val ExerciseType.carriesLoad: Boolean
    get() =
        when (this) {
            ExerciseType.Weight,
            ExerciseType.Calisthenics -> true

            ExerciseType.Reps,
            ExerciseType.Cardio,
            ExerciseType.Time -> false
        }

/**
 * A guess at an exercise's [ExerciseRole] from how many muscles it lists across all three tiers.
 *
 * **Offer it, never store it.** Measured against the hand-labelled seed catalog it separates
 * isolation from compound 84% of the time, but the three-way split asked for here is right about
 * 70% of the time and finds 3 of 13 primary compounds. The failure is systematic rather than
 * random: the muscle lists were authored to drive the muscle diagram, so their granularity tracks
 * how thoroughly each entry was annotated. A lat pulldown and a barbell row are the same movement
 * pattern and land on opposite sides of the boundary purely because one list is longer than the
 * other.
 *
 * Persisting a guess this weak would have the app prescribe 3–5 reps at 2 RIR for a lateral raise,
 * and would erase the difference between "we guessed" and "the user said". Pre-selecting it in the
 * editor costs nothing, because the user sees it before it is saved.
 *
 * Returns null when the type rules a role out, which is the same answer [Exercise.role] carries for
 * an unclassified exercise.
 */
fun suggestExerciseRole(type: ExerciseType, muscles: MuscleContainer): ExerciseRole? =
    suggestExerciseRole(
        type = type,
        muscleCount =
            muscles.primaryMuscles.size +
                muscles.secondaryMuscles.size +
                muscles.tertiaryMuscles.size,
    )

fun suggestExerciseRole(type: ExerciseType, muscleCount: Int): ExerciseRole? =
    when {
        type.hasRole.not() -> null
        muscleCount <= ISOLATION_MAX_MUSCLES -> ExerciseRole.Isolation
        muscleCount <= SECONDARY_COMPOUND_MAX_MUSCLES -> ExerciseRole.SecondaryCompound
        else -> ExerciseRole.PrimaryCompound
    }

/**
 * The progression an exercise of this [type] starts on. Where the type carries no load this is not
 * a default but the only possible answer, which is why it is also what the editor coerces to.
 */
fun defaultProgressionPolicy(type: ExerciseType): ProgressionPolicy =
    if (type.carriesLoad) ProgressionPolicy.DoubleProgression else ProgressionPolicy.RepsOnly
