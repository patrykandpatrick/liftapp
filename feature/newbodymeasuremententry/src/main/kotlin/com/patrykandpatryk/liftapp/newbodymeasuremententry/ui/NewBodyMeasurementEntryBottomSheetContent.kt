package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.collectInComposable
import com.patrykandpatryk.liftapp.core.extension.getMessageTextOrNull
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.state.onClick
import com.patrykandpatryk.liftapp.core.ui.DialogTopBar
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.input.DatePicker
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.core.ui.input.TimePicker
import com.patrykandpatryk.liftapp.core.ui.input.rememberDatePickerState
import com.patrykandpatryk.liftapp.core.ui.input.rememberTimePickerState
import com.patrykandpatryk.liftapp.core.ui.theme.BottomSheetShape
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.Constants.Input.INCREMENT_LONG
import com.patrykandpatryk.liftapp.domain.Constants.Input.INCREMENT_SHORT
import com.patrykandpatryk.liftapp.domain.format.FormattedDate
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.validation.Validatable

@Composable
fun NewBodyMeasurementEntryBottomSheet(
    bodyMeasurementId: Long,
    bodyMeasurementEntryId: Long?,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<NewBodyMeasurementEntryViewModel, NewBodyMeasurementEntryViewModel.Factory>(
        creationCallback = { factory -> factory.create(bodyMeasurementId, bodyMeasurementEntryId) },
    )
    val bodyModel by viewModel.state.collectAsState()

    BackHandler(enabled = true, onBack = onDismissRequest)

    viewModel.events.collectInComposable { event ->
        when (event) {
            Event.EntrySaved -> onDismissRequest()
        }
    }

    NewBodyMeasurementEntryBottomSheetContent(
        state = bodyModel,
        onIntent = viewModel::handleIntent,
        onCloseClick = onDismissRequest,
        modifier = modifier,
    )
}

@Composable
private fun NewBodyMeasurementEntryBottomSheetContent(
    state: ScreenState,
    onIntent: (Intent) -> Unit,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = LocalDimens.current

    val timePickerState = rememberTimePickerState(
        is24h = state.is24H,
        hour = state.formattedDate.hour,
        minute = state.formattedDate.minute,
    )

    val datePickerState = rememberDatePickerState(
        millis = state.formattedDate.millis,
    )

    TimePicker(state = timePickerState) { hour, minute ->
        onIntent(Intent.SetTime(hour, minute))
    }

    DatePicker(state = datePickerState) { year, month, day ->
        onIntent(Intent.SetDate(year, month, day))
    }

    Column(
        modifier = modifier
            .navigationBarsPadding()
            .padding(vertical = dimens.padding.contentVertical),
    ) {
        DialogTopBar(
            title = state.name,
            onCloseClick = onCloseClick,
        )

        Column(
            modifier = modifier
                .navigationBarsPadding()
                .padding(horizontal = dimens.padding.contentHorizontal)
                .padding(top = dimens.padding.itemVertical),
            verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVertical),
        ) {
            state.values.forEachIndexed { index, validatable ->

                NumberInput(
                    screenState = state,
                    index = index,
                    validatable = validatable,
                    actionHandler = onIntent,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(space = dimens.padding.itemHorizontal),
            ) {
                val dateInteractionSource = remember { MutableInteractionSource() }
                    .onClick(datePickerState::show)

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    value = state.formattedDate.dateShort,
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.picker_date)) },
                    interactionSource = dateInteractionSource,
                )

                val timeInteractionSource = remember { MutableInteractionSource() }
                    .onClick { timePickerState.isShowing = true }

                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    readOnly = true,
                    value = state.formattedDate.timeShort,
                    onValueChange = {},
                    label = { Text(text = stringResource(id = R.string.picker_time)) },
                    interactionSource = timeInteractionSource,
                )
            }

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onIntent(Intent.Save) },
            ) {
                Text(
                    text = stringResource(id = R.string.action_save),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
private fun NumberInput(
    screenState: ScreenState,
    index: Int,
    validatable: Validatable<String>,
    actionHandler: (Intent) -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberInput(
        modifier = modifier,
        value = validatable.value,
        onValueChange = { value ->
            actionHandler(Intent.SetValue(index = index, value = value))
        },
        hint = stringResource(id = R.string.value),
        suffix = stringResource(id = screenState.unit.stringResourceId),
        onMinusClick = { long ->
            actionHandler(
                Intent.IncrementValue(
                    index = index,
                    incrementBy = -getIncrement(long),
                ),
            )
        },
        onPlusClick = { long ->
            actionHandler(
                Intent.IncrementValue(
                    index = index,
                    incrementBy = getIncrement(long),
                ),
            )
        },
        keyboardActions = KeyboardActions(
            onDone = {
                actionHandler(Intent.Save)
            },
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = if (index == screenState.values.lastIndex) ImeAction.Done else ImeAction.Next,
        ),
        isError = screenState.showErrors,
        errorMessage = validatable.getMessageTextOrNull(),
    )
}

private fun getIncrement(long: Boolean) =
    if (long) INCREMENT_LONG else INCREMENT_SHORT

@MultiDevicePreview
@Composable
fun NewBodyMeasurementEntryBottomSheetContentPreview() {
    LiftAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                shape = BottomSheetShape,
                shadowElevation = 8.dp,
            ) {
                NewBodyMeasurementEntryBottomSheetContent(
                    state = ScreenState.Insert(
                        name = "Weight",
                        values = List(size = 1) { Validatable.Valid("65") },
                        formattedDate = FormattedDate.Empty,
                        is24H = false,
                        unit = MassUnit.Kilograms,
                    ),
                    onIntent = {},
                    onCloseClick = {},
                )
            }
        }
    }
}
