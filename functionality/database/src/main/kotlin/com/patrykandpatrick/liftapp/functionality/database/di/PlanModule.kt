package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.plan.AddPlanItemsScheduleContract
import com.patrykandpatrick.liftapp.domain.plan.GetAllPlansUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanItemContract
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.UpsertPlanContract
import com.patrykandpatrick.liftapp.functionality.database.plan.RoomPlanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface PlanModule {
    @Binds fun bindGetPlanUseCase(repository: RoomPlanRepository): GetPlanUseCase

    @Binds fun binUpsertPlanContract(repository: RoomPlanRepository): UpsertPlanContract

    @Binds fun bindGetAllPlansUseCase(repository: RoomPlanRepository): GetAllPlansUseCase

    @Binds
    fun bindAddPlanItemsScheduleContract(
        repository: RoomPlanRepository
    ): AddPlanItemsScheduleContract

    @Binds fun bindGetPlanItemContract(repository: RoomPlanRepository): GetPlanItemContract
}
