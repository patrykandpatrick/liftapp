package com.patrykandpatryk.liftapp.core.di

import com.patrykandpatryk.liftapp.core.search.SearchAlgorithm
import com.patrykandpatryk.liftapp.core.search.SearchAlgorithmImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SearchModule {

    @Binds fun bindSearchAlgorithmImpl(searchAlgorithmImpl: SearchAlgorithmImpl): SearchAlgorithm
}
