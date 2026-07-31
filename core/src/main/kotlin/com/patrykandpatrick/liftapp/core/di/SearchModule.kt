package com.patrykandpatrick.liftapp.core.di

import com.patrykandpatrick.liftapp.core.search.SearchAlgorithm
import com.patrykandpatrick.liftapp.core.search.SearchAlgorithmImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface SearchModule {

    @Binds fun bindSearchAlgorithmImpl(searchAlgorithmImpl: SearchAlgorithmImpl): SearchAlgorithm
}
