package com.patrykandpatryk.liftapp.feature.exercise.ui

import androidx.annotation.DrawableRes

sealed class ExercisesItem(val key: Any) {

    class Header(val title: String) : ExercisesItem(title)

    class Exercise(
        val id: Long,
        val name: String,
        val muscles: String,
        @DrawableRes val iconRes: Int,
    ) : ExercisesItem(id)
}
