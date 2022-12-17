package com.patrykandpatryk.liftapp.feature.exercise.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import javax.annotation.concurrent.Immutable

sealed class ScreenState {

    open val name: String = ""

    open val showDeleteDialog: Boolean = false

    open val selectedTabIndex: Int = 0

    fun mutate(
        name: String = this.name,
        showDeleteDialog: Boolean = this.showDeleteDialog,
        selectedTabIndex: Int = this.selectedTabIndex,
    ): Populated = Populated(
        name = name,
        showDeleteDialog = showDeleteDialog,
        selectedTabIndex = selectedTabIndex,
    )

    @Parcelize
    @Immutable
    object Loading : ScreenState(), Parcelable

    @Parcelize
    @Immutable
    data class Populated(
        override val name: String,
        override val showDeleteDialog: Boolean = false,
        override val selectedTabIndex: Int = 0,
    ) : ScreenState(), Parcelable
}
