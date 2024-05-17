package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

sealed class ExercisesItem(open val key: Any) {

    @Immutable
    class Header(val title: String) : ExercisesItem(title)

    @Immutable
    data class Exercise(
        val id: Long,
        override val key: Any,
        val name: String,
        val muscles: String,
        @DrawableRes val iconRes: Int,
        val checked: Boolean = false,
        val enabled: Boolean = true,
        val nameHighlightPosition: IntRange = IntRange.EMPTY,
    ) : ExercisesItem(key)
}
