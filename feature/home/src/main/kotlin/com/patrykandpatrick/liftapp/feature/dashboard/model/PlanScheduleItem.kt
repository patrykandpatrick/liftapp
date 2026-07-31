package com.patrykandpatrick.liftapp.feature.dashboard.model

import com.patrykandpatrick.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatrick.liftapp.domain.workout.Workout

sealed class PlanScheduleItem {
    data object Rest : PlanScheduleItem()

    data object None : PlanScheduleItem()

    data class Routine(val routine: RoutineWithExercises, val workout: Workout?) :
        PlanScheduleItem()
}
