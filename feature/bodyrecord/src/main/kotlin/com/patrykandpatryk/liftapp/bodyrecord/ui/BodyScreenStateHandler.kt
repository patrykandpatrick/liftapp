package com.patrykandpatryk.liftapp.bodyrecord.ui

import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.bodyrecord.di.BodyId
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.validation.HigherThanZero
import com.patrykandpatryk.liftapp.domain.date.day
import com.patrykandpatryk.liftapp.domain.date.hour
import com.patrykandpatryk.liftapp.domain.date.minute
import com.patrykandpatryk.liftapp.domain.date.month
import com.patrykandpatryk.liftapp.domain.date.year
import com.patrykandpatryk.liftapp.domain.di.MainDispatcher
import com.patrykandpatryk.liftapp.domain.extension.set
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.format.Formatter.NumberFormat
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.message.LocalizableMessage
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.domain.validation.map
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val SCREEN_STATE_KEY = "screen_state"

internal class BodyScreenStateHandler @Inject constructor(
    @BodyId val id: Long,
    private val formatter: Formatter,
    exceptionHandler: CoroutineExceptionHandler,
    private val repository: BodyRepository,
    private val savedStateHandle: SavedStateHandle,
    @HigherThanZero private val validator: Validator<Float>,
    @MainDispatcher.Immediate private val dispatcher: CoroutineDispatcher,
) : ScreenStateHandler<ScreenState, Intent> {

    private val scope = CoroutineScope(dispatcher + SupervisorJob() + exceptionHandler)

    override val state: StateFlow<ScreenState> = savedStateHandle
        .getStateFlow<ScreenState>(SCREEN_STATE_KEY, ScreenState.Loading)

    private val calendar = Calendar.getInstance()

    init {
        if (savedStateHandle.get<ScreenState>(SCREEN_STATE_KEY) == ScreenState.Loading) {
            scope.launch {
                savedStateHandle[SCREEN_STATE_KEY] = getInitialState(id)
            }
        }
    }

    private suspend fun getInitialState(id: Long): ScreenState {
        val body = repository.getBody(id).first()
        return ScreenState.Insert(
            name = body.name,
            values = List(size = body.type.fields) {
                Validatable.Invalid(
                    value = "",
                    message = LocalizableMessage(R.string.error_must_be_higher_than_zero),
                )
            },
            is24H = formatter.is24H(),
            formattedDate = formatter.getFormattedDate(calendar),
        )
    }

    override fun handleIntent(intent: Intent) {
        val model = state.value

        scope.launch {
            savedStateHandle[SCREEN_STATE_KEY] = when (intent) {
                is Intent.IncrementValue -> incrementValue(model, intent)
                is Intent.SetValue -> setValue(model, intent)
                is Intent.SetTime -> setTime(model, intent)
                is Intent.SetDate -> setDate(model, intent)
                is Intent.Save -> save(model)
            }
        }
    }

    private fun incrementValue(model: ScreenState, intent: Intent.IncrementValue): ScreenState {
        val newValue = model.values[intent.index].value.parse()
            .plus(intent.incrementBy)
            .let(validator::validate)
            .map { number -> formatter.formatNumber(number, NumberFormat.Decimal) }

        return model.mutate(values = model.values.set(intent.index, newValue))
    }

    private fun setValue(model: ScreenState, intent: Intent.SetValue): ScreenState {
        val newValue =
            intent.value.parse()
                .let(validator::validate)
                .map { intent.value }

        return model.mutate(values = model.values.set(intent.index, newValue))
    }

    private suspend fun setTime(model: ScreenState, intent: Intent.SetTime): ScreenState {
        calendar.hour = intent.hour
        calendar.minute = intent.minute
        return model.mutate(formattedDate = formatter.getFormattedDate(calendar))
    }

    private suspend fun setDate(model: ScreenState, intent: Intent.SetDate): ScreenState {
        calendar.year = intent.year
        calendar.month = intent.month
        calendar.day = intent.day
        return model.mutate(formattedDate = formatter.getFormattedDate(calendar))
    }

    private fun save(model: ScreenState): ScreenState {
        return if (model.values.any { it.isInvalid }) model.mutate(showErrors = true)
        else model
    }

    private fun String.parse(): Float =
        toFloatOrNull() ?: formatter.parseFloatOrZero(this)

    override fun close() {
        scope.cancel()
    }
}
