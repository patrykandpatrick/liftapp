package com.patrykandpatrick.liftapp.feature.routines.model

interface Action {
    data object PopBackStack : Action

    data object AddNewRoutine : Action

    data class RoutineClicked(val routineID: Long) : Action
}
