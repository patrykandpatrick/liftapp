package com.patrykandpatryk.liftapp.core.di

import com.patrykandpatryk.liftapp.core.ui.name.NameResolverImpl
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Binds
    fun bindNameSolver(solver: NameResolverImpl): NameResolver
}
