package com.patrykandpatrick.liftapp.functionality.musclebitmap.model

import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import com.patrykandpatrick.liftapp.functionality.musclebitmap.di.MuscleBitmapModule
import kotlin.test.assertEquals
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class NameInfoCoderTest {

    private val sut: NameInfoCoder = NameInfoCoder(Json, MuscleBitmapModule.provideSha1Algorithm())

    // The version is what evicts images drawn with older colors or older art. It only does that if
    // it reaches the encoded form — and a property equal to its default would not, because the
    // app's `Json` leaves `encodeDefaults` off.
    @Test
    fun `Bumping the version changes the name a cached image is stored under`() {
        val muscles = listOf(Muscle.Chest)

        val names =
            (1..3)
                .map { version ->
                    Json.encodeToString(
                        NameInfo.create(
                            mainMuscles = muscles,
                            secondaryMuscles = emptyList(),
                            tertiaryMuscles = emptyList(),
                            isDark = false,
                            version = version,
                        )
                    )
                }
                .distinct()

        assertEquals(3, names.size, "The version is missing from $names.")
    }

    @ParameterizedTest
    @MethodSource("nameInfoArguments")
    fun `Given NameInfo is encoded, produced hash is always the same`(nameInfo: NameInfo) {
        val encodedOne =
            sut.encodeToName(
                primaryMuscles = nameInfo.mainMuscles,
                secondaryMuscles = nameInfo.secondaryMuscles,
                tertiaryMuscles = nameInfo.tertiaryMuscles,
                isDark = nameInfo.isDark,
            )

        val encodedTwo =
            sut.encodeToName(
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
                    secondaryMuscles = listOf(Muscle.FrontDelts),
                    tertiaryMuscles = listOf(Muscle.Forearms),
                    isDark = true,
                ),
                NameInfo.create(
                    mainMuscles = listOf(Muscle.LowerBack, Muscle.Hamstrings),
                    secondaryMuscles = listOf(Muscle.Forearms, Muscle.RearDelts, Muscle.Quadriceps),
                    tertiaryMuscles = listOf(Muscle.Abs, Muscle.Quadriceps),
                    isDark = true,
                ),
                NameInfo.create(
                    mainMuscles = listOf(Muscle.Chest, Muscle.Triceps),
                    secondaryMuscles = listOf(Muscle.Forearms, Muscle.RearDelts, Muscle.Quadriceps),
                    tertiaryMuscles = listOf(Muscle.Abs, Muscle.Quadriceps),
                    isDark = false,
                ),
            )
    }
}
