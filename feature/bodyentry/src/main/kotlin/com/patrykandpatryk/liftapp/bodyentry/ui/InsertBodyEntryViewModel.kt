package com.patrykandpatryk.liftapp.bodyentry.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.bodyentry.di.BodyEntryId
import com.patrykandpatryk.liftapp.bodyentry.di.BodyId
import com.patrykandpatryk.liftapp.core.validation.HigherThanZero
import com.patrykandpatryk.liftapp.domain.body.BodyRepository
import com.patrykandpatryk.liftapp.domain.body.BodyValues
import com.patrykandpatryk.liftapp.domain.date.day
import com.patrykandpatryk.liftapp.domain.date.hour
import com.patrykandpatryk.liftapp.domain.date.minute
import com.patrykandpatryk.liftapp.domain.date.month
import com.patrykandpatryk.liftapp.domain.date.year
import com.patrykandpatryk.liftapp.domain.extension.set
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.state.ScreenStateHandler
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.GetUnitForBodyTypeUseCase
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import com.patrykandpatryk.liftapp.domain.validation.Validatable
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.domain.validation.map
import com.patrykandpatryk.liftapp.domain.validation.toInvalid
import com.patrykandpatryk.liftapp.domain.validation.toValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private const val SCREEN_STATE_KEY = "screen_state"

@HiltViewModel
internal class InsertBodyEntryViewModel @Inject constructor(
    @BodyId val id: Long,
    @BodyEntryId val entryId: Long,
    private val formatter: Formatter,
    private val exceptionHandler: CoroutineExceptionHandler,
    private val repository: BodyRepository,
    private val stringProvider: StringProvider,
    private val getUnitForBodyType: GetUnitForBodyTypeUseCase,
    private val savedStateHandle: SavedStateHandle,
    @HigherThanZero private val validator: Validator<Float>,
) : ViewModel(), ScreenStateHandler<ScreenState, Intent, Event> {

    override val state: StateFlow<ScreenState> = savedStateHandle
        .getStateFlow<ScreenState>(SCREEN_STATE_KEY, ScreenState.Loading)

    override val events: MutableSharedFlow<Event> = MutableSharedFlow(replay = 1)

    private val calendar = Calendar.getInstance()

    init {
        if (state.value == ScreenState.Loading) {
            viewModelScope.launch {
                savedStateHandle[SCREEN_STATE_KEY] = getInitialState(id)
            }
        }
    }

    private suspend fun getInitialState(id: Long): ScreenState {
        val body = repository.getBodyWithLatestEntry(id).first()
        val entry = repository.getEntry(entryId)
        val unit = getUnitForBodyType(body.type)

        return when (entry) {
            null -> ScreenState.Insert(
                name = body.name,
                values = body.latestEntry?.values.toValidatableStrings(body.type.fields),
                unit = unit,
                is24H = formatter.is24H(),
                formattedDate = formatter.getFormattedDate(calendar),
            )
            else -> ScreenState.Update(
                entryId = entryId,
                name = body.name,
                values = entry.values.toValidatableStrings(body.type.fields),
                unit = entry.values.unit,
                is24H = formatter.is24H(),
                formattedDate = entry.formattedDate,
            )
        }
    }

    override fun handleIntent(intent: Intent) {
        val model = state.value

        viewModelScope.launch(exceptionHandler) {
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
            .map { number -> formatter.formatNumber(number, format = Formatter.NumberFormat.Decimal) }

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

    private suspend fun save(model: ScreenState): ScreenState {
        return when {
            model.values.any { it.isInvalid } -> model.mutate(showErrors = true)
            model is ScreenState.Update -> {
                repository.updateBodyEntry(
                    entryId = model.entryId,
                    parentId = id,
                    values = model.values.toBodyValues(unit = model.unit),
                    timestamp = model.formattedDate.millis,
                )
                events.emit(Event.EntrySaved)
                model
            }
            else -> {
                repository.insertBodyEntry(
                    parentId = id,
                    values = model.values.toBodyValues(unit = model.unit),
                    timestamp = model.formattedDate.millis,
                )
                events.emit(Event.EntrySaved)
                model
            }
        }
    }

    private fun List<Validatable<String>>.toBodyValues(
        unit: ValueUnit,
    ) = when (size) {
        1 -> BodyValues.Single(
            value = get(0).value.parse(),
            unit = unit,
        )
        2 -> BodyValues.Double(
            left = get(0).value.parse(),
            right = get(1).value.parse(),
            unit = unit,
        )
        else -> error("Tried to convert $size items into `BodyValues`.")
    }

    private fun BodyValues?.toValidatableStrings(fieldCount: Int): List<Validatable<String>> = buildList {
        when (this@toValidatableStrings) {
            is BodyValues.Double -> {
                add(left.toString().toValid())
                add(right.toString().toValid())
            }
            is BodyValues.Single -> {
                add(value.toString().toValid())
            }
            null -> repeat(fieldCount) {
                add("".toInvalid(stringProvider.errorMustBeHigherThanZero))
            }
        }
    }

    private fun String.parse(): Float =
        toFloatOrNull() ?: formatter.parseFloatOrZero(this)
}
