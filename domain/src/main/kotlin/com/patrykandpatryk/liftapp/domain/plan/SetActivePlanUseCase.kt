package com.patrykandpatryk.liftapp.domain.plan

import com.patrykandpatrick.opto.domain.Preference
import com.patrykandpatryk.liftapp.domain.di.PreferenceQualifier
import java.time.LocalDate
import javax.inject.Inject

class SetActivePlanUseCase
@Inject
constructor(
    @PreferenceQualifier.ActivePlan private val activePlan: Preference<ActivePlan?>,
    private val addPlanItemsScheduleContract: AddPlanItemsScheduleContract,
) {
    suspend operator fun invoke(plan: Plan, startDate: LocalDate, cycleCount: Int) {
        activePlan.set(ActivePlan(planID = plan.id, startDate = startDate, cycleCount = cycleCount))
        addPlanItemsScheduleContract.addPlanItemsSchedule(plan, startDate, cycleCount)
    }
}
