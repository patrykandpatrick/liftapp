package com.patrykandpatryk.liftapp.bodyrecord.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.patrykandpatryk.liftapp.bodyrecord.di.BodyId
import com.patrykandpatryk.liftapp.core.di.Decimal
import com.patrykandpatryk.liftapp.core.validation.HigherThanZero
import com.patrykandpatryk.liftapp.domain.extension.parseFloatOrNull
import com.patrykandpatryk.liftapp.domain.extension.set
import com.patrykandpatryk.liftapp.domain.validation.Validator
import com.patrykandpatryk.liftapp.domain.validation.map
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.DecimalFormat
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val MODEL_KEY = "model"

@HiltViewModel
class InsertBodyRecordViewModel @Inject constructor(
    @BodyId val id: Long,
    private val getBodyModel: GetBodyModelUseCase,
    private val savedStateHandle: SavedStateHandle,
    @Decimal private val decimalFormat: DecimalFormat,
    @HigherThanZero private val validator: Validator<Float>,
) : ViewModel(), BodyActionHandler {

    val bodyModel: StateFlow<BodyModel> = savedStateHandle.getStateFlow(MODEL_KEY, BodyModel.Loading)

    init {
        if (savedStateHandle.get<BodyModel>(MODEL_KEY) == BodyModel.Loading) {
            viewModelScope.launch {
                savedStateHandle[MODEL_KEY] = getBodyModel(id)
            }
        }
    }

    override fun invoke(action: BodyAction) {
        val model = bodyModel.value
        val values = model.values

        savedStateHandle[MODEL_KEY] = when (action) {
            is BodyAction.IncrementValue -> {
                val newValue = (decimalFormat.parseFloatOrNull(model.values[action.index].value) ?: 0f)
                    .plus(action.incrementBy)
                    .let(validator::validate)
                    .map(decimalFormat::format)

                model.mutate(values = values.set(action.index, newValue))
            }
            is BodyAction.SetValue -> {
                val newValue = decimalFormat.parseFloatOrNull(action.value)
                    ?.let(validator::validate)
                    ?.map { action.value }
                    ?: return

                model.mutate(values = values.set(action.index, newValue))
            }
            is BodyAction.Save -> {
                if (values.any { it.isInvalid }) model.mutate(showErrors = true)
                else model
            }
        }
    }
}
