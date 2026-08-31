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
 * Activation re-authors the plan. Every target is regenerated from [parameters], so changing the
 * intent or the cycle count here does what it looks like it does — and an edit made to a single
 * cell afterwards is lost if the plan is configured again. Activation is the explicit act of saying
 * what the block is, which is the only place that trade is the right way round.
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
    ) {
        activePlan.set(
            ActivePlan(
                planID = plan.id,
                startDate = startDate,
                cycleCount = parameters.cycleCount,
            )
        )
        addPlanItemsScheduleContract.addPlanItemsSchedule(plan, startDate, parameters.cycleCount)
        upsertTargets.upsertTargets(generateTargets(plan, parameters))
    }
}
