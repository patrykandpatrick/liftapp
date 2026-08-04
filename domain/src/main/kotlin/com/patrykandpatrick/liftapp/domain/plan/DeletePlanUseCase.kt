package com.patrykandpatrick.liftapp.domain.plan

import com.patrykandpatrick.liftapp.domain.datastore.Preference
import com.patrykandpatrick.liftapp.domain.di.PreferenceQualifier
import javax.inject.Inject
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

fun interface DeletePlanContract {
    suspend fun deletePlan(id: Long)
}

class DeletePlanUseCase
@Inject
constructor(
    private val deletePlanContract: DeletePlanContract,
    @param:PreferenceQualifier.ActivePlan private val activePlan: Preference<ActivePlan?>,
) {
    suspend operator fun invoke(id: Long) {
        withContext(NonCancellable) {
            deletePlanContract.deletePlan(id)
            activePlan.update { selectedPlan -> selectedPlan?.takeUnless { it.planID == id } }
        }
    }
}
