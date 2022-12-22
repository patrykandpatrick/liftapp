package com.patrykandpatryk.liftapp.feature.exercise.model

import android.os.Parcelable
import com.patrykandpatryk.liftapp.core.model.MuscleModel
import kotlinx.parcelize.Parcelize
import javax.annotation.concurrent.Immutable

sealed class ScreenState {

    open val name: String = ""

    open val showDeleteDialog: Boolean = false

    open val imagePath: String? = null

    open val muscles: List<MuscleModel> = emptyList()

    fun mutate(
        name: String = this.name,
        showDeleteDialog: Boolean = this.showDeleteDialog,
        imagePath: String? = this.imagePath,
        muscles: List<MuscleModel> = this.muscles,
    ): Populated = Populated(
        name = name,
        showDeleteDialog = showDeleteDialog,
        imagePath = imagePath,
        muscles = muscles,
    )

    @Parcelize
    @Immutable
    object Loading : ScreenState(), Parcelable

    @Parcelize
    @Immutable
    data class Populated(
        override val name: String,
        override val showDeleteDialog: Boolean = false,
        override val imagePath: String? = null,
        override val muscles: List<MuscleModel> = emptyList(),
    ) : ScreenState(), Parcelable
}
