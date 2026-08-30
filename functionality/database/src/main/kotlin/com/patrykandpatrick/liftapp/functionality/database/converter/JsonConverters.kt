package com.patrykandpatrick.liftapp.functionality.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.model.Name
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.domain.target.SetTarget
import com.patrykandpatrick.liftapp.domain.unit.LongDistanceUnit
import com.patrykandpatrick.liftapp.domain.unit.MassUnit
import com.patrykandpatrick.liftapp.domain.unit.ValueUnit
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class JsonConverters @Inject constructor(private val json: Json) {

    @TypeConverter fun toString(name: Name): String = json.encodeToString(name)

    @TypeConverter fun toName(string: String): Name = json.decodeFromString(string)

    @TypeConverter
    fun toString(bodyMeasurementValue: BodyMeasurementValue): String =
        json.encodeToString(bodyMeasurementValue)

    @TypeConverter
    fun toBodyValues(string: String): BodyMeasurementValue = json.decodeFromString(string)

    // The one place the muscle columns are (de)serialized, and so the one place that has to survive
    // a name this version no longer knows. See MuscleListSerializer.
    @TypeConverter
    fun toString(muscles: List<Muscle>): String = json.encodeToString(MuscleListSerializer, muscles)

    @TypeConverter
    fun toMuscles(string: String): List<Muscle> =
        json.decodeFromString(MuscleListSerializer, string)

    // Named rather than overloaded: List<SetTarget> and List<Muscle> share a JVM signature.
    @TypeConverter fun setTargetsToString(sets: List<SetTarget>): String = json.encodeToString(sets)

    @TypeConverter fun toSetTargets(string: String): List<SetTarget> = json.decodeFromString(string)

    @TypeConverter fun toString(valueUnit: ValueUnit): String = json.encodeToString(valueUnit)

    @TypeConverter fun toValueUnit(string: String): ValueUnit = json.decodeFromString(string)

    @TypeConverter fun toString(massUnit: MassUnit): String = json.encodeToString(massUnit)

    @TypeConverter fun toMassUnit(string: String): MassUnit = json.decodeFromString(string)

    @TypeConverter
    fun toString(longDistanceUnit: LongDistanceUnit): String = json.encodeToString(longDistanceUnit)

    @TypeConverter
    fun toLongDistanceUnit(string: String): LongDistanceUnit = json.decodeFromString(string)
}
