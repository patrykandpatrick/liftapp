package com.patrykandpatrick.liftapp.newbodymeasuremententry.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatrick.liftapp.core.model.toLoadableStateFlow
import com.patrykandpatrick.liftapp.core.text.TextFieldStateManager
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementEntry
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementType
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementWithLatestEntry
import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementEntryUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.GetBodyMeasurementWithLatestEntryUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.UpsertBodyMeasurementUseCase
import com.patrykandpatrick.liftapp.domain.bodymeasurement.getValueRange
import com.patrykandpatrick.liftapp.domain.bodymeasurement.invoke
import com.patrykandpatrick.liftapp.domain.di.PreferenceQualifier
import com.patrykandpatrick.liftapp.domain.extension.toStringOrEmpty
import com.patrykandpatrick.liftapp.domain.format.Formatter
import com.patrykandpatrick.liftapp.domain.model.Loadable
import com.patrykandpatrick.liftapp.domain.navigation.NavigationCommander
import com.patrykandpatrick.liftapp.domain.text.StringProvider
import com.patrykandpatrick.liftapp.domain.unit.GetUnitForBodyMeasurementTypeUseCase
import com.patrykandpatrick.liftapp.domain.unit.UnitConverter
import com.patrykandpatrick.liftapp.domain.validation.nonEmpty
import com.patrykandpatrick.liftapp.domain.validation.valueInRange
import com.patrykandpatrick.liftapp.navigation.data.NewBodyMeasurementRouteData
import com.patrykandpatrick.liftapp.newbodymeasuremententry.model.Action
import com.patrykandpatrick.liftapp.newbodymeasuremententry.model.NewBodyMeasurementState
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
            formatter = DateTimeFormatter.ofPattern(stringProvider.dateWeekdayDayMonthYear)
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
                    dateTextFieldState.updateValue(measurementEntry.localDateTime.toLocalDate())
                    timeTextFieldState.updateValue(measurementEntry.localDateTime.toLocalTime())
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
