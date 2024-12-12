package com.patrykandpatryk.liftapp.functionality.musclebitmap.model

import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import java.security.MessageDigest
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val BYTES_FORMAT = "%02x"

class NameInfoCoder @Inject constructor(private val json: Json, private val digest: MessageDigest) :
    NameInfoEncoder {

    override fun encodeToName(
        primaryMuscles: List<Muscle>,
        secondaryMuscles: List<Muscle>,
        tertiaryMuscles: List<Muscle>,
        isDark: Boolean,
    ): String {
        val info =
            NameInfo.create(
                mainMuscles = primaryMuscles,
                secondaryMuscles = secondaryMuscles,
                tertiaryMuscles = tertiaryMuscles,
                isDark = isDark,
            )

        val jsonBytes = json.encodeToString(info).toByteArray(Charsets.ISO_8859_1)

        return digest.digest(jsonBytes).joinToString("") { String.format(BYTES_FORMAT, it) }
    }
}
