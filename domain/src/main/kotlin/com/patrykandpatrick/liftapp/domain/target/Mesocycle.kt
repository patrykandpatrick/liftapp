package com.patrykandpatrick.liftapp.domain.target

/**
 * How a plan's cycles divide into blocks of accumulation ending in a deload.
 *
 * [length] is the whole block including its deload, so a length of five is four weeks of
 * accumulation and a fifth of reduced work — the shape the reference reports surveyed lifters
 * actually running, about a week off every four to six.
 *
 * Null [length] is a plan with no deloads at all, which is one long block. That is the honest
 * default: a lifter running four weeks total has nothing to deload from.
 */
@JvmInline
value class Mesocycle(val length: Int?) {

    /** The last cycle of each block, and only that one. */
    fun isDeload(cycleIndex: Int): Boolean =
        length != null && length > 0 && (cycleIndex + 1) % length == 0

    /**
     * Where a cycle sits in its own block, and how long the accumulating part of that block is.
     *
     * The ramps are read against this rather than against the whole plan, so a second block starts
     * over at the top of the RIR range instead of continuing down from where the first ended. A
     * plan that ramped straight through would prescribe its tenth week harder than its fifth for no
     * reason other than arithmetic.
     */
    fun accumulationPosition(cycleIndex: Int, cycleCount: Int): AccumulationPosition {
        val blockLength = length?.takeIf { it > 0 } ?: cycleCount
        // The accumulating weeks are the block less its deload; a block of one is all deload.
        val accumulatingLength = if (length != null) (blockLength - 1) else blockLength
        return AccumulationPosition(
            index = cycleIndex % blockLength,
            length = accumulatingLength.coerceAtLeast(1),
        )
    }

    companion object {
        /** No deloads: the plan is one block and the ramps run its whole length. */
        val Continuous = Mesocycle(null)

        /** The reference's midpoint — four accumulating weeks and a fifth of reduced work. */
        const val DEFAULT_LENGTH = 5
    }
}

/** A cycle's place among the accumulating weeks of its block, which is what the ramps read. */
data class AccumulationPosition(val index: Int, val length: Int) {
    /** Zero on the first accumulating week, one on the last, whatever the block's length. */
    val progress: Double
        get() = if (length <= 1) 0.0 else (index.toDouble() / (length - 1)).coerceIn(0.0, 1.0)
}
