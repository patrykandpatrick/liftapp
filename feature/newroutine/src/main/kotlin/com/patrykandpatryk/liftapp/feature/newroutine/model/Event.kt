package com.patrykandpatryk.liftapp.feature.newroutine.model

sealed class Event {

    object RoutineNotFound : Event()

    object EntrySaved : Event()
}
