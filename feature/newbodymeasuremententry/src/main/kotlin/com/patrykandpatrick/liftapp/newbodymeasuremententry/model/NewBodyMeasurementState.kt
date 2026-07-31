package com.patrykandpatrick.liftapp.newbodymeasuremententry.model

import androidx.compose.runtime.Stable
import com.patrykandpatrick.liftapp.core.text.LocalDateTextFieldState
import com.patrykandpatrick.liftapp.core.text.LocalTimeTextFieldState
import com.patrykandpatrick.liftapp.core.text.TextFieldState
import com.patrykandpatrick.liftapp.domain.bodymeasurement.BodyMeasurementValue
import com.patrykandpatrick.liftapp.domain.unit.ValueUnit

@Stable
data class NewBodyMeasurementState(
    val name: String,
    val inputData: InputData,
    val dateTextFieldState: LocalDateTextFieldState,
    val timeTextFieldState: LocalTimeTextFieldState,
    val is24H: Boolean,
    val unit: ValueUnit,
    val isEdit: Boolean,
) {

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
}
