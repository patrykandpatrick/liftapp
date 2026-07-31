package com.patrykandpatrick.liftapp.feature.exercises.ui

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ExercisesItem(open val key: Any) {

    @Immutable class Header(val title: String) : ExercisesItem(title)

    @Immutable
    data class Exercise(
        val id: Long,
        override val key: Any,
        val name: String,
        val muscles: String,
        val icon: ImageVector,
        val checked: Boolean = false,
        val enabled: Boolean = true,
        val nameHighlightPosition: IntRange = IntRange.EMPTY,
    ) : ExercisesItem(key)
}
