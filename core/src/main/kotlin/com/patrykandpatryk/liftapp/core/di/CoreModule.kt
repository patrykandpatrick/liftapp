package com.patrykandpatryk.liftapp.core.di

import com.patrykandpatryk.liftapp.core.ui.name.NameSolverImpl
import com.patrykandpatryk.liftapp.domain.model.NameSolver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Binds
    fun bindNameSolver(solver: NameSolverImpl): NameSolver
}
