package com.patrykandpatryk.liftapp.functionality.database.di

import com.patrykandpatryk.liftapp.domain.plan.GetPlanContract
import com.patrykandpatryk.liftapp.domain.plan.UpsertPlanContract
import com.patrykandpatryk.liftapp.functionality.database.plan.RoomPlanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface PlanModule {
    @Binds fun bindGetPlanContract(repository: RoomPlanRepository): GetPlanContract

    @Binds fun binUpsertPlanContract(repository: RoomPlanRepository): UpsertPlanContract
}
