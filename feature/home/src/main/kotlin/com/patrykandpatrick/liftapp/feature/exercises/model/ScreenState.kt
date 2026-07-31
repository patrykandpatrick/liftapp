package com.patrykandpatrick.liftapp.feature.exercises.model

import com.patrykandpatrick.liftapp.core.text.StringTextFieldState
import com.patrykandpatrick.liftapp.feature.exercises.ui.ExercisesItem
import com.patrykandpatrick.liftapp.navigation.data.ExerciseListRouteData

data class ScreenState(
    val mode: ExerciseListRouteData.Mode,
    val query: StringTextFieldState,
    val exercises: List<ExercisesItem> = emptyList(),
    val groupBy: GroupBy = GroupBy.Name,
    val selectedItemCount: Int = 0,
) {
    val pickingMode: Boolean = mode is ExerciseListRouteData.Mode.Pick
}
