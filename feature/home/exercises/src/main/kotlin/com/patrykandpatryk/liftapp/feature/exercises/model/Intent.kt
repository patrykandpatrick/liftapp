package com.patrykandpatryk.liftapp.feature.exercises.model

sealed interface Intent {

    class SetQuery(val query: String) : Intent

    class SetGroupBy(val groupBy: GroupBy) : Intent

    class SetExerciseChecked(val exerciseId: Long, val checked: Boolean) : Intent

    object FinishPickingExercises : Intent
}
