package com.patrykandpatryk.liftapp.functionality.database.converter

import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.di.DomainModule
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.functionality.database.string.ExerciseStringResource
import org.junit.Test
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class JsonConvertersTest {

    private val converter = JsonConverters(DomainModule.provideJson(emptySet()))

    @Test
    fun `Conversion of List of Muscles to String and back yields the same result`() {
        val input = listOf(Muscle.Abs, Muscle.Chest, Muscle.Glutes)
        val serializedInput = converter.toString(input)
        val deserializedInput = converter.toMuscles(serializedInput)
        assertContentEquals(input, deserializedInput)
    }

    @Test
    fun `Conversion of Name#Raw to String and back yields the same result`() {
        val input = Name.Raw("A raw name")
        val serializedInput = converter.toString(input)
        val deserializedInput = converter.toName(serializedInput)
        assertEquals(input, deserializedInput)
    }

    @Test
    fun `Conversion of Name#Resource to String and back yields the same result`() {
        val input = Name.Resource(ExerciseStringResource.DumbbellLunges)
        val serializedInput = converter.toString(input)
        val deserializedInput = converter.toName(serializedInput)
        assertEquals(input, deserializedInput)
    }

    @Test
    fun `Conversion of BodyMeasurementValue#Single to String and back yields the same result`() {
        val input = BodyMeasurementValue.SingleValue(Random.nextDouble(), MassUnit.Kilograms)
        val serializedInput = converter.toString(input)
        val deserializedInput = converter.toBodyValues(serializedInput)
        assertEquals(input, deserializedInput)
    }

    @Test
    fun `Conversion of BodyMeasurementValue#Double to String and back yields the same result`() {
        val input = BodyMeasurementValue.DoubleValue(Random.nextDouble(), Random.nextDouble(), MassUnit.Kilograms)
        val serializedInput = converter.toString(input)
        val deserializedInput = converter.toBodyValues(serializedInput)
        assertEquals(input, deserializedInput)
    }
}
