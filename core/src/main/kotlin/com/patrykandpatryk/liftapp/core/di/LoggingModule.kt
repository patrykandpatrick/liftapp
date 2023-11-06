package com.patrykandpatryk.liftapp.core.di

import com.patrykandpatryk.liftapp.core.BuildConfig
import com.patrykandpatryk.liftapp.core.logging.DisplayableExceptionMapper
import com.patrykandpatryk.liftapp.core.logging.UiLogger
import com.patrykandpatryk.liftapp.domain.exception.DisplayableException
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
            if (isDebug) {
                arrayOf(Timber.DebugTree(), uiLogger)
            } else {
                arrayOf(uiLogger)
            }
    }
}
