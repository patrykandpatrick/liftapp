package com.patrykandpatryk.liftapp.core.di

import com.patrykandpatryk.liftapp.core.format.FormatterImpl
import com.patrykandpatryk.liftapp.core.ui.name.NameResolverImpl
import com.patrykandpatryk.liftapp.core.validation.HigherThanZero
import com.patrykandpatryk.liftapp.core.validation.HigherThanZeroValidator
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import com.patrykandpatryk.liftapp.domain.validation.Validator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.text.Collator
import java.text.DecimalFormat
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
internal interface CoreModule {

    @Binds
    fun bindNameSolver(solver: NameResolverImpl): NameResolver

    @Binds
    fun bindFormatter(formatter: FormatterImpl): Formatter

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

        @Provides
        fun provideCoroutineExceptionHandler(): CoroutineExceptionHandler =
            CoroutineExceptionHandler { coroutineContext, throwable ->
                Timber.e("Uncaught exception in $coroutineContext", throwable)
            }
    }
}
