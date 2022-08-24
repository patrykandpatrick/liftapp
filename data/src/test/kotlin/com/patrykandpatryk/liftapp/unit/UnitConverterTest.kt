package com.patrykandpatryk.liftapp.unit

import com.patrykandpatry.liftapp.data.unit.UnitConverterImpl
import com.patrykandpatryk.liftapp.domain.repository.PreferenceRepository
import com.patrykandpatryk.liftapp.domain.unit.DistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykmichalik.opto.domain.Preference
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class UnitConverterTest {

    private val testedValues = arrayOf(
        1f,
        4.2f,
        9.99f,
        69f,
        123.45f
    )

    @MockK
    lateinit var preferenceRepository: PreferenceRepository

    private var converter: UnitConverter

    init {
        MockKAnnotations.init(this)
        converter = UnitConverterImpl(preferenceRepository)
    }

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
                from = DistanceUnit.Kilometers,
                to = DistanceUnit.Kilometers,
            )
        }
    }

    @Test
    fun `convert km to mi`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = DistanceUnit.Kilometers,
                to = DistanceUnit.Miles,
            )
        }
    }

    @Test
    fun `convert mi to mi`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = DistanceUnit.Miles,
                to = DistanceUnit.Miles,
            )
        }
    }

    @Test
    fun `convert mi to km`() = runBlocking {
        testedValues.forEach { testedValue ->
            testedValue.convert(
                from = DistanceUnit.Miles,
                to = DistanceUnit.Kilometers,
            )
        }
    }

    private fun Float.convert(
        from: MassUnit,
        to: MassUnit,
    ) = runBlocking {
        mockPreferredMassUnit(to)

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

    private fun mockPreferredMassUnit(massUnit: MassUnit) {
        every {
            preferenceRepository.massUnit
        } returns getTestPreference { flowOf(massUnit) }
    }

    private fun Float.convert(
        from: DistanceUnit,
        to: DistanceUnit,
    ) = runBlocking {
        mockPreferredDistanceUnit(to)

        assertEquals(
            expected = when (to) {
                DistanceUnit.Kilometers -> from.toKilometers(this@convert)
                DistanceUnit.Miles -> from.toMiles(this@convert)
            },
            actual = converter.convertToPreferredUnit(
                from = from,
                value = this@convert,
            ),
        )
    }

    private fun mockPreferredDistanceUnit(distanceUnit: DistanceUnit) {
        every {
            preferenceRepository.distanceUnit
        } returns getTestPreference { flowOf(distanceUnit) }
    }

    private fun <T> getTestPreference(
        get: () -> Flow<T>,
    ): Preference<T, String, *> = object : Preference<T, String, Any> {

        override val defaultValue: T
            get() = TODO("Not yet implemented")

        override val key: Unit
            get() = TODO("Not yet implemented")

        override fun get(): Flow<T> = get()

        override suspend fun set(value: T) {
            TODO("Not yet implemented")
        }

        override suspend fun update(block: (T) -> T) {
            TODO("Not yet implemented")
        }
    }
}