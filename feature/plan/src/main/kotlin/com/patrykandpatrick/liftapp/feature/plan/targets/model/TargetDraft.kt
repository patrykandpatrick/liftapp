package com.patrykandpatrick.liftapp.feature.plan.targets.model

import com.patrykandpatrick.liftapp.domain.target.SetTarget

/**
 * One exercise's sets as the editor holds them between opening and saving.
 *
 * [selected] is the set an edit lands on, or null for every set at once. An edit to every set
 * changes only the field being edited, so levelling the reps leaves the sets' differing loads
 * alone.
 *
 * Lowering [count] hides sets rather than dropping them. A count is typed a digit at a time — from
 * 3 to 12 goes through 1 — and dropping would destroy every set it passed on the way.
 */
class TargetDraft
private constructor(
    private val stash: List<SetTarget>,
    val count: Int,
    val selected: Int?,
) {
    val sets: List<SetTarget>
        get() = stash.take(count)

    /** Lowering it hides sets; raising it brings them back, then copies the last one. */
    fun withCount(count: Int): TargetDraft {
        if (count < 1) return this
        val grown =
            if (count <= stash.size) stash else stash + List(count - stash.size) { stash.last() }
        return TargetDraft(grown, count, selected?.takeIf { it < count })
    }

    fun select(index: Int?): TargetDraft =
        TargetDraft(stash, count, index?.takeIf { it in 0 until count })

    /** Applied to the [selected] set, or to every set — the hidden ones too — when none is. */
    fun edit(transform: (SetTarget) -> SetTarget): TargetDraft =
        TargetDraft(
            stash =
                stash.mapIndexed { index, set ->
                    if (selected == null || selected == index) transform(set) else set
                },
            count = count,
            selected = selected,
        )

    /** The value the fields show: the selected set's, or the one every set shares. */
    fun <T> valueOf(field: (SetTarget) -> T): T? =
        if (selected != null) field(sets[selected]) else sets.map(field).distinct().singleOrNull()

    /** True when every set is selected and they disagree on [field], which no one value shows. */
    fun varies(field: (SetTarget) -> Any?): Boolean =
        selected == null && sets.map(field).distinct().size > 1

    /** What saving writes: the visible sets, none with a top of its range below the bottom. */
    fun toSets(): List<SetTarget> = sets.map { it.copy(maxReps = maxOf(it.maxReps, it.minReps)) }

    companion object {
        fun of(sets: List<SetTarget>): TargetDraft =
            TargetDraft(
                stash = sets.ifEmpty { listOf(SetTarget(minReps = 0, maxReps = 0)) },
                count = sets.size.coerceAtLeast(1),
                selected = null,
            )
    }
}
