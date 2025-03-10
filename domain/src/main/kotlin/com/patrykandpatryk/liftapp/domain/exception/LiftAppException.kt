package com.patrykandpatryk.liftapp.domain.exception

sealed class LiftAppException(message: String) : Exception(message)

class RoutineNotFoundException(val id: Long) : LiftAppException("Routine with id = $id not found")

class PlanNotFoundException(val id: Long) : LiftAppException("Plan with id = $id not found")
