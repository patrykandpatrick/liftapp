package com.patrykandpatrick.liftapp.newbodymeasurement

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.Constants.Database.ID_NOT_SET
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementEntryUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementWithLatestEntryUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.UpsertBodyMeasurementUseCase
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.unit.GetUnitForBodyMeasurementTypeUseCase
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.navigation.data.NewBodyMeasurementRouteData
import com.patrykandpatrick.liftapp.newbodymeasuremententry.model.Action
import com.patrykandpatrick.liftapp.newbodymeasuremententry.model.NewBodyMeasurementState
import com.patrykandpatrick.liftapp.newbodymeasuremententry.ui.NewBodyMeasurementEntryViewModel
import com.patrykandpatrick.liftapp.testing.TestPreferenceRepository
import com.patrykandpatrick.liftapp.testing.TestStringProvider
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NewBodyMeasurementStateTest {

    private val testScheduler = TestCoroutineScheduler()

    private val formatter = Formatter(TestStringProvider, MutableStateFlow(true))

    private val savedStateHandle = SavedStateHandle()

    private val textFieldStateManager =
        TextFieldStateManager(
            stringProvider = TestStringProvider,
            formatter = formatter,
            savedStateHandle = savedStateHandle,
        )

    private val preferenceRepository = TestPreferenceRepository()

    private val unitConverter = UnitConverter(formatter, TestStringProvider, preferenceRepository)

    private val weightMeasurement =
        BodyMeasurementWithLatestEntry(
            id = 1,
            name = "Weight",
            type = BodyMeasurementType.Weight,
            latestEntry = null,
        )

    private fun getLatestEntry() =
        BodyMeasurementEntry(
            id = 1,
            value = BodyMeasurementValue.SingleValue(75.0, MassUnit.Kilograms),
            localDateTime = LocalDateTime.now(),
        )

    private fun getSut(
        bodyMeasurementWithLatestEntry: BodyMeasurementWithLatestEntry = weightMeasurement,
        bodyMeasurementEntry: BodyMeasurementEntry? = null,
        upsertBodyMeasurementEntry: UpsertBodyMeasurementUseCase =
            UpsertBodyMeasurementUseCase { _, _, _, _ ->
            },
    ): NewBodyMeasurementEntryViewModel =
        NewBodyMeasurementEntryViewModel(
            routeData =
                NewBodyMeasurementRouteData(
                    bodyMeasurementWithLatestEntry.id,
                    bodyMeasurementEntry?.id ?: ID_NOT_SET,
                ),
            textFieldStateManager = textFieldStateManager,
            getUnitForBodyMeasurementType =
                GetUnitForBodyMeasurementTypeUseCase(preferenceRepository),
            is24H = flowOf(true),
            formatter = formatter,
            upsertBodyMeasurementUseCase = upsertBodyMeasurementEntry,
            navigationCommander = NavigationCommander(),
            unitConverter = unitConverter,
            getBodyMeasurementWithLatestEntryUseCase =
                GetBodyMeasurementWithLatestEntryUseCase { flowOf(bodyMeasurementWithLatestEntry) },
            getBodyMeasurementEntryUseCase =
                GetBodyMeasurementEntryUseCase { flowOf(bodyMeasurementEntry) },
            stringProvider = TestStringProvider,
        )

    private suspend fun NewBodyMeasurementEntryViewModel.getSuccessState():
        NewBodyMeasurementState =
        state.filterIsInstance<Loadable.Success<NewBodyMeasurementState>>().first().data

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Name field has the name of the body measurement`() = runTest {
        val sut = getSut().getSuccessState()
        assertEquals(sut.name, weightMeasurement.name)
    }

    @Test
    fun `Given latestEntry is null inputData holds 0 value`() = runTest {
        val sut = getSut()
        val inputData = sut.getSuccessState().inputData
        assertIs<NewBodyMeasurementState.InputData.SingleValue>(inputData)
        assertEquals(0.0, inputData.textFieldState.value)
    }

    @Test
    fun `Given latestEntry is not null inputData holds the value of the latest entry`() = runTest {
        val latestEntry = getLatestEntry()
        val sut =
            getSut(
                bodyMeasurementWithLatestEntry = weightMeasurement.copy(latestEntry = latestEntry)
            )
        val inputData = sut.getSuccessState().inputData
        assertIs<NewBodyMeasurementState.InputData.SingleValue>(inputData)
        assertEquals(latestEntry.value, inputData.toBodyMeasurementValue())
    }

    @Test
    fun `Given the measurement is loaded inputData is not null and has invalid data`() = runTest {
        val sut =
            getSut(
                upsertBodyMeasurementEntry = { _, _, _, _ ->
                    error("BodyMeasurementEntry mustn't be saved.")
                }
            )
        val state = sut.getSuccessState()
        val inputData = state.inputData
        assertNotNull(inputData)
        assertTrue(inputData.isInvalid())
        sut.onAction(Action.Save(state))
    }

    @Test
    fun `Given inputData is valid entry is saved`() = runTest {
        var savedValue: BodyMeasurementValue? = null
        val sut = getSut(upsertBodyMeasurementEntry = { _, value, _, _ -> savedValue = value })
        val state = sut.getSuccessState()
        val inputData = state.inputData
        assertIs<NewBodyMeasurementState.InputData.SingleValue>(inputData)
        inputData.textFieldState.updateText("75")
        assertFalse(inputData.isInvalid())
        sut.onAction(Action.Save(state))
        assertNotNull(savedValue)
        assertEquals(inputData.toBodyMeasurementValue(), savedValue)
    }
}
