package com.patrykandpatrick.liftapp.domain.muscle

/**
 * Where a muscle's weekly set count falls against the volume landmarks in
 * `docs/workout-programming.md`.
 *
 * A band rather than a number because the input does not justify one: fractional credits nobody
 * measured, read against population figures whose individual variation is wider than the gaps
 * between them.
 */
enum class VolumeBand {
    BelowMaintenance,

    /** Minimum effective volume is a range of roughly 8 to 10 sets, so this band contains it. */
    Maintenance,
    Adaptive,
    Maximum;

    companion object {
        const val MAINTENANCE_VOLUME = 6.0

        /**
         * The top of the 8–10 set minimum-effective range, so a muscle reads as productive only
         * once it clearly is. Also ACSM's summary figure for hypertrophy.
         */
        const val ADAPTIVE_VOLUME = 10.0

        const val RECOVERABLE_VOLUME = 20.0

        fun of(sets: Double): VolumeBand =
            when {
                sets < MAINTENANCE_VOLUME -> BelowMaintenance
                sets < ADAPTIVE_VOLUME -> Maintenance
                sets < RECOVERABLE_VOLUME -> Adaptive
                else -> Maximum
            }
    }
}
