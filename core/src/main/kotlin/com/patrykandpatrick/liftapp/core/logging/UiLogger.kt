package com.patrykandpatrick.liftapp.core.logging

import android.util.Log
import com.patrykandpatrick.liftapp.core.di.IsDebug
import com.patrykandpatrick.liftapp.domain.Constants
import com.patrykandpatrick.liftapp.domain.di.DefaultDispatcher
import com.patrykandpatrick.liftapp.domain.exception.DisplayableException
import com.patrykandpatrick.liftapp.domain.mapper.Mapper
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@Singleton
class UiLogger
@Inject
constructor(
    @IsDebug private val isDebug: Boolean,
    private val exceptionMapper: Mapper<DisplayableException, String>,
    @DefaultDispatcher dispatcher: CoroutineDispatcher,
) : Timber.Tree(), LogPublisher {

    private val scope = CoroutineScope(dispatcher)

    private val events = MutableSharedFlow<UiMessage>(extraBufferCapacity = 3)

    override val messages = events.asSharedFlow()

    override fun isLoggable(tag: String?, priority: Int): Boolean =
        isDebug && priority >= Log.INFO || tag == Constants.Logging.DISPLAYABLE_ERROR

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        scope.launch {
            when (t) {
                is DisplayableException -> UiMessage.SnackbarText(exceptionMapper(t))
                else -> UiMessage.SnackbarText(message)
            }.also { events.emit(it) }
        }
    }
}
