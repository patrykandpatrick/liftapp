package com.patrykandpatryk.liftapp.feature.newroutine.domain

sealed class Intent {

    class UpdateName(val name: String) : Intent()

    object Save : Intent()

    class AddPickedExercises(val exerciseIds: List<Long>) : Intent()

    class RemovePickedExercise(val exerciseId: Long) : Intent()
}
