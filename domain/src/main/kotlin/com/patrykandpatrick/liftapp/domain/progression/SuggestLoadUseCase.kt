package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.round

/**
 * What to load the next session with, given what the last few did.
 *
 * The reference's loop: fill the rep range at a load, then add load and drop back to the bottom of
 * it. The estimated one-rep max carries the load across that reset, so the new weight is the one
 * that puts the *bottom* of the range at the target effort rather than a fixed step up.
 *
 * Three guards are non-negotiable and each is enforced here rather than left to callers:
 *
 * - **Never a jump over a tenth.** Whatever the arithmetic says. It applies downward too — a
 *   back-off steeper than a tenth in one step is as wrong as a climb.
 * - **Two sessions before acting.** One bad session is noise, and the loop stays quiet until it has
 *   something to compare against. The plan's deload week is the exception: it is scheduled rather
 *   than read from the history, so one session to come down from is enough.
 * - **Always a reason.** [LoadSuggestion] cannot be built without one.
 *
 * Returns null where there is nothing honest to say: an exercise that progresses reps rather than
 * load, too little history to read, or a bodyweight lift with nothing on the belt to take off.
 */
class SuggestLoadUseCase @Inject constructor() {

    /**
     * [history] is most recent first. [isDeloadWeek] says the session being planned falls in the
     * plan's deload week.
     *
     * A plan adding sets this cycle does not hold the load back. The reference says to progress one
     * or the other, but the set ramp is scheduled rather than chosen, and a generated plan steps it
     * up in most weeks of a block — honouring the rule would leave the load stuck for the block.
     */
    operator fun invoke(
        history: List<ExerciseSession>,
        target: SetTarget,
        policy: ProgressionPolicy,
        loadIncrement: Double?,
        massUnit: MassUnit,
        isDeloadWeek: Boolean = false,
    ): LoadSuggestion? {
        // Reps-only exercises never get a load suggestion: the smallest step their equipment
        // offers is a large fraction of what they carry.
        if (policy == ProgressionPolicy.RepsOnly) return null

        val sessions = history.usable()
        val lastSet = sessions.firstOrNull()?.firstWorkingSet ?: return null
        val lastLoad = lastSet.totalWeight ?: return null
        if (lastLoad <= 0.0) return null

        val toBar =
            BarTerms(lastSet.bodyWeightOffset, loadIncrement?.let { incrementIn(it, massUnit) })

        if (isDeloadWeek) {
            return toBar.suggestion(
                totalLoad = lastLoad * (1.0 - DELOAD_FRACTION),
                lastLoad = lastLoad,
                reason = SuggestionReason.Deload(previousLoad = lastLoad),
            )
        }
        if (sessions.size < MINIMUM_SESSIONS) return null

        val stall = stalledSessions(sessions, target)
        val reason =
            if (stall >= MINIMUM_SESSIONS) {
                SuggestionReason.Stalled(escalationFor(stall), stall)
            } else {
                progressionReason(lastSet, target)
            }

        val raw = loadFor(reason, lastLoad, lastSet, sessions, target, massUnit)
        val (capped, cappedReason) = capToTenth(raw, lastLoad, reason)
        return toBar.suggestion(totalLoad = capped, lastLoad = lastLoad, reason = cappedReason)
    }

    /** True when the loop has exactly one session to read, and a second would let it speak. */
    fun isOneSessionShort(history: List<ExerciseSession>, policy: ProgressionPolicy): Boolean =
        policy != ProgressionPolicy.RepsOnly && history.usable().size == MINIMUM_SESSIONS - 1

    /** Deload sessions were light by design, so progressing from one would progress from a rest. */
    private fun List<ExerciseSession>.usable(): List<ExerciseSession> = filter { session ->
        session.hasWorkingSets && !session.isDeload
    }

