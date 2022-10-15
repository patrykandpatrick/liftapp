package com.patrykandpatryk.liftapp.feature.newroutine.domain

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed class ScreenState : Parcelable {

    abstract val name: Validatable<String>

    abstract val showErrors: Boolean

    abstract fun mutate(
        name: Validatable<String> = this.name,
        showErrors: Boolean = this.showErrors,
    ): ScreenState

    @Parcelize
    @Immutable
    data class Insert(
        override val name: Validatable<String>,
        override val showErrors: Boolean = false,
    ) : ScreenState() {

        override fun mutate(
            name: Validatable<String>,
            showErrors: Boolean,
        ): ScreenState = copy(
            name = name,
            showErrors = showErrors,
        )
    }

    @Parcelize
    @Immutable
    data class Update(
        val id: Long,
        override val name: Validatable<String>,
        override val showErrors: Boolean = false,
    ) : ScreenState() {

        override fun mutate(
            name: Validatable<String>,
            showErrors: Boolean,
        ): ScreenState = copy(
            name = name,
            showErrors = showErrors,
        )
    }

    @Parcelize
    @Immutable
    object Loading : ScreenState() {

        @IgnoredOnParcel
        override val name: Validatable<String> = "".toInvalid()

        @IgnoredOnParcel
        override val showErrors: Boolean = false

        override fun mutate(
            name: Validatable<String>,
            showErrors: Boolean,
        ): ScreenState {
            error("Cannot mutate the `Loading` state.")
        }
    }
}
