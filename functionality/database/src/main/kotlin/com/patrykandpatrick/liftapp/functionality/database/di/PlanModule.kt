package com.patrykandpatrick.liftapp.functionality.database.di

import com.patrykandpatrick.liftapp.domain.plan.AddPlanItemsScheduleContract
import com.patrykandpatrick.liftapp.domain.plan.DeletePlanContract
import com.patrykandpatrick.liftapp.domain.plan.GetAllPlansUseCase
import com.patrykandpatrick.liftapp.domain.plan.GetPlanItemContract
import com.patrykandpatrick.liftapp.domain.plan.GetPlanUseCase
import com.patrykandpatrick.liftapp.domain.plan.UpsertPlanContract
import com.patrykandpatrick.liftapp.functionality.database.plan.RoomPlanRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PlanModule {
    @Binds fun bindGetPlanUseCase(repository: RoomPlanRepository): GetPlanUseCase

    @Binds fun binUpsertPlanContract(repository: RoomPlanRepository): UpsertPlanContract

    @Binds fun bindGetAllPlansUseCase(repository: RoomPlanRepository): GetAllPlansUseCase

    @Binds
    fun bindAddPlanItemsScheduleContract(
        repository: RoomPlanRepository
    ): AddPlanItemsScheduleContract

    @Binds fun bindGetPlanItemContract(repository: RoomPlanRepository): GetPlanItemContract

    @Binds fun bindDeletePlanContract(repository: RoomPlanRepository): DeletePlanContract
}