    private fun progressionReason(lastSet: ExerciseSet, target: SetTarget): SuggestionReason {
        val reps = lastSet.reps ?: 0
        val repsAtTarget = lastSet.repsAtTargetEffort(target)
        return when {
            repsAtTarget >= target.maxReps ->
                SuggestionReason.LoadUp(
                    previousLoad = lastSet.totalWeight ?: 0.0,
                    easierThanAsked = reps < target.maxReps,
                )
            repsAtTarget < target.minReps ->
                SuggestionReason.BackOff(
                    achievedReps = reps,
                    minReps = target.minReps,
                    harderThanAsked = reps >= target.minReps,
                )
            else ->
                SuggestionReason.Hold(
                    achievedReps = reps,
                    maxReps = target.maxReps,
                    harderThanAsked = reps >= target.maxReps,
                )
        }
    }

    private fun loadFor(
        reason: SuggestionReason,
        lastLoad: Double,
        lastSet: ExerciseSet,
        sessions: List<ExerciseSession>,
        target: SetTarget,
        massUnit: MassUnit,
    ): Double =
        when (reason) {
            is SuggestionReason.LoadUp -> {
                val best = bestOneRepMax(sessions, massUnit)
                OneRepMaxCalculator.loadFor(best, target.minReps, target.rir ?: 0, massUnit)
            }
            is SuggestionReason.BackOff -> {
                val short = (target.minReps - lastSet.repsAtTargetEffort(target)).coerceAtLeast(1)
                lastLoad * (1.0 - BACK_OFF_PER_REP * short)
            }
            is SuggestionReason.Stalled ->
                if (reason.step == StallEscalation.DeloadExercise) {
                    lastLoad * (1.0 - DELOAD_FRACTION)
                } else {
                    lastLoad
                }
            is SuggestionReason.Hold,
            is SuggestionReason.Capped,
            is SuggestionReason.Deload -> lastLoad
        }

    /** The best the exercise has shown recently, which is what a load-up is measured against. */
    private fun bestOneRepMax(sessions: List<ExerciseSession>, massUnit: MassUnit): Double =
        sessions.take(SESSIONS_READ).flatMap(ExerciseSession::workingSets).maxOfOrNull { set ->
            val mass = set.totalWeight ?: return@maxOfOrNull 0.0
            val reps = set.reps ?: return@maxOfOrNull 0.0
            OneRepMaxCalculator.estimatedOneRepMax(mass, reps, set.rir ?: 0, massUnit)
        } ?: 0.0

    /**
     * Holds any single step to a tenth of what is on the bar, in either direction, and says so.
     *
     * A back-off of three percent a rep passes a tenth at four reps short, and a load-up off a
     * one-rep max that a single unusually good set inflated can ask for far more than that.
     */
    private fun capToTenth(
        load: Double,
        lastLoad: Double,
        reason: SuggestionReason,
    ): Pair<Double, SuggestionReason> {
        val limit = lastLoad * MAX_STEP_FRACTION
        if (abs(load - lastLoad) <= limit) return load to reason
        val capped = if (load > lastLoad) lastLoad + limit else lastLoad - limit
        return capped to SuggestionReason.Capped(uncapped = load, within = reason)
    }

    /**
     * How many sessions back the exercise has stood still at the target effort.
     *
     * Standing still is the operative word: a session that went *backwards* is not a stall but a
     * bad day, and the loop already has an answer for it in the back-off. Treating a regression as
     * a stall would escalate — questioning the effort, then deloading — over what one back-off
     * handles.
     */
    private fun stalledSessions(sessions: List<ExerciseSession>, target: SetTarget): Int {
        var flatTransitions = 0
        for (index in 0 until sessions.size - 1) {
            val current = sessions[index].firstWorkingSet ?: break
            val previous = sessions[index + 1].firstWorkingSet ?: break
            val reps = current.reps ?: 0
            val previousReps = previous.reps ?: 0
            val load = current.totalWeight ?: 0.0
            val previousLoad = previous.totalWeight ?: 0.0
            val flat = reps == previousReps && load == previousLoad
            if (!flat || !current.metEffort(target)) break
            flatTransitions++
        }
        // One flat transition is two sessions that read the same, which is the reference's stall.
        return if (flatTransitions >= 1) flatTransitions + 1 else 0
    }

    /** Cheapest first, and one step further for every session the stall has persisted. */
    private fun escalationFor(stalledSessions: Int): StallEscalation =
        StallEscalation.entries[
                (stalledSessions - MINIMUM_SESSIONS).coerceIn(0, StallEscalation.entries.lastIndex)]

