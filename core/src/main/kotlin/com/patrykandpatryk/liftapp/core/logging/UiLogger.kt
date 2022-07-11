package com.patrykandpatryk.liftapp.core.logging

import com.patrykandpatryk.liftapp.core.di.IsDebug
import com.patrykandpatryk.liftapp.domain.Constants
import com.patrykandpatryk.liftapp.domain.exception.DisplayableException
import com.patrykandpatryk.liftapp.domain.mapper.Mapper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiLogger @Inject constructor(
    @IsDebug private val isDebug: Boolean,
    private val exceptionMapper: Mapper<DisplayableException, String>,
) : Timber.Tree(), LogPublisher {

    private val events = MutableSharedFlow<UiMessage>(extraBufferCapacity = 3)

    override val messages = events.asSharedFlow()

    override fun isLoggable(tag: String?, priority: Int): Boolean =
        isDebug || tag == Constants.Logging.DISPLAYABLE_ERROR

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        when {
            t is DisplayableException -> UiMessage.SnackbarText(exceptionMapper(t))
            else -> UiMessage.SnackbarText(message)
        }.also(events::tryEmit)
    }
}
