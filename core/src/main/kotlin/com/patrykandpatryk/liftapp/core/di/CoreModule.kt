package com.patrykandpatryk.liftapp.core.di

import com.patrykandpatryk.liftapp.core.ui.name.NameResolverImpl
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.Collator

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Binds
    fun bindNameSolver(solver: NameResolverImpl): NameResolver

    companion object {

        @Provides
        fun provideCollator(): Collator = Collator.getInstance()
    }
}
