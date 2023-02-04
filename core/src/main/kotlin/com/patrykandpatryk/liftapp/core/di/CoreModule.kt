package com.patrykandpatryk.liftapp.core.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.patrykandpatryk.liftapp.core.android.IsDarkModeHandler
import com.patrykandpatryk.liftapp.core.text.StringProviderImpl
import com.patrykandpatryk.liftapp.core.validation.HigherThanZeroValidator
import com.patrykandpatryk.liftapp.core.validation.NameValidator
import com.patrykandpatryk.liftapp.domain.android.IsDarkModePublisher
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.validation.Validator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber
import java.io.File
import java.text.Collator
import java.text.DecimalFormat

@Module
@InstallIn(SingletonComponent::class)
internal interface CoreModule {

    @Binds
    fun bindStringProvider(provider: StringProviderImpl): StringProvider

    @Binds
    @ValidatorType.Name
    fun bindNameValidator(validator: NameValidator): Validator<String>

    @Binds
    @ValidatorType.HigherThanZero
    fun bindHigherThanZeroValidatorValidator(validator: HigherThanZeroValidator): Validator<Float>

    @Binds
    fun bindIsDarkModeReceiver(useCase: IsDarkModeHandler): IsDarkModeReceiver

    @Binds
    fun bindIsDarkModePublisher(useCase: IsDarkModeHandler): IsDarkModePublisher

    companion object {

        @Provides
        fun provideCollator(): Collator = Collator.getInstance()

        @Provides
        @Decimal
        fun provideDecimalFormat(): DecimalFormat = DecimalFormat("#.##")

        @Provides
        @Integer
        fun provideIntegerFormat(): DecimalFormat = DecimalFormat("#")

        @Provides
        fun provideCoroutineExceptionHandler(): CoroutineExceptionHandler =
            CoroutineExceptionHandler { coroutineContext, throwable ->
                Timber.e(throwable, "Uncaught exception in $coroutineContext.")
            }

        @Provides
        fun provideContext(application: Application): Context =
            application.applicationContext

        @Provides
        fun provideResources(context: Context): Resources =
            context.resources

        @Provides
        fun provideFilesDir(context: Context): File = context.filesDir
    }
}
