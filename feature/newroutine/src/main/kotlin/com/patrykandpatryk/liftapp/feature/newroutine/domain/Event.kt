package com.patrykandpatryk.liftapp.feature.newroutine.domain

sealed class Event {

    object RoutineNotFound : Event()

    object EntrySaved : Event()
}
