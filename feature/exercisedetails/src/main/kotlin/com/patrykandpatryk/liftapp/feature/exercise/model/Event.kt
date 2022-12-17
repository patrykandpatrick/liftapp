package com.patrykandpatryk.liftapp.feature.exercise.model

sealed interface Event {

    object ExerciseNotFound : Event

    class EditExercise(val id: Long) : Event
}
