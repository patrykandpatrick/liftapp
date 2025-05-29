package com.patrykandpatryk.liftapp.feature.dashboard.model

import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises
import com.patrykandpatryk.liftapp.domain.workout.Workout

sealed class PlanScheduleItem {
    data object Rest : PlanScheduleItem()

    data class Routine(val routine: RoutineWithExercises, val workout: Workout?) :
        PlanScheduleItem()
}
