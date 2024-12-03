package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementWithLatestEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.UpsertBodyMeasurementUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.getValueRange
import com.patrykandpatryk.liftapp.domain.extension.toStringOrEmpty
import com.patrykandpatryk.liftapp.domain.format.FormattedDate
import com.patrykandpatryk.liftapp.domain.format.GetFormattedDateUseCase
import com.patrykandpatryk.liftapp.domain.unit.GetUnitForBodyMeasurementTypeUseCase
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.domain.validation.valueInRange
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private const val LocalDateTimeKey = "LocalDateTime"

@Stable
class NewBodyMeasurementState(
    private val getFormattedDate: suspend (LocalDateTime) -> FormattedDate,
    private val getBodyMeasurementWithLatestEntry: suspend () -> BodyMeasurementWithLatestEntry,
    private val getBodyMeasurementEntry: suspend () -> BodyMeasurementEntry?,
    private val upsertBodyMeasurementEntry: suspend (value: BodyMeasurementValue, time: LocalDateTime) -> Unit,
    private val getUnitForBodyMeasurementType: suspend (BodyMeasurementType) -> ValueUnit,
    private val textFieldStateManager: TextFieldStateManager,
    private val coroutineScope: CoroutineScope,
    private val savedStateHandle: SavedStateHandle,
) {
    private val dateTime = savedStateHandle.getStateFlow(LocalDateTimeKey, LocalDateTime.now())

    private val _name: MutableState<String> = mutableStateOf("")

    private val _inputData: MutableState<InputData?> = mutableStateOf(null)

    private val _is24H: MutableState<Boolean> = mutableStateOf(false)

    private val _entrySaved: MutableState<Boolean> = mutableStateOf(false)

    val name: State<String> = _name

    val inputData: State<InputData?> = _inputData

    val is24H: State<Boolean> = _is24H

    val formattedDate: StateFlow<FormattedDate> = dateTime
        .map { getFormattedDate(it) }
        .stateIn(coroutineScope, SharingStarted.Lazily, FormattedDate.Empty)

    val entrySaved: State<Boolean> = _entrySaved

    @AssistedInject
    constructor(
        @Assisted id: Long,
        @Assisted entryId: Long?,
        @Assisted coroutineScope: CoroutineScope,
        getFormattedDateUseCase: GetFormattedDateUseCase,
        getBodyMeasurementWithLatestEntryUseCaseFactory: GetBodyMeasurementWithLatestEntryUseCase.Factory,
        getBodyMeasurementEntryUseCaseFactory: GetBodyMeasurementEntryUseCase.Factory,
        upsertBodyMeasurementUseCaseFactory: UpsertBodyMeasurementUseCase.Factory,
        textFieldStateManager: TextFieldStateManager,
        getUnitForBodyMeasurementType: GetUnitForBodyMeasurementTypeUseCase,
        savedStateHandle: SavedStateHandle,
    ) : this(
        getFormattedDateUseCase::invoke,
        getBodyMeasurementWithLatestEntryUseCaseFactory.create(id)::invoke,
        { if (entryId != null) getBodyMeasurementEntryUseCaseFactory.create(entryId).invoke() else null },
        upsertBodyMeasurementUseCaseFactory.create(id, entryId)::invoke,
        getUnitForBodyMeasurementType::invoke,
        textFieldStateManager,
        coroutineScope,
        savedStateHandle,
    )

    init {
        loadData()
    }

    private fun loadData() {
        coroutineScope.launch {
            val bodyMeasurement = getBodyMeasurementWithLatestEntry()
            val bodyMeasurementEntry = getBodyMeasurementEntry()

            if (bodyMeasurementEntry != null) {
                updateLocalDateTime { bodyMeasurementEntry.formattedDate.localDateTime }
            }

            _name.value = bodyMeasurement.name
            _inputData.value = getInputData(bodyMeasurement, bodyMeasurementEntry)
        }
    }

    private suspend fun getInputData(
        bodyMeasurement: BodyMeasurementWithLatestEntry,
        bodyMeasurementEntry: BodyMeasurementEntry?,
    ): InputData {
        val unit = bodyMeasurementEntry?.value?.unit ?: getUnitForBodyMeasurementType(bodyMeasurement.type)
        val allowedValueRange = bodyMeasurement.type.getValueRange(unit)
        val inputValue = bodyMeasurementEntry?.value ?: bodyMeasurement.latestEntry?.value

        return if (bodyMeasurement.type == BodyMeasurementType.LengthTwoSides) {
            val doubleValue = inputValue as? BodyMeasurementValue.DoubleValue
            InputData.DoubleValue(
                leftTextFieldState = textFieldStateManager.doubleTextField(
                    initialValue = doubleValue?.left.toStringOrEmpty(),
                    validators = {
                        nonEmpty()
                        valueInRange(allowedValueRange)
                    }
                ),
                rightTextFieldState = textFieldStateManager.doubleTextField(
                    initialValue = doubleValue?.right.toStringOrEmpty(),
                    validators = {
                        nonEmpty()
                        valueInRange(allowedValueRange)
                    }
                ),
                unit = unit
            )
        } else {
            InputData.SingleValue(
                textFieldState = textFieldStateManager.doubleTextField(
                    initialValue = (inputValue as? BodyMeasurementValue.SingleValue)?.value.toStringOrEmpty(),
                    validators = {
                        nonEmpty()
                        valueInRange(allowedValueRange)
                    }
                ),
                unit = unit,
            )
        }
    }

    fun setTime(hour: Int, minute: Int) {
        updateLocalDateTime { dateTime -> dateTime.withHour(hour).withMinute(minute) }
    }

    fun setDate(year: Int, month: Int, day: Int) {
        updateLocalDateTime { dateTime -> dateTime.withYear(year).withMonth(month).withDayOfMonth(day) }
    }

    private fun updateLocalDateTime(update: (dateTime: LocalDateTime) -> LocalDateTime) {
        savedStateHandle[LocalDateTimeKey] = update(dateTime.value)
    }

    fun getDateTime(): LocalDateTime = dateTime.value

    fun save(inputData: InputData) {
        if (inputData.isInvalid()) {
            inputData.showErrors()
            return
        }
        coroutineScope.launch {
            upsertBodyMeasurementEntry(inputData.toBodyMeasurementValue(), dateTime.value)
            _entrySaved.value = true
            updateLocalDateTime { LocalDateTime.now() }
        }
    }

    @Stable
    sealed interface InputData {
        val unit: ValueUnit

        fun isInvalid(): Boolean

        fun showErrors()

        fun toBodyMeasurementValue(): BodyMeasurementValue

        @Stable
        data class SingleValue(
            val textFieldState: TextFieldState<Double>,
            override val unit: ValueUnit,
        ) : InputData {
            override fun isInvalid(): Boolean {
                textFieldState.updateErrorMessages()
                return textFieldState.hasError
            }

            override fun showErrors() {
                textFieldState.updateErrorMessages()
            }

            override fun toBodyMeasurementValue(): BodyMeasurementValue =
                BodyMeasurementValue.SingleValue(textFieldState.value, unit)
        }

        @Stable
        data class DoubleValue(
            val leftTextFieldState: TextFieldState<Double>,
            val rightTextFieldState: TextFieldState<Double>,
            override val unit: ValueUnit,
        ) : InputData {
            override fun isInvalid(): Boolean {
                leftTextFieldState.updateErrorMessages()
                rightTextFieldState.updateErrorMessages()
                return leftTextFieldState.hasError || rightTextFieldState.hasError
            }

            override fun showErrors() {
                leftTextFieldState.updateErrorMessages()
                rightTextFieldState.updateErrorMessages()
            }

            override fun toBodyMeasurementValue(): BodyMeasurementValue =
                BodyMeasurementValue.DoubleValue(
                    leftTextFieldState.value,
                    rightTextFieldState.value,
                    unit,
                )
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long, entryId: Long?, coroutineScope: CoroutineScope): NewBodyMeasurementState
    }
}

inline fun NewBodyMeasurementState.InputData.forEachTextField(action: (textFieldState: TextFieldState<Double>, isLast: Boolean) -> Unit) {
    when (this) {
        is NewBodyMeasurementState.InputData.DoubleValue -> {
            action(leftTextFieldState, false)
            action(rightTextFieldState, true)
        }

        is NewBodyMeasurementState.InputData.SingleValue -> {
            action(textFieldState, true)
        }
    }
}