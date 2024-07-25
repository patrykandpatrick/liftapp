package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementRepository
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.getValueRange
import com.patrykandpatryk.liftapp.domain.extension.toStringOrEmpty
import com.patrykandpatryk.liftapp.domain.format.FormattedDate
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.unit.GetUnitForBodyMeasurementTypeUseCase
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.domain.validation.valueInRange
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private const val LocalDateTimeKey = "LocalDateTime"

@HiltViewModel(assistedFactory = NewBodyMeasurementEntryViewModel.Factory::class)
class NewBodyMeasurementEntryViewModel(
    private val getFormattedDate: (LocalDateTime) -> FormattedDate,
    private val getBodyMeasurementWithLatestEntry: suspend () -> BodyMeasurementWithLatestEntry,
    private val getBodyMeasurementEntry: suspend () -> BodyMeasurementEntry?,
    private val upsertBodyMeasurementEntry: suspend (value: BodyMeasurementValue, time: LocalDateTime) -> Unit,
    private val textFieldStateManager: TextFieldStateManager,
    private val getUnitForBodyMeasurementType: suspend (BodyMeasurementType) -> ValueUnit,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel(), NewBodyMeasurementState {
    private val dateTime = savedStateHandle.getStateFlow(LocalDateTimeKey, LocalDateTime.now())

    override val name: MutableState<String> = mutableStateOf("")

    override val inputData: MutableState<NewBodyMeasurementState.InputData?> = mutableStateOf(null)

    override val is24H: MutableState<Boolean> = mutableStateOf(false)

    override val formattedDate: StateFlow<FormattedDate> = dateTime
        .map { getFormattedDate(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), getFormattedDate(dateTime.value))

    override val entrySaved: MutableState<Boolean> = mutableStateOf(false)

    @AssistedInject
    constructor(
        @Assisted id: Long,
        @Assisted entryId: Long?,
        formatter: Formatter,
        repository: BodyMeasurementRepository,
        textFieldStateManager: TextFieldStateManager,
        getUnitForBodyMeasurementType: GetUnitForBodyMeasurementTypeUseCase,
        savedStateHandle: SavedStateHandle,
    ) : this(
        getFormattedDate = { formatter.getFormattedDate(it) },
        getBodyMeasurementWithLatestEntry = { repository.getBodyMeasurementWithLatestEntry(id).first() },
        getBodyMeasurementEntry = { entryId?.let { repository.getBodyMeasurementEntry(it) } },
        upsertBodyMeasurementEntry = { value, time ->
            if (entryId != null) {
                repository.updateBodyMeasurementEntry(entryId, id, value, time)
            } else {
                repository.insertBodyMeasurementEntry(id, value, time)
            }
        },
        textFieldStateManager = textFieldStateManager,
        getUnitForBodyMeasurementType = { getUnitForBodyMeasurementType(it) },
        savedStateHandle = savedStateHandle,
    )

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            val bodyMeasurement = getBodyMeasurementWithLatestEntry()
            val bodyMeasurementEntry = getBodyMeasurementEntry()

            if (bodyMeasurementEntry != null) {
                updateLocalDateTime { bodyMeasurementEntry.formattedDate.localDateTime }
            }

            name.value = bodyMeasurement.name
            inputData.value = getInputData(bodyMeasurement, bodyMeasurementEntry)
        }
    }

    private suspend fun getInputData(
        bodyMeasurement: BodyMeasurementWithLatestEntry,
        bodyMeasurementEntry: BodyMeasurementEntry?,
    ): NewBodyMeasurementState.InputData {
        val unit = bodyMeasurementEntry?.value?.unit ?: getUnitForBodyMeasurementType(bodyMeasurement.type)
        val allowedValueRange = bodyMeasurement.type.getValueRange(unit)
        val inputValue = bodyMeasurementEntry?.value ?: bodyMeasurement.latestEntry?.value

        return if (bodyMeasurement.type == BodyMeasurementType.LengthTwoSides) {
            val doubleValue = inputValue as? BodyMeasurementValue.Double
            NewBodyMeasurementState.InputData.Double(
                leftTextFieldState = textFieldStateManager.floatTextField(
                    initialValue = doubleValue?.left.toStringOrEmpty(),
                    validators = {
                        nonEmpty()
                        valueInRange(allowedValueRange)
                    }
                ),
                rightTextFieldState = textFieldStateManager.floatTextField(
                    initialValue = doubleValue?.right.toStringOrEmpty(),
                    validators = {
                        nonEmpty()
                        valueInRange(allowedValueRange)
                    }
                ),
                unit = unit
            )
        } else {
            NewBodyMeasurementState.InputData.Single(
                textFieldState = textFieldStateManager.floatTextField(
                    initialValue = (inputValue as? BodyMeasurementValue.Single)?.value.toStringOrEmpty(),
                    validators = {
                        nonEmpty()
                        valueInRange(allowedValueRange)
                    }
                ),
                unit = unit,
            )
        }
    }

    override fun setTime(hour: Int, minute: Int) {
        updateLocalDateTime { dateTime -> dateTime.withHour(hour).withMinute(minute) }
    }

    override fun setDate(year: Int, month: Int, day: Int) {
        updateLocalDateTime { dateTime -> dateTime.withYear(year).withMonth(month).withDayOfMonth(day) }
    }

    private fun updateLocalDateTime(update: (dateTime: LocalDateTime) -> LocalDateTime) {
        savedStateHandle[LocalDateTimeKey] = update(dateTime.value)
    }

    override fun save(inputData: NewBodyMeasurementState.InputData) {
        if (inputData.isInvalid.value) {
            inputData.showErrors()
            return
        }
        viewModelScope.launch {
            upsertBodyMeasurementEntry(inputData.toBodyMeasurementValue(), dateTime.value)
            entrySaved.value = true
            updateLocalDateTime { LocalDateTime.now() }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Long, entryId: Long?): NewBodyMeasurementEntryViewModel
    }
}
