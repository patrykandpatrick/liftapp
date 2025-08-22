package com.patrykandpatryk.liftapp.domain.exerciseset

object OneRepMaxCalculator {
    private const val EPLEY_REP_COUNT_DIVISOR = 30.0

    fun getOneRepMax(mass: Double, reps: Int): Double =
        when {
            reps == 0 || mass == 0.0 -> 0.0
            reps == 1 -> mass
            else -> mass * (1.0 + reps / EPLEY_REP_COUNT_DIVISOR)
        }
}
