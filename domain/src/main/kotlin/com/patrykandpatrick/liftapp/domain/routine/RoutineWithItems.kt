package com.patrykandpatrick.liftapp.domain.routine

data class RoutineWithItems(val id: Long, val name: String, val items: List<RoutineItem>) {
    val exerciseIDs: List<Long>
        get() = items.flatMap(RoutineItem::exerciseIDs)
}
