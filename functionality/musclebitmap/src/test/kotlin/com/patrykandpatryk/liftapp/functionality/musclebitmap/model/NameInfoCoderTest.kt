package com.patrykandpatryk.liftapp.functionality.musclebitmap.model

import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.functionality.musclebitmap.di.MuscleBitmapModule
import kotlinx.serialization.json.Json
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals

class NameInfoCoderTest {

    private val sut: NameInfoCoder = NameInfoCoder(
        Json,
        MuscleBitmapModule.provideSha1Algorithm(),
    )

    @ParameterizedTest
    @MethodSource("nameInfoArguments")
    fun `Given NameInfo is encoded, produced hash is always the same`(nameInfo: NameInfo) {
        val encodedOne = sut.encodeToName(
            primaryMuscles = nameInfo.mainMuscles,
            secondaryMuscles = nameInfo.secondaryMuscles,
            tertiaryMuscles = nameInfo.tertiaryMuscles,
            isDark = nameInfo.isDark,
        )

        val encodedTwo = sut.encodeToName(
            primaryMuscles = nameInfo.mainMuscles,
            secondaryMuscles = nameInfo.secondaryMuscles,
            tertiaryMuscles = nameInfo.tertiaryMuscles,
            isDark = nameInfo.isDark,
        )

        assertEquals(encodedOne, encodedTwo)
    }

    companion object {

        @JvmStatic
        fun nameInfoArguments(): Array<NameInfo> =
            arrayOf(
                NameInfo.create(
                    mainMuscles = listOf(Muscle.Triceps),
                    secondaryMuscles = listOf(Muscle.Shoulders),
                    tertiaryMuscles = listOf(Muscle.Forearms),
                    isDark = true,
                ),
                NameInfo.create(
                    mainMuscles = listOf(Muscle.LowerBack, Muscle.Hamstrings),
                    secondaryMuscles = listOf(Muscle.Forearms, Muscle.Shoulders, Muscle.Quadriceps),
                    tertiaryMuscles = listOf(Muscle.Abs, Muscle.Quadriceps),
                    isDark = true,
                ),
                NameInfo.create(
                    mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
                    secondaryMuscles = listOf(Muscle.Forearms, Muscle.Shoulders, Muscle.Quadriceps),
                    tertiaryMuscles = listOf(Muscle.Abs, Muscle.Quadriceps),
                    isDark = false,
                ),
            )
    }
}
