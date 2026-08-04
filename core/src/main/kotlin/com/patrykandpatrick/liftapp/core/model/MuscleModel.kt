package com.patrykandpatrick.liftapp.core.model

import android.os.Parcelable
import androidx.annotation.StringRes
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.ui.resource.stringRes
import com.patrykandpatrick.liftapp.domain.muscle.Muscle
import javax.annotation.concurrent.Immutable
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class MuscleModel(val muscle: Muscle, val type: Type, val nameRes: Int) : Parcelable {

    enum class Type(@StringRes val nameRes: Int) {
        Primary(nameRes = R.string.primary_muscle),
        Secondary(nameRes = R.string.secondary_muscle),
        Tertiary(nameRes = R.string.tertiary_muscle),
    }

    companion object {

        fun create(
            primaryMuscles: Collection<Muscle>,
            secondaryMuscles: Collection<Muscle>,
            tertiaryMuscles: Collection<Muscle>,
        ): List<MuscleModel> = buildList {
            addAll(
                primaryMuscles.map { muscle ->
                    MuscleModel(muscle = muscle, type = Type.Primary, nameRes = muscle.stringRes)
                }
            )

            addAll(
                secondaryMuscles.map { muscle ->
                    MuscleModel(muscle = muscle, type = Type.Secondary, nameRes = muscle.stringRes)
                }
            )

            addAll(
                tertiaryMuscles.map { muscle ->
                    MuscleModel(muscle = muscle, type = Type.Tertiary, nameRes = muscle.stringRes)
                }
            )
        }
    }
}
