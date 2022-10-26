package com.patrykandpatryk.liftapp.feature.exercises.model

import com.patrykandpatryk.liftapp.feature.exercises.ui.ExercisesItem

data class ScreenState(
    val pickingMode: Boolean,
    val exercises: List<ExercisesItem> = emptyList(),
    val query: String = "",
    val groupBy: GroupBy = GroupBy.Name,
)
