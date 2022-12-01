package com.patrykandpatryk.liftapp.feature.exercises.model

sealed interface Event {

    class OnExercisesPicked(val exerciseIds: List<Long>) : Event
}
