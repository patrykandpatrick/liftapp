package com.patrykandpatryk.liftapp.feature.routine.model

sealed interface Event {

    data object RoutineNotFound : Event

    data object EditRoutine : Event
}
