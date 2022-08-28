package com.patrykandpatryk.liftapp.bodyentry.ui

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.patrykandpatryk.liftapp.domain.format.FormattedDate
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

internal sealed class ScreenState : Parcelable {

    abstract val name: String

    abstract val values: List<Validatable<String>>

    abstract val showErrors: Boolean

    abstract val is24H: Boolean

    abstract val formattedDate: FormattedDate

    abstract val unit: ValueUnit

    abstract fun mutate(
        name: String = this.name,
        values: List<Validatable<String>> = this.values,
        formattedDate: FormattedDate = this.formattedDate,
        showErrors: Boolean = false,
    ): ScreenState

    @Stable
    @Parcelize
    internal object Loading : ScreenState() {

        @IgnoredOnParcel
        override val name: String = ""

        @IgnoredOnParcel
        override val values: List<Validatable<String>> = emptyList()

        @IgnoredOnParcel
        override val showErrors: Boolean = false

        @IgnoredOnParcel
        override val is24H: Boolean = false

        @IgnoredOnParcel
        override val formattedDate: FormattedDate = FormattedDate.Empty

        @IgnoredOnParcel
        override val unit: ValueUnit = MassUnit.Kilograms

        override fun mutate(
            name: String,
            values: List<Validatable<String>>,
            formattedDate: FormattedDate,
            showErrors: Boolean,
        ): ScreenState = this
    }

    @Stable
    @Parcelize
    internal data class Insert(
        override val name: String,
        override val values: List<Validatable<String>>,
        override val formattedDate: FormattedDate,
        override val unit: ValueUnit,
        override val is24H: Boolean = false,
        override val showErrors: Boolean = false,
    ) : ScreenState() {

        override fun mutate(
            name: String,
            values: List<Validatable<String>>,
            formattedDate: FormattedDate,
            showErrors: Boolean,
        ): ScreenState = copy(
            name = name,
            values = values,
            showErrors = showErrors,
            formattedDate = formattedDate,
        )
    }

    @Stable
    @Parcelize
    internal data class Update(
        val recordId: Long,
        override val name: String,
        override val values: List<Validatable<String>>,
        override val formattedDate: FormattedDate,
        override val unit: ValueUnit,
        override val is24H: Boolean,
        override val showErrors: Boolean = false,
    ) : ScreenState() {

        override fun mutate(
            name: String,
            values: List<Validatable<String>>,
            formattedDate: FormattedDate,
            showErrors: Boolean,
        ): ScreenState = copy(
            name = name,
            values = values,
            showErrors = showErrors,
            formattedDate = formattedDate,
        )
    }
}
