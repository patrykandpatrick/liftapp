package com.patrykandpatryk.liftapp.functionality.musclebitmap.model

import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import com.patrykandpatryk.liftapp.domain.muscle.MuscleImageType
import kotlinx.serialization.Serializable

@Serializable
class NameInfo
private constructor(
    val mainMuscles: List<Muscle>,
    val secondaryMuscles: List<Muscle>,
    val tertiaryMuscles: List<Muscle>,
    val isDark: Boolean,
    val version: Int = 1,
    val muscleImageType: MuscleImageType,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NameInfo

        if (mainMuscles != other.mainMuscles) return false
        if (secondaryMuscles != other.secondaryMuscles) return false
        if (tertiaryMuscles != other.tertiaryMuscles) return false
        if (isDark != other.isDark) return false
        if (version != other.version) return false
        if (muscleImageType != other.muscleImageType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mainMuscles.hashCode()
        result = 31 * result + secondaryMuscles.hashCode()
        result = 31 * result + tertiaryMuscles.hashCode()
        result = 31 * result + isDark.hashCode()
        result = 31 * result + version
        result = 31 * result + muscleImageType.hashCode()
        return result
    }

    companion object {
        fun create(
            mainMuscles: List<Muscle>,
            secondaryMuscles: List<Muscle>,
            tertiaryMuscles: List<Muscle>,
            isDark: Boolean,
            version: Int = 1,
            muscleImageType: MuscleImageType = MuscleImageType.FrontAndRear,
        ): NameInfo =
            NameInfo(
                mainMuscles = mainMuscles.sorted(),
                secondaryMuscles = secondaryMuscles.sorted(),
                tertiaryMuscles = tertiaryMuscles.sorted(),
                isDark = isDark,
                version = version,
                muscleImageType = muscleImageType,
            )
    }
}
