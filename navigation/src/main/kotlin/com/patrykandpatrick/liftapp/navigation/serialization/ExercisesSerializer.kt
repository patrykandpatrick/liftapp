package com.patrykandpatrick.liftapp.navigation.serialization

import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ExercisesSerializer : KSerializer<Routes.Home.Exercises> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor(checkNotNull(Routes.Home.Exercises::class.qualifiedName)) {
            element(
                elementName = "mode",
                descriptor = ExerciseListRouteData.Mode.serializer().descriptor,
            )
            element(
                elementName = "disabledExerciseIDs",
                descriptor = ListSerializer(Long.serializer()).descriptor,
            )
        }

    override fun deserialize(decoder: Decoder): Routes.Home.Exercises = Routes.Home.Exercises

    override fun serialize(encoder: Encoder, value: Routes.Home.Exercises) {
        ExerciseListRouteData.serializer().serialize(encoder, value)
    }
}
