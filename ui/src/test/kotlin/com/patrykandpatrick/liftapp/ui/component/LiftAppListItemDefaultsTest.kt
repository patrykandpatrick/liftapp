package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.ui.unit.dp
import kotlin.test.assertEquals
import org.junit.Test

class LiftAppListItemDefaultsTest {
    @Test
    fun `an ordinary inter-item gap uses the default spacing`() {
        assertEquals(
            2.dp,
            LiftAppListItemDefaults.gapAfter(
                position = LiftAppListItemPosition(index = 0, count = 2),
                selected = false,
                nextItemSelected = false,
            ),
        )
    }

    @Test
    fun `a gap beside either selected item uses double spacing`() {
        val position = LiftAppListItemPosition(index = 0, count = 2)

        assertEquals(
            4.dp,
            LiftAppListItemDefaults.gapAfter(
                position = position,
                selected = true,
                nextItemSelected = false,
            ),
        )
        assertEquals(
            4.dp,
            LiftAppListItemDefaults.gapAfter(
                position = position,
                selected = false,
                nextItemSelected = true,
            ),
        )
    }

    @Test
    fun `two selected neighbors negotiate one doubled gap`() {
        assertEquals(
            4.dp,
            LiftAppListItemDefaults.gapAfter(
                position = LiftAppListItemPosition(index = 0, count = 2),
                selected = true,
                nextItemSelected = true,
            ),
        )
    }

    @Test
    fun `the last item does not add spacing after its segment`() {
        assertEquals(
            0.dp,
            LiftAppListItemDefaults.gapAfter(
                position = LiftAppListItemPosition(index = 1, count = 2),
                selected = true,
                nextItemSelected = true,
            ),
        )
    }
}
