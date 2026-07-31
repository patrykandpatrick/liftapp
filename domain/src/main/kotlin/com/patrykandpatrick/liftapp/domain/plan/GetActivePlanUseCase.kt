package com.patrykandpatrick.liftapp.domain.plan

import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatrick.liftapp.domain.exception.PlanNotFoundException
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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
        activePlan.get().transformLatest { selectedPlan ->
            if (selectedPlan == null) {
                emit(null)
            } else {
                emitAll(
                    getPlanUseCase
                        .getPlan(selectedPlan.planID)
                        .map<Plan, Pair<ActivePlan, Plan>?> { plan -> selectedPlan to plan }
                        .catch { error ->
                            if (error is PlanNotFoundException) {
                                // A deleted plan can remain selected briefly because the plan and
                                // preference live in separate stores. Treat that stale selection
                                // exactly like having no active plan, and repair it for subsequent
                                // collectors.
                                activePlan.update { currentPlan ->
                                    currentPlan?.takeUnless {
                                        it.planID == selectedPlan.planID
                                    }
                                }
                                emit(null)
                            } else {
                                throw error
                            }
                        }
                )
            }
        }
}
