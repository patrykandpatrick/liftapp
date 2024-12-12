package com.patrykandpatryk.liftapp.feature.routine.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.core.model.MuscleModel
import com.patrykandpatryk.liftapp.domain.routine.RoutineExerciseItem
import kotlinx.parcelize.Parcelize

sealed class ScreenState {

    open val name: String = ""

    open val showDeleteDialog: Boolean = false

    open val imagePath: String? = null

    open val exercises: List<RoutineExerciseItem> = emptyList()

    open val muscles: List<MuscleModel> = emptyList()

    fun mutate(
        name: String = this.name,
        showDeleteDialog: Boolean = this.showDeleteDialog,
        imagePath: String? = this.imagePath,
        exercises: List<RoutineExerciseItem> = this.exercises,
        muscles: List<MuscleModel> = this.muscles,
    ): Populated =
        Populated(
            name = name,
            showDeleteDialog = showDeleteDialog,
            imagePath = imagePath,
            exercises = exercises,
            muscles = muscles,
        )

    @Parcelize @Immutable object Loading : ScreenState(), Parcelable

    @Parcelize
    @Immutable
    data class Populated(
        override val name: String,
        override val showDeleteDialog: Boolean = false,
        override val imagePath: String? = null,
        override val exercises: List<RoutineExerciseItem> = emptyList(),
        override val muscles: List<MuscleModel> = emptyList(),
    ) : ScreenState(), Parcelable
}
