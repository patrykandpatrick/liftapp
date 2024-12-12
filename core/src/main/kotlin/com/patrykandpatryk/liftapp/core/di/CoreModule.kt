package com.patrykandpatryk.liftapp.core.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import com.patrykandpatryk.liftapp.core.android.IsDarkModeHandler
import com.patrykandpatryk.liftapp.core.text.StringProviderImpl
import com.patrykandpatryk.liftapp.domain.android.IsDarkModePublisher
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatryk.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.text.Collator
import java.text.DecimalFormat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
internal interface CoreModule {

    @Binds fun bindStringProvider(provider: StringProviderImpl): StringProvider

    @Binds fun bindIsDarkModeReceiver(useCase: IsDarkModeHandler): IsDarkModeReceiver

    @Binds fun bindIsDarkModePublisher(useCase: IsDarkModeHandler): IsDarkModePublisher

    companion object {

        @Provides fun provideCollator(): Collator = Collator.getInstance()

        @Provides @Decimal fun provideDecimalFormat(): DecimalFormat = DecimalFormat("#.##")

        @Provides @Integer fun provideIntegerFormat(): DecimalFormat = DecimalFormat("#")

        @Provides
        fun provideCoroutineExceptionHandler(): CoroutineExceptionHandler =
            CoroutineExceptionHandler { coroutineContext, throwable ->
                Timber.e(throwable, "Uncaught exception in $coroutineContext.")
                throwable.printStackTrace()
            }

        @Provides
        fun provideViewModelScope(
            @DefaultDispatcher coroutineDispatcher: CoroutineDispatcher,
            coroutineExceptionHandler: CoroutineExceptionHandler,
        ): CoroutineScope = CoroutineScope(coroutineDispatcher + coroutineExceptionHandler)

        @Provides
        fun provideContext(application: Application): Context = application.applicationContext

        @Provides fun provideResources(context: Context): Resources = context.resources

        @Provides fun provideFilesDir(context: Context): File = context.filesDir
    }
}
