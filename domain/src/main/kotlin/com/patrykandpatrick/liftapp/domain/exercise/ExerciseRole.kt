package com.patrykandpatrick.liftapp.domain.exercise

/**
 * What an exercise is for, which is what indexes every rep-range and RIR default: a primary
 * compound belongs at 3–5 reps and 1–2 RIR for strength, an isolation lift at 10–15 reps and 0–1
 * RIR.
 *
 * Orthogonal to [ExerciseType], not derived from it — a weighted pull-up is `Calisthenics` *and* a
 * primary compound. The "bodyweight-limited" prescription is selected by type at lookup time rather
 * than stored, so the same fact is not encoded twice.
 *
 * Wherever a role is expected the value is nullable: `Cardio` and `Time` exercises genuinely have
 * none, and null is also the honest answer for an exercise nobody has classified. Everything
 * downstream has to degrade gracefully rather than assume a role.
 *
 * Constant names are the persisted format; renaming one is a silent data break.
 */
enum class ExerciseRole {
    /** Squat, bench, deadlift, overhead press, weighted pull-up. Carries the load-driven work. */
    PrimaryCompound,

    /** Row, leg press, dip, lunge, RDL. Multi-joint, but not what the program is built around. */
    SecondaryCompound,

    /** Curl, extension, raise, calf, fly. Single-joint, light, and kept at ~5 reps and above. */
    Isolation,
}
