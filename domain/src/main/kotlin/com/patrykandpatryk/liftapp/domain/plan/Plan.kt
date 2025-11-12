package com.patrykandpatryk.liftapp.domain.plan

import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises

data class Plan(val id: Long, val name: String?, val description: String, val items: List<Item>) {

    val routines: List<RoutineWithExercises>
        get() = items.filterIsInstance<Item.Routine>().map { it.routine }

    sealed class Item(val id: Long) {
        data class Routine(val routine: RoutineWithExercises) : Item(routine.id)

        data object Rest : Item(0L)
    }
}
