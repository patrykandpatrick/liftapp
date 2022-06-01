package pl.patrykgoworowski.mintlift.core.logging

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import pl.patrykgoworowski.mintlift.core.di.IsDebug
import pl.patrykgoworowski.mintlift.domain.Constants
import pl.patrykgoworowski.mintlift.domain.exception.DisplayableException
import pl.patrykgoworowski.mintlift.domain.mapper.Mapper
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiLogger @Inject constructor(
    @IsDebug private val isDebug: Boolean,
    private val exceptionMapper: Mapper<DisplayableException, String>,
) : Timber.Tree() {

    private val events = MutableSharedFlow<UiMessage>(extraBufferCapacity = 3)

    val messages = events.asSharedFlow()

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
