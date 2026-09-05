package com.patrykandpatrick.liftapp.domain.progression

import com.patrykandpatrick.liftapp.domain.exercise.ProgressionPolicy
import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.workout.ExerciseSet
import javax.inject.Inject
import kotlin.math.abs

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
 *   something to compare against.
 * - **Always a reason.** [LoadSuggestion] cannot be built without one.
 *
 * Returns null where there is nothing honest to say: an exercise that progresses reps rather than
 * load, or too little history to read.
 */
class SuggestLoadUseCase @Inject constructor() {

    /**
     * [history] is most recent first. [volumeProgressedThisCycle] holds the load steady when the
     * plan already added sets — one variable progresses at a time, and the plan moved first.
     */
    operator fun invoke(
        history: List<ExerciseSession>,
        target: SetTarget,
        policy: ProgressionPolicy,
        loadIncrement: Double?,
        massUnit: MassUnit,
        volumeProgressedThisCycle: Boolean = false,
    ): LoadSuggestion? {
        // Reps-only exercises never get a load suggestion: the smallest step their equipment
        // offers is a large fraction of what they carry.
        if (policy == ProgressionPolicy.RepsOnly) return null

        val sessions = history.filter(ExerciseSession::hasWorkingSets)
        if (sessions.size < MINIMUM_SESSIONS) return null

        val lastSet = sessions.first().firstWorkingSet ?: return null
        val lastLoad = lastSet.totalWeight ?: return null
        if (lastLoad <= 0.0) return null

        val stall = stalledSessions(sessions, target)
        val reason =
            when {
                stall >= MINIMUM_SESSIONS -> SuggestionReason.Stalled(escalationFor(stall), stall)
                volumeProgressedThisCycle -> SuggestionReason.VolumeProgressedInstead
                else -> progressionReason(lastSet, target)
            }

        val raw = loadFor(reason, lastLoad, lastSet, sessions, target, massUnit)
        val capped = capToTenth(raw, lastLoad, reason)

        // The bar is what the user sets; a bodyweight lift records only what is hung off it.
        val offset = lastSet.bodyWeightOffset
        return LoadSuggestion(
            load = OneRepMaxCalculator.roundToIncrement(capped.first - offset, loadIncrement),
            reason = capped.second,
        )
    }

    private fun progressionReason(lastSet: ExerciseSet, target: SetTarget): SuggestionReason {
        val reps = lastSet.reps ?: 0
        return when {
            reps >= target.maxReps && lastSet.metEffort(target) ->
                SuggestionReason.LoadUp(previousLoad = lastSet.totalWeight ?: 0.0)
            reps < target.minReps || !lastSet.metEffort(target) ->
                SuggestionReason.BackOff(achievedReps = reps, minReps = target.minReps)
            else -> SuggestionReason.Hold(achievedReps = reps, maxReps = target.maxReps)
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
                val short = (target.minReps - (lastSet.reps ?: 0)).coerceAtLeast(1)
                lastLoad * (1.0 - BACK_OFF_PER_REP * short)
            }
            is SuggestionReason.Stalled ->
                if (reason.step == StallEscalation.DeloadExercise) {
                    lastLoad * (1.0 - DELOAD_FRACTION)
                } else {
                    lastLoad
                }
            is SuggestionReason.Hold,
            SuggestionReason.VolumeProgressedInstead,
            is SuggestionReason.Capped -> lastLoad
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

    /** What the lifter did not put on the bar, and so cannot take off it. */
    private val ExerciseSet.bodyWeightOffset: Double
        get() = (this as? ExerciseSet.Calisthenics)?.bodyWeight ?: 0.0

    private companion object {
        /** One bad session is noise; the loop waits for a second before it says anything. */
        const val MINIMUM_SESSIONS = 2

        /** How far back a load-up looks for the best the exercise has shown. */
        const val SESSIONS_READ = 4

        const val MAX_STEP_FRACTION = 0.10
        const val BACK_OFF_PER_REP = 0.03
        const val DELOAD_FRACTION = 0.10
    }
}
