package com.patrykandpatryk.liftapp.functionality.database.converter

import com.patrykandpatryk.liftapp.domain.di.DomainModule
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import org.junit.Test
import kotlin.test.assertContentEquals

class MuscleConverterTest {

    private val converter = JsonConverters(DomainModule.provideJson(emptySet()))

    @Test
    fun `Conversion of List of Muscles to String and back yields the same result`() {
        val input = listOf(Muscle.Abs, Muscle.Chest, Muscle.Glutes)
        val serializedInput = converter.toString(input)
        val deserializedInput = converter.toMuscles(serializedInput)
        assertContentEquals(input, deserializedInput)
    }
}
