package com.patrykandpatryk.liftapp.unit

import com.patrykandpatry.liftapp.data.unit.UnitConverterImpl
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.testing.TestPreferenceRepository
import com.patrykandpatryk.liftapp.testing.TestStringProvider
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class UnitConverterTest {

    private val testedValues = arrayOf(
        1.0,
        4.2,
        9.99,
        69.0,
        123.45,
    )

    private val preferenceRepository = TestPreferenceRepository()

    private var converter: UnitConverter = UnitConverterImpl(
        formatter = Formatter(TestStringProvider,preferenceRepository),
        stringProvider = TestStringProvider,
        preferences = preferenceRepository,
    )

    @Test
    fun `convert kg to kg`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = MassUnit.Kilograms,
                to = MassUnit.Kilograms,
            )
        }
    }

    @Test
    fun `convert kg to lb`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = MassUnit.Kilograms,
                to = MassUnit.Pounds,
            )
        }
    }

    @Test
    fun `convert lb to lb`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = MassUnit.Pounds,
                to = MassUnit.Pounds,
            )
        }
    }

    @Test
    fun `convert lb to kg`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = MassUnit.Pounds,
                to = MassUnit.Kilograms,
            )
        }
    }

    @Test
    fun `convert km to km`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = LongDistanceUnit.Kilometer,
                to = LongDistanceUnit.Kilometer,
            )
        }
    }

    @Test
    fun `convert km to mi`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = LongDistanceUnit.Kilometer,
                to = LongDistanceUnit.Mile,
            )
        }
    }

    @Test
    fun `convert mi to mi`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = LongDistanceUnit.Mile,
                to = LongDistanceUnit.Mile,
            )
        }
    }

    @Test
    fun `convert mi to km`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = LongDistanceUnit.Mile,
                to = LongDistanceUnit.Kilometer,
            )
        }
    }

    private fun Double.convert(
        from: MassUnit,
        to: MassUnit,
    ) = runBlocking {
        preferenceRepository.massUnit.set(to)

        assertEquals(
            expected = when (to) {
                MassUnit.Kilograms -> from.toKilograms(this@convert)
                MassUnit.Pounds -> from.toPounds(this@convert)
            },
            actual = converter.convertToPreferredUnit(
                from = from,
                value = this@convert,
            ),
        )
    }

    private fun Double.convert(
        from: LongDistanceUnit,
        to: LongDistanceUnit,
    ) = runBlocking {
        preferenceRepository.longDistanceUnit.set(to)

        assertEquals(
            expected = when (to) {
                LongDistanceUnit.Kilometer -> from.toKilometers(this@convert)
                LongDistanceUnit.Mile -> from.toMiles(this@convert)
            },
            actual = converter.convertToPreferredUnit(
                from = from,
                value = this@convert,
            ),
        )
    }
}
