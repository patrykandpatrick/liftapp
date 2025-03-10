package com.patrykandpatryk.liftapp.domain.plan

import com.patrykandpatryk.liftapp.domain.routine.RoutineWithExercises

data class Plan(val id: Long, val name: String, val description: String, val items: List<Item>) {
    sealed class Item {
        data class RoutineItem(val routine: RoutineWithExercises) : Item()

        data object RestItem : Item()
    }
}
