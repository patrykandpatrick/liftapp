package com.patrykandpatryk.liftapp.core.model

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.resource.stringRes
import com.patrykandpatryk.liftapp.domain.muscle.Muscle
import javax.annotation.concurrent.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MuscleModel(val muscle: Muscle, val type: Type, val nameRes: Int) : Parcelable {

    enum class Type(@StringRes val nameRes: Int, @ColorRes val colorRes: Int) {
        Primary(nameRes = R.string.primary_muscle, colorRes = R.color.muscle_primary),
        Secondary(nameRes = R.string.secondary_muscle, colorRes = R.color.muscle_secondary),
        Tertiary(nameRes = R.string.tertiary_muscle, colorRes = R.color.muscle_tertiary),
    }

    companion object {

        fun create(
            primaryMuscles: Collection<Muscle>,
            secondaryMuscles: Collection<Muscle>,
            tertiaryMuscles: Collection<Muscle>,
        ): List<MuscleModel> = buildList {
            primaryMuscles
                .map { muscle ->
                    MuscleModel(muscle = muscle, type = Type.Primary, nameRes = muscle.stringRes)
                }
                .let(::addAll)

            secondaryMuscles
                .map { muscle ->
                    MuscleModel(muscle = muscle, type = Type.Secondary, nameRes = muscle.stringRes)
                }
                .let(::addAll)

            tertiaryMuscles
                .map { muscle ->
                    MuscleModel(muscle = muscle, type = Type.Tertiary, nameRes = muscle.stringRes)
                }
                .let(::addAll)
        }
    }
}
