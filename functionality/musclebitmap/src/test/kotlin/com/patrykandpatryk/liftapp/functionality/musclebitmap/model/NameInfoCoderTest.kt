package com.patrykandpatryk.liftapp.functionality.musclebitmap.model

import com.patrykandpatryk.liftapp.domain.base64.JavaBase64
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import kotlinx.serialization.json.Json
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class NameInfoCoderTest {

    private val sut: NameInfoCoder = NameInfoCoder(
        Json,
        JavaBase64(),
    )

    @ParameterizedTest
    @MethodSource("nameInfoArguments")
    fun `Given NameInfo is encoded, decoding it returns exact same NameInfo`(nameInfo: NameInfo) {

        val encoded = sut.encodeToName(
            primaryMuscles = nameInfo.mainMuscles,
            secondaryMuscles = nameInfo.secondaryMuscles,
            tertiaryMuscles = nameInfo.tertiaryMuscles,
            isDark = nameInfo.isDark,
        )

        val decoded = sut.decodeFromName(encoded)

        assertEquals(nameInfo, decoded)
    }

    companion object {

        @JvmStatic
        fun nameInfoArguments(): Array<NameInfo> =
            arrayOf(
                NameInfo(
                    mainMuscles = listOf(Muscle.Triceps),
                    secondaryMuscles = listOf(Muscle.Shoulders),
                    tertiaryMuscles = listOf(Muscle.Forearms),
                    isDark = true,
                ),
                NameInfo(
                    mainMuscles = listOf(Muscle.LowerBack, Muscle.Hamstrings),
                    secondaryMuscles = listOf(Muscle.Forearms, Muscle.Shoulders, Muscle.Quadriceps),
                    tertiaryMuscles = listOf(Muscle.Abs, Muscle.Quadriceps),
                    isDark = true,
                ),
                NameInfo(
                    mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
                    secondaryMuscles = listOf(Muscle.Forearms, Muscle.Shoulders, Muscle.Quadriceps),
                    tertiaryMuscles = listOf(Muscle.Abs, Muscle.Quadriceps),
                    isDark = false,
                ),
            )
    }
}
