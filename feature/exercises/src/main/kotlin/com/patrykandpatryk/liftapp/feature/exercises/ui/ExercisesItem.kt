package com.patrykandpatryk.liftapp.feature.exercises.ui

import androidx.annotation.DrawableRes

sealed class ExercisesItem(open val key: Any) {

    class Header(val title: String) : ExercisesItem(title)

    class Exercise(
        val id: Long,
        key: Any,
        val name: String,
        val muscles: String,
        @DrawableRes val iconRes: Int,
    ) : ExercisesItem(key)
}
