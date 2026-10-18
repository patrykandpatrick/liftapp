package com.patrykandpatrick.liftapp.domain.progression

/** What the workout can say about an exercise's load before its first set. */
sealed interface LoadAdvice {

    /**
     * Exactly one session to read. The loop waits for a second, and saying so once is better than
     * leaving the user to wonder why nothing appears.
     */
    data object OneMoreSession : LoadAdvice
}

/**
 * What to put on the bar next time, and why.
 *
 * [reason] is not decoration. A number that changes on its own is indistinguishable from a bug, so
 * nothing here is allowed to reach a user without the sentence that explains it — which is why the
 * reason is part of the suggestion rather than something a caller may forget to ask for.
 *
 * [load], and every weight inside [reason], is in the same terms the set records: the bar weight
 * for a loaded lift, the weight hung off it for a bodyweight one.
 */
data class LoadSuggestion(val load: Double, val reason: SuggestionReason) : LoadAdvice

/**
 * Why the loop landed where it did, carrying the numbers the sentence needs rather than a pre-built
 * string — the wording and its plurals belong with the resources.
 *
 * The flags on [LoadUp], [Hold] and [BackOff] are set only when the effort, not the rep count,
 * decided the outcome, so the sentence can say which of the two it was.
 */
sealed interface SuggestionReason {

    /** The top of the rep range came up, counting any reps left beyond the target effort. */
    data class LoadUp(val previousLoad: Double, val easierThanAsked: Boolean = false) :
        SuggestionReason

    /** Reps still to find inside the range at this load. */
    data class Hold(
        val achievedReps: Int,
        val maxReps: Int,
        val harderThanAsked: Boolean = false,
    ) : SuggestionReason

    /** The range was missed once the effort is accounted for: give some back. */
    data class BackOff(
        val achievedReps: Int,
        val minReps: Int,
        val harderThanAsked: Boolean = false,
    ) : SuggestionReason

    /** The arithmetic asked for more than a tenth in one step, and was held to it. */
    data class Capped(val uncapped: Double, val within: SuggestionReason) : SuggestionReason

    /** Two sessions or more without progress. [step] is the cheapest thing left to try. */
    data class Stalled(val step: StallEscalation, val sessions: Int) : SuggestionReason

    /** The plan's deload week: about a tenth under [previousLoad], the last normal week's load. */
    data class Deload(val previousLoad: Double) : SuggestionReason

    /** The plan asks for [percent] of a one-rep max, estimated at [oneRepMax] from recent sets. */
    data class PercentOfOneRepMax(val percent: Double, val oneRepMax: Double) : SuggestionReason
}

/**
 * What to try about a stall, cheapest first.
 *
 * The order is the point. Changing the exercise is the last resort because users read it as the app
 * giving up, and it is also the most expensive thing to be wrong about.
 */
enum class StallEscalation {
    /** RIR is a ±1 measurement and most people under-report. Ask before changing anything. */
    CheckEffort,

    /** Take about a tenth off and build back through it. */
    DeloadExercise,

    /** The exercise may be fine and the muscle simply over-committed elsewhere. */
    ReduceVolumeElsewhere,

    /** Only once the cheaper answers have been tried. */
    ChangeExercise,
}
