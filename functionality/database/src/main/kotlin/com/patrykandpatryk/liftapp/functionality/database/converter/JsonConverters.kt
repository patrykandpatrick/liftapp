package com.patrykandpatryk.liftapp.functionality.database.converter

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.goal.Goal
import com.patrykandpatryk.liftapp.domain.model.Name
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import javax.inject.Inject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class JsonConverters @Inject constructor(
    private val json: Json,
) {

    @TypeConverter
    fun toString(name: Name): String = json.encodeToString(name)

    @TypeConverter
    fun toName(string: String): Name = json.decodeFromString(string)

    @TypeConverter
    fun toString(bodyMeasurementValue: BodyMeasurementValue): String = json.encodeToString(bodyMeasurementValue)

    @TypeConverter
    fun toBodyValues(string: String): BodyMeasurementValue = json.decodeFromString(string)

    @TypeConverter
    fun toString(muscles: List<Muscle>): String = json.encodeToString(muscles)

    @TypeConverter
    fun toMuscles(string: String): List<Muscle> = json.decodeFromString(string)

    @TypeConverter
    fun toString(goal: Goal): String = json.encodeToString(goal)

    @TypeConverter
    fun toGoal(string: String): Goal = json.decodeFromString(string)
}
