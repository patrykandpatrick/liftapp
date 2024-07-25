package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatryk.liftapp.domain.format.FormattedDate
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import kotlinx.coroutines.flow.StateFlow

@Stable
interface NewBodyMeasurementState {
    val name: State<String>
    val inputData: State<InputData?>
    val is24H: State<Boolean>
    val formattedDate: StateFlow<FormattedDate>
    val entrySaved: State<Boolean>

    fun setTime(hour: Int, minute: Int)

    fun setDate(year: Int, month: Int, day: Int)

    fun save(inputData: InputData)

    @Stable
    sealed interface InputData {
        val unit: ValueUnit

        val isInvalid: State<Boolean>

        fun showErrors()

        fun toBodyMeasurementValue(): BodyMeasurementValue

        @Stable
        data class Single(
            val textFieldState: TextFieldState<Float>,
            override val unit: ValueUnit,
        ) : InputData {
            override val isInvalid = derivedStateOf { textFieldState.hasError }

            override fun showErrors() {
                textFieldState.updateErrorMessages()
            }

            override fun toBodyMeasurementValue(): BodyMeasurementValue =
                BodyMeasurementValue.Single(textFieldState.value, unit)
        }

        @Stable
        data class Double(
            val leftTextFieldState: TextFieldState<Float>,
            val rightTextFieldState: TextFieldState<Float>,
            override val unit: ValueUnit,
        ) : InputData {
            override val isInvalid = derivedStateOf { leftTextFieldState.hasError || rightTextFieldState.hasError }

            override fun showErrors() {
                leftTextFieldState.updateErrorMessages()
                rightTextFieldState.updateErrorMessages()
            }

            override fun toBodyMeasurementValue(): BodyMeasurementValue =
                BodyMeasurementValue.Double(
                    leftTextFieldState.value,
                    rightTextFieldState.value,
                    unit,
                )
        }
    }
}

inline fun NewBodyMeasurementState.InputData.forEachTextField(action: (textFieldState: TextFieldState<Float>, isLast: Boolean) -> Unit) {
    when (this) {
        is NewBodyMeasurementState.InputData.Double -> {
            action(leftTextFieldState, false)
            action(rightTextFieldState, true)
        }
        is NewBodyMeasurementState.InputData.Single -> {
            action(textFieldState, true)
        }
    }
}
