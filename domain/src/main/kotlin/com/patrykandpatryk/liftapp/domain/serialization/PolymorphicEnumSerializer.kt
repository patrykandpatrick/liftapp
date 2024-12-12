package com.patrykandpatryk.liftapp.domain.serialization

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure

/**
 * [Source](https://github.com/imotions/carp.core-kotlin/blob/develop/carp.common/src/commonMain/kotlin/dk/cachet/carp/common/infrastructure/serialization/PolymorphicEnumSerializer.kt)
 * A serializer which supports registering [Enum]s as subclasses in polymorphic serialization when
 * class discriminators are used. When class discriminators are used, an enum is not encoded as a
 * structure which the class discriminator can be added to. An exception is thrown when initializing
 * Json: "Serializer for <enum> of kind ENUM cannot be serialized polymorphically with class
 * discriminator." This serializer encodes the enum as a structure with a single `value` holding the
 * enum value.
 *
 * Use this serializer to register the enum in the serializers module, e.g.:
 * `subclass(<enum>::class, PolymorphicEnumSerializer(<enum>.serializer())`
 */
@Suppress("MaxLineLength")
@OptIn(ExperimentalSerializationApi::class)
class PolymorphicEnumSerializer<T : Enum<T>>(private val enumSerializer: KSerializer<T>) :
    KSerializer<T> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(enumSerializer.descriptor.serialName) {
            element("value", enumSerializer.descriptor)
        }

    override fun deserialize(decoder: Decoder): T =
        decoder.decodeStructure(descriptor) {
            decodeElementIndex(descriptor)
            decodeSerializableElement(descriptor, 0, enumSerializer)
        }

    override fun serialize(encoder: Encoder, value: T) =
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(descriptor, 0, enumSerializer, value)
        }
}
