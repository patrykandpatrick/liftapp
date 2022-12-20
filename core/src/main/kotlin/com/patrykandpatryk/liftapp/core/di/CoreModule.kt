package com.patrykandpatryk.liftapp.core.di

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Build
import com.patrykandpatryk.liftapp.core.android.IsDarkModeHandler
import com.patrykandpatryk.liftapp.core.base64.AndroidBase64
import com.patrykandpatryk.liftapp.core.mapper.BodyEntriesToChartEntriesMapper
import com.patrykandpatryk.liftapp.core.text.StringProviderImpl
import com.patrykandpatryk.liftapp.core.ui.name.NameResolverImpl
import com.patrykandpatryk.liftapp.core.validation.HigherThanZero
import com.patrykandpatryk.liftapp.core.validation.HigherThanZeroValidator
import com.patrykandpatryk.liftapp.core.validation.Name
import com.patrykandpatryk.liftapp.core.validation.NameValidator
import com.patrykandpatryk.liftapp.domain.android.IsDarkModePublisher
import com.patrykandpatryk.liftapp.domain.android.IsDarkModeReceiver
import com.patrykandpatryk.liftapp.domain.base64.Base64
import com.patrykandpatryk.liftapp.domain.base64.JavaBase64
import com.patrykandpatryk.liftapp.domain.body.BodyEntry
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import com.patrykandpatryk.liftapp.domain.model.NameResolver
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.vico.core.entry.ChartEntry
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
import android.os.Build.VERSION_CODES.O as Oreo

@Module
@InstallIn(SingletonComponent::class)
internal interface CoreModule {

    @Binds
    fun bindNameSolver(solver: NameResolverImpl): NameResolver

    @Binds
    fun bindBodyEntriesToChartEntriesMapper(
        mapper: BodyEntriesToChartEntriesMapper,
    ): Mapper<List<BodyEntry>, List<List<ChartEntry>>>

    @Binds
    fun bindStringProvider(provider: StringProviderImpl): StringProvider

    @Binds
    @Name
    fun bindNameValidator(validator: NameValidator): Validator<String>

    @Binds
    @HigherThanZero
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
        fun provideBase64(
            javaBase64: JavaBase64,
            androidBase64: AndroidBase64,
        ): Base64 =
            if (Build.VERSION.SDK_INT >= Oreo) {
                javaBase64
            } else {
                androidBase64
            }

        @Provides
        fun provideFilesDir(context: Context): File = context.filesDir
    }
}
