package com.patrykandpatryk.liftapp.functionality.musclebitmap.model

import com.patrykandpatryk.liftapp.domain.base64.Base64
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class NameInfoCoder @Inject constructor(
    private val json: Json,
    private val base64: Base64,
) : NameInfoEncoder, NameInfoDecoder {

    override fun encodeToName(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
        isDark: Boolean,
    ): String {
        val info = NameInfo(
            mainMuscles = primaryMuscles.sorted(),
            secondaryMuscles = secondaryMuscles.sorted(),
            tertiaryMuscles = tertiaryMuscles.sorted(),
            isDark = isDark,
        )

        return base64.encode(json.encodeToString(info))
    }

    override fun decodeFromName(input: String): NameInfo =
        json.decodeFromString(base64.decode(input))
}
