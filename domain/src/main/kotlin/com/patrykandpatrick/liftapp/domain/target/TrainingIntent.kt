package com.patrykandpatrick.liftapp.domain.target

import kotlinx.serialization.Serializable

/**
 * What a plan is being run for, which is what indexes every rep-range and RIR default.
 *
 * A property of the plan rather than of the exercise or the routine: the same bench press belongs
 * at 3–5 reps in a strength block and 6–10 in a hypertrophy one, and it is the block that decides.
 *
 * Constant names are the persisted format; renaming one is a silent data break.
 */
@Serializable
enum class TrainingIntent {
    Strength,
    Hypertrophy,
    Both,
}
