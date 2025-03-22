package com.patrykandpatryk.liftapp.domain.plan

interface UpsertPlanContract {

    suspend fun upsertPlan(plan: Plan)
}
