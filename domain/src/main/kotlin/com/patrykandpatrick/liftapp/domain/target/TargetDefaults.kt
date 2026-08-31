package com.patrykandpatrick.liftapp.domain.target

import com.patrykandpatrick.liftapp.domain.exercise.ExerciseRole
import com.patrykandpatrick.liftapp.domain.exercise.ExerciseType

/**
 * What to prescribe when nothing has been authored — a workout started outside a plan, or an
 * exercise added to one after its targets were generated.
 *
 * Reads [TargetPrescriptions] with no intent, because an unplanned workout has stated none, and at
 * the loose end of the RIR band, because there is no mesocycle to place it in. A plan's own targets
 * come from [TargetGenerator] instead, off the same table.
 *
 * Progression policy deliberately does not appear. It decides how load is allowed to move between
 * sessions, and these targets carry no load at all — [SetTarget.loadPercent] stays null until a
 * generator that knows the lifter's numbers fills it in.
 */
object TargetDefaults {

    /** The set count the app has always defaulted to, and a reasonable floor for one exercise. */
    const val SET_COUNT = 3

    fun of(
        exerciseType: ExerciseType,
        role: ExerciseRole?,
        setCount: Int = SET_COUNT,
    ): TargetTemplate {
        val prescription = TargetPrescriptions.of(exerciseType, role, TrainingIntent.Both)
        val set = prescription.setTarget(rir = prescription.rirRange?.last)
        return TargetTemplate(sets = List(setCount) { set }, restTime = prescription.restTime)
    }
}
