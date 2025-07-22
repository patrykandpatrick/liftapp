package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.navigation.data.NewBodyMeasurementRouteData
import com.patrykandpatryk.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatryk.liftapp.core.text.TextFieldStateManager
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.GetBodyMeasurementWithLatestEntryUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.UpsertBodyMeasurementUseCase
import com.patrykandpatryk.liftapp.domain.bodymeasurement.getValueRange
import com.patrykandpatryk.liftapp.domain.bodymeasurement.invoke
import com.patrykandpatryk.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatryk.liftapp.domain.extension.toStringOrEmpty
import com.patrykandpatryk.liftapp.domain.format.Formatter
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatryk.liftapp.domain.text.StringProvider
import com.patrykandpatryk.liftapp.domain.unit.GetUnitForBodyMeasurementTypeUseCase
import com.patrykandpatryk.liftapp.domain.unit.UnitConverter
import com.patrykandpatryk.liftapp.domain.validation.nonEmpty
import com.patrykandpatryk.liftapp.domain.validation.valueInRange
import com.patrykandpatryk.liftapp.newbodymeasuremententry.model.Action
import com.patrykandpatryk.liftapp.newbodymeasuremententry.model.NewBodyMeasurementState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@HiltViewModel
class NewBodyMeasurementEntryViewModel
@Inject
constructor(
    private val routeData: NewBodyMeasurementRouteData,
    @param:PreferenceQualifier.Is24H private val is24H: Flow<Boolean>,
    private val formatter: Formatter,
    private val upsertBodyMeasurementUseCase: UpsertBodyMeasurementUseCase,
    private val textFieldStateManager: TextFieldStateManager,
    private val getUnitForBodyMeasurementType: GetUnitForBodyMeasurementTypeUseCase,
    private val navigationCommander: NavigationCommander,
    private val unitConverter: UnitConverter,
    getBodyMeasurementWithLatestEntryUseCase: GetBodyMeasurementWithLatestEntryUseCase,
    getBodyMeasurementEntryUseCase: GetBodyMeasurementEntryUseCase,
    stringProvider: StringProvider,
) : ViewModel() {

    private val dateTextFieldState =
        textFieldStateManager.localDateField(
            formatter = DateTimeFormatter.ofPattern(stringProvider.dateFormatEdit)
        )

    private val timeTextFieldState = flow {
        val formatter = formatter.getLocalTimeFormatter()
        emit(textFieldStateManager.localTimeField(formatter))
    }

    val state: StateFlow<Loadable<NewBodyMeasurementState>> =
        combine(
                is24H,
                timeTextFieldState,
                getBodyMeasurementWithLatestEntryUseCase(routeData.bodyMeasurementID),
                getBodyMeasurementEntryUseCase(routeData.bodyMeasurementEntryID),
            ) { is24H, timeTextFieldState, measurementWithLatestEntry, measurementEntry ->
                if (measurementEntry != null) {
                    dateTextFieldState.updateValue(
                        measurementEntry.formattedDate.localDateTime.toLocalDate()
                    )
                    timeTextFieldState.updateValue(
                        measurementEntry.formattedDate.localDateTime.toLocalTime()
                    )
                }
                NewBodyMeasurementState(
                    name = measurementWithLatestEntry.name,
                    inputData = getInputData(measurementWithLatestEntry, measurementEntry),
                    dateTextFieldState = dateTextFieldState,
                    timeTextFieldState = timeTextFieldState,
                    is24H = is24H,
                    unit = getUnitForBodyMeasurementType(measurementWithLatestEntry.type),
                    isEdit = measurementEntry != null,
                )
            }
            .toLoadableStateFlow(viewModelScope)

    private suspend fun getInputData(
        bodyMeasurement: BodyMeasurementWithLatestEntry,
        bodyMeasurementEntry: BodyMeasurementEntry?,
    ): NewBodyMeasurementState.InputData {
        val currentUnit =
            bodyMeasurementEntry?.value?.unit ?: getUnitForBodyMeasurementType(bodyMeasurement.type)
        val allowedValueRange = bodyMeasurement.type.getValueRange(currentUnit)
        val inputValue = bodyMeasurementEntry?.value ?: bodyMeasurement.latestEntry?.value

        return if (bodyMeasurement.type == BodyMeasurementType.LengthTwoSides) {
            val doubleValue = inputValue as? BodyMeasurementValue.DoubleValue
            NewBodyMeasurementState.InputData.DoubleValue(
                leftTextFieldState =
                    textFieldStateManager.doubleTextField(
                        initialValue =
                            doubleValue
                                ?.let { (value, _, unit) ->
                                    unitConverter.convertToPreferredUnit(unit, value)
                                }
                                .toStringOrEmpty(),
                        validators = {
                            nonEmpty()
                            valueInRange(allowedValueRange)
                        },
                    ),
                rightTextFieldState =
                    textFieldStateManager.doubleTextField(
                        initialValue =
                            doubleValue
                                ?.let { (_, value, unit) ->
                                    unitConverter.convertToPreferredUnit(unit, value)
                                }
                                .toStringOrEmpty(),
                        validators = {
                            nonEmpty()
                            valueInRange(allowedValueRange)
                        },
                    ),
                unit = currentUnit,
            )
        } else {
            NewBodyMeasurementState.InputData.SingleValue(
                textFieldState =
                    textFieldStateManager.doubleTextField(
                        initialValue =
                            (inputValue as? BodyMeasurementValue.SingleValue)
                                ?.let { (value, unit) ->
                                    unitConverter.convertToPreferredUnit(unit, value)
                                }
                                .toStringOrEmpty(),
                        validators = {
                            nonEmpty()
                            valueInRange(allowedValueRange)
                        },
                    ),
                unit = currentUnit,
            )
        }
    }

    fun onAction(action: Action) {
        when (action) {
            Action.PopBackStack -> popBackStack()
            is Action.Save -> save(action.state)
        }
    }

    private fun popBackStack() {
        viewModelScope.launch { navigationCommander.popBackStack() }
    }

    private fun save(state: NewBodyMeasurementState) {
        if (state.inputData.isInvalid()) return
        viewModelScope.launch {
            upsertBodyMeasurementUseCase(
                bodyMeasurementID = routeData.bodyMeasurementID,
                entryID = routeData.bodyMeasurementEntryID,
                value = state.inputData.toBodyMeasurementValue(),
                time = state.dateTextFieldState.value.atTime(state.timeTextFieldState.value),
            )
            navigationCommander.popBackStack()
        }
    }
}
