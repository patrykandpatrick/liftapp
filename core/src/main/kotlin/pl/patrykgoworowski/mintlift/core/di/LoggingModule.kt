package pl.patrykgoworowski.mintlift.core.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.patrykgoworowski.mintlift.core.BuildConfig
import pl.patrykgoworowski.mintlift.core.logging.DisplayableExceptionMapper
import pl.patrykgoworowski.mintlift.core.logging.UiLogger
import pl.patrykgoworowski.mintlift.domain.exception.DisplayableException
import pl.patrykgoworowski.mintlift.domain.mapper.Mapper
import timber.log.Timber

@Module
@InstallIn(SingletonComponent::class)
interface LoggingModule {

    @Binds
    fun bindDisplayableExceptionMapper(
        mapper: DisplayableExceptionMapper,
    ): Mapper<DisplayableException, String>

    companion object {

        @IsDebug
        @Provides
        fun provideIsDebug(): Boolean = BuildConfig.DEBUG

        @Provides
        fun provideLoggingTrees(
            @IsDebug isDebug: Boolean,
            uiLogger: UiLogger,
        ): Array<Timber.Tree> =
            if (isDebug) arrayOf(Timber.DebugTree(), uiLogger)
            else arrayOf(uiLogger)
    }
}
