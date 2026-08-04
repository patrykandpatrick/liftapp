package com.patrykandpatrick.liftapp.navigation

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = Application::class, sdk = [36])
class BottomAppBarNavigatorAnimationTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `initial tab is stationary but later entrances bounce`() {
        val initialState = TabEntranceAnimationState(startAtEntrance = false)
        val animationState = TabEntranceAnimationState(startAtEntrance = true)
        assertTrue(initialState.offsetFraction.value == 0f)
        assertTrue(initialState.alpha.value == 1f)

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Box(Modifier.size(200.dp)) {
                TabEntrance(
                    isTarget = true,
                    animate = true,
                    providedAnimationState = animationState,
                ) {}
            }
        }

        assertTrue(animationState.offsetFraction.value == 1f)
        assertTrue(animationState.alpha.value == 0f)

        composeRule.mainClock.advanceTimeBy(2_000)
        assertTrue(animationState.offsetFraction.value.absoluteValue < 0.01f)
        assertTrue(animationState.alpha.value == 1f)
    }

    @Test
    fun `returning to an outgoing tab restarts its bounce`() {
        var isTarget by mutableStateOf(true)
        var animate by mutableStateOf(false)
        val animationState = TabEntranceAnimationState(startAtEntrance = false)
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            Box(Modifier.size(200.dp)) {
                TabEntrance(
                    isTarget = isTarget,
                    animate = animate,
                    providedAnimationState = animationState,
                ) {}
            }
        }

        composeRule.runOnIdle { animate = true }
        composeRule.mainClock.advanceTimeBy(100)
        composeRule.runOnIdle { isTarget = false }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.runOnIdle { isTarget = true }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()

        assertTrue(animationState.offsetFraction.value == 1f)
        assertTrue(animationState.alpha.value == 0f)
    }
}
