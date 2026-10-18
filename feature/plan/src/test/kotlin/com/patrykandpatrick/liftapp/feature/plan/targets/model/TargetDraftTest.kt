package com.patrykandpatrick.liftapp.feature.plan.targets.model

import com.patrykandpatrick.liftapp.domain.target.SetTarget
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.junit.Test

class TargetDraftTest {

    private val ramp =
        listOf(
            SetTarget(minReps = 5, maxReps = 5, rir = 2, loadPercent = 70.0),
            SetTarget(minReps = 5, maxReps = 5, rir = 2, loadPercent = 75.0),
            SetTarget(minReps = 5, maxReps = 5, rir = 1, loadPercent = 80.0),
        )

    @Test
    fun `An edit to one set leaves the others alone`() {
        val draft = TargetDraft.of(ramp).select(1).edit { it.copy(minReps = 3, maxReps = 3) }

        assertEquals(listOf(5, 3, 5), draft.sets.map(SetTarget::minReps))
    }

    @Test
    fun `An edit to every set levels only the field edited`() {
        val draft = TargetDraft.of(ramp).edit { it.copy(minReps = 3) }

        assertEquals(listOf(3, 3, 3), draft.sets.map(SetTarget::minReps))
        assertEquals(listOf(70.0, 75.0, 80.0), draft.sets.map(SetTarget::loadPercent))
    }

    @Test
    fun `Every set shows the value they share, and none where they differ`() {
        val draft = TargetDraft.of(ramp)

        assertEquals(5, draft.valueOf(SetTarget::minReps))
        assertNull(draft.valueOf(SetTarget::loadPercent))
        assertTrue(draft.varies(SetTarget::loadPercent))
        assertFalse(draft.varies(SetTarget::minReps))
    }

    @Test
    fun `One selected set shows its own value and never varies`() {
        val draft = TargetDraft.of(ramp).select(2)

        assertEquals(80.0, draft.valueOf(SetTarget::loadPercent))
        assertFalse(draft.varies(SetTarget::loadPercent))
    }

    @Test
    fun `Typing a count through a smaller one does not lose the sets it passes`() {
        // From 3 to 12 a digit at a time goes through 1.
        val draft = TargetDraft.of(ramp).withCount(1).withCount(12)

        assertEquals(ramp, draft.sets.take(3))
        assertEquals(List(9) { ramp.last() }, draft.sets.drop(3))
    }

    @Test
    fun `A count that drops the selected set selects them all`() {
        val draft = TargetDraft.of(ramp).select(2).withCount(2)

        assertNull(draft.selected)
        assertEquals(2, draft.sets.size)
    }

    @Test
    fun `No count below one is taken`() {
        assertEquals(3, TargetDraft.of(ramp).withCount(0).count)
    }

    @Test
    fun `An edit to every set reaches the hidden ones too`() {
        val draft = TargetDraft.of(ramp).withCount(1).edit { it.copy(rir = 3) }.withCount(3)

        assertEquals(listOf(3, 3, 3), draft.sets.map(SetTarget::rir))
    }

    @Test
    fun `Saving never writes a range whose top is below its bottom`() {
        val sets = TargetDraft.of(ramp).edit { it.copy(minReps = 8) }.toSets()

        assertTrue(sets.all { it.maxReps >= it.minReps })
    }
}
