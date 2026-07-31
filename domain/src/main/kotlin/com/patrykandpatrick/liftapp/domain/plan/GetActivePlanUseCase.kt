package com.patrykandpatrick.liftapp.domain.plan

import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.domain.di.PreferenceQualifier
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest

class GetActivePlanUseCase
@Inject
constructor(
    private val getPlanUseCase: GetPlanUseCase,
    @param:PreferenceQualifier.ActivePlan private val activePlan: Preference<ActivePlan?>,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Pair<ActivePlan, Plan>?> =
        activePlan.get().transformLatest { activePlan ->
            if (activePlan == null) {
                emit(null)
            } else {
                emitAll(getPlanUseCase.getPlan(activePlan.planID).map { activePlan to it })
            }
        }
}
