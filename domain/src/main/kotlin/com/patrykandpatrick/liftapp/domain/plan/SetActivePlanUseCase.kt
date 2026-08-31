package com.patrykandpatrick.liftapp.domain.plan

import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatrick.liftapp.domain.target.GenerationParameters
import com.patrykandpatrick.liftapp.domain.target.TargetGenerator
import com.patrykandpatrick.liftapp.domain.target.UpsertExerciseTargetsContract
import java.time.LocalDate
import javax.inject.Inject

/**
 * Starts a plan: records it as active, lays out its days, and writes out its prescription.
 *
 * [rebuildTargets] decides what happens to a plan that already has one. Off, generation only fills
 * the gaps, so a plan reconfigured to start on a different date keeps every target the user edited.
 * On, the whole prescription is re-authored from [parameters], which is what changing the intent or
 * the starting RIR has to mean — and what discards those edits. Asking rather than assuming is the
 * point: a silent rebuild is indistinguishable from having lost the edits to a bug.
 */
class SetActivePlanUseCase
@Inject
constructor(
    @PreferenceQualifier.ActivePlan private val activePlan: Preference<ActivePlan?>,
    private val addPlanItemsScheduleContract: AddPlanItemsScheduleContract,
    private val generateTargets: TargetGenerator,
    private val upsertTargets: UpsertExerciseTargetsContract,
) {
    suspend operator fun invoke(
        plan: Plan,
        startDate: LocalDate,
        parameters: GenerationParameters,
        rebuildTargets: Boolean = false,
    ) {
        activePlan.set(
            ActivePlan(
                planID = plan.id,
                startDate = startDate,
                cycleCount = parameters.cycleCount,
            )
        )
        addPlanItemsScheduleContract.addPlanItemsSchedule(plan, startDate, parameters.cycleCount)
        val targets = generateTargets(plan, parameters)
        if (rebuildTargets) {
            upsertTargets.upsertTargets(targets)
        } else {
            upsertTargets.addMissingTargets(targets)
        }
    }
}