    /** A set is at target effort when it was at least as hard as asked — a lower RIR is harder. */
    private fun ExerciseSet.metEffort(target: SetTarget): Boolean {
        val targetRir = target.rir ?: return true
        return (rir ?: targetRir) <= targetRir
    }

    /**
     * The reps the set would have reached at the target effort. Reps left in reserve beyond the
     * target count towards the range; reps ground out past it do not, because they cost effort the
     * next session is not asked to spend.
     */
    private fun ExerciseSet.repsAtTargetEffort(target: SetTarget): Int {
        val reps = reps ?: 0
        val rir = rir ?: return reps
        val targetRir = target.rir ?: return reps
        return reps + rir - targetRir
    }

    /** What the lifter did not put on the bar, and so cannot take off it. */
    private val ExerciseSet.bodyWeightOffset: Double
        get() = (this as? ExerciseSet.Calisthenics)?.bodyWeight ?: 0.0

    /**
     * [loadIncrement] is stored in kilograms, but a pound gym's equipment steps in round pounds,
     * not in converted kilograms. The step becomes the nearest multiple of 2.5 lb: 2.5 kg becomes 5
     * lb, 1.25 kg becomes 2.5 lb, and a 2 kg dumbbell jump becomes the 5 lb a pound rack offers.
     */
    private fun incrementIn(kilograms: Double, massUnit: MassUnit): Double =
        when (massUnit) {
            MassUnit.Kilograms -> kilograms
            MassUnit.Pounds ->
                (round(massUnit.fromKilograms(kilograms) / POUND_STEP) * POUND_STEP).coerceAtLeast(
                    POUND_STEP
                )
        }

    /**
     * Restates the loop's total-mass arithmetic in the terms the user sets: the bar for a loaded
     * lift, what is hung off it for a bodyweight one. Every weight a reason carries goes through
     * here too, so the sentence and the number beside it never measure different things.
     */
    private class BarTerms(private val offset: Double, private val increment: Double?) {

        fun suggestion(
            totalLoad: Double,
            lastLoad: Double,
            reason: SuggestionReason,
        ): LoadSuggestion? {
            val load = toBar(totalLoad)
            // A bodyweight lift with nothing hung off it has nothing to take off, and a load the
            // equipment's step rounds away entirely is no weight to put on a bar.
            if (totalLoad < lastLoad && lastLoad - offset <= 0.0) return null
            if (load <= 0.0 && offset == 0.0) return null
            val barReason = reason.inBarTerms()
            // Rounding can land the uncapped figure on the capped one, which leaves nothing to say.
            val honestReason =
                if (barReason is SuggestionReason.Capped && barReason.uncapped == load) {
                    barReason.within
                } else {
                    barReason
                }
            return LoadSuggestion(load = load, reason = honestReason)
        }

        private fun toBar(totalLoad: Double): Double =
            maxOf(0.0, OneRepMaxCalculator.roundToIncrement(totalLoad - offset, increment))

        private fun SuggestionReason.inBarTerms(): SuggestionReason =
            when (this) {
                is SuggestionReason.LoadUp -> copy(previousLoad = previousLoad - offset)
                is SuggestionReason.Deload -> copy(previousLoad = previousLoad - offset)
                is SuggestionReason.Capped ->
                    copy(uncapped = toBar(uncapped), within = within.inBarTerms())
                is SuggestionReason.Hold,
                is SuggestionReason.BackOff,
                is SuggestionReason.Stalled -> this
            }
    }

    companion object {
        /**
         * How many past sessions to hand the loop: enough to follow a stall to its last step with a
         * deload week among them.
         */
        const val HISTORY_LENGTH = 8

        /** One bad session is noise; the loop waits for a second before it says anything. */
        private const val MINIMUM_SESSIONS = 2

        /** How far back a load-up looks for the best the exercise has shown. */
        private const val SESSIONS_READ = 4

        private const val MAX_STEP_FRACTION = 0.10
        private const val BACK_OFF_PER_REP = 0.03
        private const val DELOAD_FRACTION = 0.10
        private const val POUND_STEP = 2.5
    }
}
