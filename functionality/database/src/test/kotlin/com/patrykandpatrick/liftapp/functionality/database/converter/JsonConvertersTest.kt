package com.patrykandpatrick.liftapp.functionality.database.converter

import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.di.DomainModule
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.functionality.database.di.DatabaseModule
import com.patrykandpatrick.liftapp.functionality.database.string.ExerciseStringResource
import kotlin.random.Random
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class JsonConvertersTest {

    // The serializer set mirrors what `DatabaseModule` contributes via `@IntoSet` at runtime.
    // Passing `emptySet()` here left `Name.Resource` unserializable.
    private val converter =
        JsonConverters(
            DomainModule.provideJson(
                setOf(DatabaseModule.provideExerciseStringResourceSerializer())
            )
        )

    @Test
    fun `Conversion of List of Muscles to String and back yields the same result`() {
        val input = listOf(Muscle.Abs, Muscle.Chest, Muscle.Glutes)
        val serializedInput = converter.toString(input)
        val deserializedInput = converter.toMuscles(serializedInput)
        assertContentEquals(input, deserializedInput)
    }

    @Test
    fun `Muscles are stored under their constant names`() {
        assertEquals(
            """["Chest","FrontDelts"]""",
            converter.toString(listOf(Muscle.Chest, Muscle.FrontDelts)),
        )
    }

    // A `.lfa` archive written before the deltoid was split replays raw column text back into the
    // table at any later date, so this is the only thing standing between an old backup and a
    // crash on restore.
    @Test
    fun `A muscle list written before the deltoid was split reads as the front head`() {
        assertContentEquals(
            listOf(Muscle.Chest, Muscle.FrontDelts),
            converter.toMuscles("""["Chest","Shoulders"]"""),
        )
    }

    @Test
    fun `A muscle this version does not know is dropped rather than failing the whole list`() {
        assertContentEquals(
            listOf(Muscle.Chest, Muscle.Triceps),
            converter.toMuscles("""["Chest","Sternocleidomastoid","Triceps"]"""),
        )
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
        val input =
            BodyMeasurementValue.DoubleValue(
                Random.nextDouble(),
                Random.nextDouble(),
                MassUnit.Kilograms,
            )
        val serializedInput = converter.toString(input)
        val deserializedInput = converter.toBodyValues(serializedInput)
        assertEquals(input, deserializedInput)
    }
}
