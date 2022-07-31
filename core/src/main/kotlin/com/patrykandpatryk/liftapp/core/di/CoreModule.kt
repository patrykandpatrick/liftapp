package com.patrykandpatryk.liftapp.core.di

import com.patrykandpatryk.liftapp.core.ui.name.NameResolverImpl
import com.patrykandpatryk.liftapp.core.validation.HigherThanZero
import com.patrykandpatryk.liftapp.core.validation.HigherThanZeroValidator
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import com.patrykandpatryk.liftapp.domain.validation.Validator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.Collator
import java.text.DecimalFormat

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Binds
    fun bindNameSolver(solver: NameResolverImpl): NameResolver
    companion object {

        @Provides
        fun provideCollator(): Collator = Collator.getInstance()

        @Provides
        @HigherThanZero
        fun provideHigherThanZeroValidator(): Validator<Float> = HigherThanZeroValidator

        @Provides
        @Decimal
        fun provideDecimalFormat(): DecimalFormat = DecimalFormat("#.##")

        @Provides
        @Integer
        fun provideIntegerFormat(): DecimalFormat = DecimalFormat("#")
    }
}
