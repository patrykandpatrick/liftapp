package com.patrykandpatrick.liftapp.domain.plan

interface UpsertPlanContract {

    suspend fun upsertPlan(plan: Plan)
}
