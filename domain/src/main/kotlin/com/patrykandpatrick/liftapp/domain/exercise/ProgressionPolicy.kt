package com.patrykandpatrick.liftapp.domain.exercise

/**
 * How an exercise is supposed to get harder week to week. One variable progresses at a time, so
 * this picks which one.
 *
 * Constant names are the persisted format; renaming one is a silent data break.
 */
enum class ProgressionPolicy {
    /**
     * Fill the rep range at the current load, then add one [Exercise.loadIncrement] and drop back
     * to the bottom of the range. The default for anything whose equipment step is small next to
     * its working load.
     */
    DoubleProgression,

    /**
     * Add one [Exercise.loadIncrement] whenever the rep target is met, without waiting for the top
     * of the range — the classic beginner linear progression. Nothing is seeded with it, because
     * how fast load can rise is a property of the lifter rather than of the exercise; it is here
     * for users who run that scheme.
     */
    FixedIncrement,

    /**
     * Never suggest a load change; progress reps, duration or distance instead. The default for
     * small-load work, where the smallest available jump is a large fraction of the load — 2 kg on
     * a 10 kg lateral raise is a 20% increase, a whole rep range's worth — and for everything that
     * carries no external load at all.
     */
    RepsOnly,
}
