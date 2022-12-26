package com.patrykandpatryk.liftapp.feature.routine.model

sealed interface Event {

    object RoutineNotFound : Event

    class EditRoutine(val id: Long) : Event
}
