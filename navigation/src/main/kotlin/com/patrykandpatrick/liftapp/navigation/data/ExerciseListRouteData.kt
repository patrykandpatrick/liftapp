package com.patrykandpatrick.liftapp.navigation.data

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlin.reflect.typeOf
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.Json

/**
 * Updates must be reflected in
 * [com.patrykandpatrick.liftapp.navigation.serialization.ExercisesSerializer].
 */
@Serializable
open class ExerciseListRouteData
internal constructor(val mode: Mode, val disabledExerciseIDs: List<Long>?) {
    @Serializable(ModeSerializer::class)
    sealed class Mode {
        data class Pick(val resultKey: String) : Mode()

        data object View : Mode()
    }

    object ModeSerializer : KSerializer<Mode> {
        override val descriptor: SerialDescriptor =
            buildClassSerialDescriptor(serialName = checkNotNull(Mode::class.qualifiedName)) {
                element(elementName = "type", descriptor = String.serializer().descriptor)
                element(
                    elementName = "resultKey",
                    descriptor = String.serializer().descriptor,
                    isOptional = true,
                )
            }

        override fun deserialize(decoder: Decoder): Mode =
            decoder.decodeStructure(descriptor) {
                var type: String? = null
                var resultKey: String? = null
                loop@ while (true) {
                    when (decodeElementIndex(descriptor)) {
                        0 -> type = decodeStringElement(descriptor, 0)
                        1 -> resultKey = decodeStringElement(descriptor, 1)
                        else -> break@loop
                    }
                }
                when (type) {
                    Mode.Pick::class.simpleName -> Mode.Pick(checkNotNull(resultKey))
                    Mode.View::class.simpleName -> Mode.View
                    else -> error("Unknown type: $type")
                }
            }

        override fun serialize(encoder: Encoder, value: Mode) {
            encoder.beginStructure(descriptor).apply {
                encodeStringElement(descriptor, 0, checkNotNull(value::class.simpleName))
                if (value is Mode.Pick) {
                    encodeStringElement(descriptor, 1, value.resultKey)
                }
                endStructure(descriptor)
            }
        }
    }

    object ModeNavType : NavType<Mode>(isNullableAllowed = false) {
        override fun get(bundle: SavedState, key: String): Mode? =
            bundle.getString(key)?.let(::parseValue)

        override fun parseValue(value: String): Mode = Json.decodeFromString(value)

        override fun put(bundle: SavedState, key: String, value: Mode) {
            bundle.putString(key, serializeAsValue(value))
        }

        override fun serializeAsValue(value: Mode): String = Json.encodeToString(value)
    }

    companion object {
        val typeMap = mapOf(typeOf<Mode>() to ModeNavType)
    }
}
