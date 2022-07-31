package com.patrykandpatryk.liftapp.bodyrecord.ui

import android.os.Parcelable
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

sealed class BodyModel {

    abstract val name: String

    abstract val values: List<Validatable<String>>

    abstract val showErrors: Boolean

    abstract fun mutate(
        name: String = this.name,
        values: List<Validatable<String>> = this.values,
        showErrors: Boolean = false,
    ): BodyModel

    @Parcelize
    object Loading : BodyModel(), Parcelable {

        @IgnoredOnParcel
        override val name: String = ""

        @IgnoredOnParcel
        override val values: List<Validatable<String>> = emptyList()

        @IgnoredOnParcel
        override val showErrors: Boolean = false

        override fun mutate(
            name: String,
            values: List<Validatable<String>>,
            showErrors: Boolean,
        ): BodyModel = this
    }

    @Parcelize
    data class Insert(
        override val name: String,
        override val values: List<Validatable<String>>,
        override val showErrors: Boolean = false,
    ) : BodyModel(), Parcelable {

        override fun mutate(
            name: String,
            values: List<Validatable<String>>,
            showErrors: Boolean,
        ): BodyModel = copy(
            name = name,
            values = values,
            showErrors = showErrors,
        )
    }

    @Parcelize
    data class Update(
        val recordId: Long,
        override val name: String,
        override val values: List<Validatable<String>>,
        override val showErrors: Boolean = false,
    ) : BodyModel(), Parcelable {

        override fun mutate(
            name: String,
            values: List<Validatable<String>>,
            showErrors: Boolean,
        ): BodyModel = copy(
            name = name,
            values = values,
            showErrors = showErrors,
        )
    }
}
