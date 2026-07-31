package com.patrykandpatrick.liftapp.feature.onerepmax

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatrick.liftapp.domain.exerciseset.OneRepMaxCalculator
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

class OneRepMaxStateTest {

    private val testScheduler = TestCoroutineScheduler()

    private val formatter = Formatter(TestStringProvider, MutableStateFlow(true))

    private val coroutineContext = UnconfinedTestDispatcher(testScheduler)

    private val coroutineScope = CoroutineScope(coroutineContext)

    private val sut =
        OneRepMaxState(
            getMassUnit = { flowOf(MassUnit.Kilograms) },
            savedStateHandle = SavedStateHandle(),
            formatWeight = formatter::formatWeight,
            coroutineScope = coroutineScope,
        )

    @Test
    fun `Given just mass is updated, 1RM is not calculated`() {
        sut.updateMass("100")
        assertEquals(formatter.formatWeight(0.0, MassUnit.Kilograms), sut.oneRepMax.value)
    }

    @Test
    fun `Given just reps is updated, 1RM is not calculated`() {
        sut.updateReps("5")
        assertEquals(formatter.formatWeight(0.0, MassUnit.Kilograms), sut.oneRepMax.value)
    }

    @Test
    fun `Given both mass and reps are updated, 1RM is calculated`() {
        val mass = 100.0
        val reps = 5
        sut.updateMass(mass.toString())
        sut.updateReps(reps.toString())
        assertEquals(
            formatter.formatWeight(
                OneRepMaxCalculator.getOneRepMax(mass, reps),
                MassUnit.Kilograms,
            ),
            sut.oneRepMax.value,
        )
    }

    @Test
    fun `Given mass is updated to invalid value, 1RM is not calculated`() {
        sut.updateMass("1,4.")
        assertEquals("", sut.mass.value)
    }

    @Test
    fun `Given reps is updated to invalid value, 1RM is not calculated`() {
        sut.updateReps("1.4")
        assertEquals("", sut.reps.value)
    }

    @Test
    fun `Given 1RM is calculated in quick succession, only one entry is added to history`() =
        runTest(coroutineContext) {
            sut.updateMass("100")
            sut.updateReps("5")
            sut.updateMass("97.5")
            sut.updateReps("6")
            advanceUntilIdle()
            assertEquals(1, sut.history.value.size)
        }

    @Test
    fun `Given another calculation is saved, the newest entry is first`() =
        runTest(coroutineContext) {
            sut.updateMass("100")
            sut.updateReps("5")
            advanceUntilIdle()

            sut.updateMass("80")
            advanceUntilIdle()

            assertEquals(
                formatter.formatWeight(80.0, MassUnit.Kilograms),
                sut.history.value[0].mass,
            )
            assertEquals(
                formatter.formatWeight(100.0, MassUnit.Kilograms),
                sut.history.value[1].mass,
            )
        }

    @Test
    fun `Given history is cleared, the history is empty`() =
        runTest(coroutineContext) {
            sut.updateMass("100")
            sut.updateReps("5")
            advanceUntilIdle()
            assertTrue(sut.history.value.isNotEmpty())
            sut.clearHistory()
            assertTrue(sut.history.value.isEmpty())
        }
}
