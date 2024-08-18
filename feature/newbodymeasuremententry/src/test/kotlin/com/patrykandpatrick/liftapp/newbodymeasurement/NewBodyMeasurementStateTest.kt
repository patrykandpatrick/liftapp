package com.patrykandpatrick.liftapp.newbodymeasurement

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.newbodymeasuremententry.ui.NewBodyMeasurementState
import com.patrykandpatryk.liftapp.testing.TestStringProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class NewBodyMeasurementStateTest {

    private val testScheduler = TestCoroutineScheduler()

    private val formatter = Formatter(TestStringProvider, flowOf(true))

    private val savedStateHandle = SavedStateHandle()

    private val weightMeasurement = BodyMeasurementWithLatestEntry(
        id = 1,
        name = "Weight",
        type = BodyMeasurementType.Weight,
        latestEntry = null,
    )

    private val latestEntry = BodyMeasurementEntry(
        id = 1,
        value = BodyMeasurementValue.Single(75f, MassUnit.Kilograms),
        formattedDate = formatter.getFormattedDate(LocalDateTime.now()),
    )

    private val coroutineScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))

    private fun getSut(
        bodyMeasurementWithLatestEntry: BodyMeasurementWithLatestEntry = weightMeasurement,
        bodyMeasurementEntry: BodyMeasurementEntry? = null,
        upsertBodyMeasurementEntry: suspend (value: BodyMeasurementValue, time: LocalDateTime) -> Unit = { _, _ -> },
        coroutineScope: CoroutineScope = this.coroutineScope,
    ): NewBodyMeasurementState =
        NewBodyMeasurementState(
            getFormattedDate = formatter::getFormattedDate,
            getBodyMeasurementWithLatestEntry = { bodyMeasurementWithLatestEntry },
            getBodyMeasurementEntry = { bodyMeasurementEntry },
            upsertBodyMeasurementEntry = upsertBodyMeasurementEntry,
            textFieldStateManager = TextFieldStateManager(TestStringProvider, formatter, savedStateHandle),
            getUnitForBodyMeasurementType = { MassUnit.Kilograms },
            coroutineScope = coroutineScope,
            savedStateHandle = savedStateHandle,
        )

    @Test
    fun `Name field has the name of the body measurement`() {
        val sut = getSut()
        assertEquals(sut.name.value, weightMeasurement.name)
    }

    @Test
    fun `Given latestEntry is null inputData holds 0 value`() {
        val sut = getSut()
        val inputData = sut.inputData.value
        assertIs<NewBodyMeasurementState.InputData.Single>(inputData)
        assertEquals(0f, inputData.textFieldState.value)
    }

    @Test
    fun `Given latestEntry is not null inputData holds the value of the latest entry`() {
        val sut = getSut(bodyMeasurementWithLatestEntry = weightMeasurement.copy(latestEntry = latestEntry))
        val inputData = sut.inputData.value
        assertIs<NewBodyMeasurementState.InputData.Single>(inputData)
        assertEquals(latestEntry.value, inputData.toBodyMeasurementValue())
    }

    @Test
    fun `Given the time is changed, the dateTime is updated`() = runTest {
        val sut = getSut()
        val now = LocalDateTime.now()
        val hour = now.hour + 1
        val minute = now.minute + 1
        sut.setTime(hour, minute)
        val dateTime = sut.getDateTime()
        assertEquals(hour, dateTime.hour)
        assertEquals(minute, dateTime.minute)
    }

    @Test
    fun `Given the date is changed, the dateTime is updated`() = runTest {
        val sut = getSut()
        val now = LocalDateTime.now()
        val year = now.year + 1
        val month = now.monthValue + 1
        val day = now.dayOfMonth + 1
        sut.setDate(year, month, day)
        val dateTime = sut.getDateTime()
        assertEquals(year, dateTime.year)
        assertEquals(month, dateTime.monthValue)
        assertEquals(day, dateTime.dayOfMonth)
    }

    @Test
    fun `Given the measurement is loaded inputData is not null and has invalid data`() {
        val sut = getSut(upsertBodyMeasurementEntry = { _, _ -> error("BodyMeasurementEntry mustn't be saved.") })
        val inputData = sut.inputData.value
        assertNotNull(inputData)
        assertTrue(inputData.isInvalid())
        sut.save(inputData)
        assertFalse(sut.entrySaved.value)
    }

    @Test
    fun `Given inputData is valid entry is saved`() = runTest {
        var savedValue: BodyMeasurementValue? = null
        val sut = getSut(upsertBodyMeasurementEntry = { value, _ -> savedValue = value })
        val inputData = sut.inputData.value
        assertIs<NewBodyMeasurementState.InputData.Single>(inputData)
        inputData.textFieldState.updateText("75")
        assertFalse(inputData.isInvalid())
        sut.save(inputData)
        assertTrue(sut.entrySaved.value)
        assertNotNull(savedValue)
        assertEquals(inputData.toBodyMeasurementValue(), savedValue)
    }
}
