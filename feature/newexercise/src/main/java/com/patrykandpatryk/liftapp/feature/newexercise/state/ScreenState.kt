package com.patrykandpatryk.liftapp.feature.newexercise.state

import android.os.Parcelable
import com.patrykandpatryk.liftapp.domain.exercise.ExerciseType
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ScreenState(
    val name: String = "",
    val type: ExerciseType = ExerciseType.Cardio,
    val mainMuscles: List<Muscle> = emptyList(),
    val secondaryMuscles: List<Muscle> = emptyList(),
    val tertiaryMuscles: List<Muscle> = emptyList(),
) : Parcelable {

    @IgnoredOnParcel
    val disabledMainMuscles = secondaryMuscles + tertiaryMuscles

    @IgnoredOnParcel
    val disabledSecondaryMuscles = mainMuscles + tertiaryMuscles

    @IgnoredOnParcel
    val disabledTertiaryMuscles = mainMuscles + secondaryMuscles
}
