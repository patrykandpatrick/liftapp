package com.patrykandpatryk.liftapp.newbodymeasuremententry.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.stringResourceId
import com.patrykandpatryk.liftapp.core.model.Unfold
import com.patrykandpatryk.liftapp.core.model.valueOrNull
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.text.updateValueBy
import com.patrykandpatryk.liftapp.core.ui.BottomAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBarDefaults
import com.patrykandpatryk.liftapp.core.ui.input.DateInput
import com.patrykandpatryk.liftapp.core.ui.input.NumberInput
import com.patrykandpatryk.liftapp.core.ui.input.TimeInput
import com.patrykandpatryk.liftapp.domain.Constants.Input.Increment
import com.patrykandpatryk.liftapp.domain.model.Loadable
import com.patrykandpatryk.liftapp.domain.model.toLoadable
import com.patrykandpatryk.liftapp.domain.unit.MassUnit
import com.patrykandpatryk.liftapp.domain.unit.ShortDistanceUnit
import com.patrykandpatryk.liftapp.domain.unit.ValueUnit
import com.patrykandpatryk.liftapp.newbodymeasuremententry.model.Action
import com.patrykandpatryk.liftapp.newbodymeasuremententry.model.NewBodyMeasurementState
import java.time.format.DateTimeFormatter

@Composable
fun NewBodyMeasurementEntryScreen(modifier: Modifier = Modifier) {
    val viewModel: NewBodyMeasurementEntryViewModel = hiltViewModel()

    NewBodyMeasurementEntryScreen(
        loadableState = viewModel.state.collectAsStateWithLifecycle().value,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun NewBodyMeasurementEntryScreen(
    loadableState: Loadable<NewBodyMeasurementState>,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppScaffold(
        topBar = {
            CompactTopAppBar(
                title = {
                    Text(
                        text =
                            if (loadableState.valueOrNull()?.isEdit == true) {
                                stringResource(id = R.string.route_edit_body_measurement)
                            } else {
                                stringResource(id = R.string.route_new_body_measurement)
                            }
                    )
                },
                navigationIcon = {
                    CompactTopAppBarDefaults.IconButton(
                        imageVector = LiftAppIcons.Cross,
                        contentDescription = stringResource(id = R.string.action_close),
                        onClick = { onAction(Action.PopBackStack) },
                    )
                },
            )
        },
        bottomBar = {
            loadableState.valueOrNull()?.also { state ->
                BottomAppBar.Save(onClick = { onAction(Action.Save(state)) })
            }
        },
        modifier = modifier,
    ) { contentPadding ->
        loadableState.Unfold(
            modifier =
                Modifier.imePadding()
                    .padding(contentPadding)
                    .padding(horizontal = dimens.padding.contentHorizontal)
                    .padding(top = dimens.padding.itemVertical)
        ) { state ->
            Content(state, onAction)
        }
    }
}

@Composable
private fun Content(
    state: NewBodyMeasurementState,
    onAction: (Action) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(dimens.grid.minCellWidthLarge),
        horizontalArrangement = Arrangement.spacedBy(dimens.padding.itemHorizontal),
        verticalArrangement = Arrangement.spacedBy(dimens.padding.itemVertical),
        modifier = modifier,
    ) {
        state.inputData.forEachTextField { textFieldState, hintRes, isLast ->
            item {
                NumberInput(
                    textFieldState = textFieldState,
                    unit = state.unit,
                    isLast = isLast,
                    onSave = { onAction(Action.Save(state)) },
                    hint = stringResource(hintRes),
                )
            }
        }

        item {
            DateInput(
                date = state.dateTextFieldState,
                label = stringResource(id = R.string.picker_date),
            )
        }

        item {
            TimeInput(
                time = state.timeTextFieldState,
                is24H = state.is24H,
                label = stringResource(id = R.string.picker_time),
            )
        }
    }
}

@Composable
private fun NumberInput(
    textFieldState: TextFieldState<Double>,
    unit: ValueUnit,
    isLast: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    hint: String = stringResource(id = R.string.value),
) {
    NumberInput(
        modifier = modifier,
        textFieldState = textFieldState,
        hint = hint,
        suffix = stringResource(id = unit.stringResourceId),
        onMinusClick = { long -> textFieldState.updateValueBy(-Increment.getBodyWeight(long)) },
        onPlusClick = { long -> textFieldState.updateValueBy(Increment.getBodyWeight(long)) },
        keyboardActions = KeyboardActions(onDone = { onSave() }),
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
            ),
    )
}

inline fun NewBodyMeasurementState.InputData.forEachTextField(
    action: (textFieldState: TextFieldState<Double>, hintRes: Int, isLast: Boolean) -> Unit
) {
    when (this) {
        is NewBodyMeasurementState.InputData.DoubleValue -> {
            action(leftTextFieldState, R.string.body_measurement_left, false)
            action(rightTextFieldState, R.string.body_measurement_right, true)
        }

        is NewBodyMeasurementState.InputData.SingleValue -> {
            action(textFieldState, R.string.value, true)
        }
    }
}

@MultiDevicePreview
@Composable
fun NewBodyMeasurementEntryScreenWeightPreview() {
    PreviewTheme {
        val stringProvider = PreviewResource.stringProvider
        val savedStateHandle = SavedStateHandle()
        val textFieldStateManager = PreviewResource.textFieldStateManager(savedStateHandle)

        NewBodyMeasurementEntryScreen(
            loadableState =
                NewBodyMeasurementState(
                        name = "Weight",
                        inputData =
                            NewBodyMeasurementState.InputData.SingleValue(
                                textFieldStateManager.doubleTextField(),
                                MassUnit.Kilograms,
                            ),
                        dateTextFieldState =
                            textFieldStateManager.localDateField(
                                DateTimeFormatter.ofPattern(stringProvider.dateWeekdayDayMonthYear)
                            ),
                        timeTextFieldState =
                            textFieldStateManager.localTimeField(
                                formatter = DateTimeFormatter.ofPattern("HH:mm")
                            ),
                        is24H = true,
                        unit = MassUnit.Kilograms,
                        isEdit = false,
                    )
                    .toLoadable(),
            onAction = {},
        )
    }
}

@MultiDevicePreview
@Composable
fun NewBodyMeasurementEntryScreenForearmsPreview() {
    PreviewTheme {
        val stringProvider = PreviewResource.stringProvider
        val savedStateHandle = SavedStateHandle()
        val textFieldStateManager = PreviewResource.textFieldStateManager(savedStateHandle)

        NewBodyMeasurementEntryScreen(
            loadableState =
                NewBodyMeasurementState(
                        name = "Forearm Circumference",
                        inputData =
                            NewBodyMeasurementState.InputData.DoubleValue(
                                textFieldStateManager.doubleTextField(),
                                textFieldStateManager.doubleTextField(),
                                ShortDistanceUnit.Centimeter,
                            ),
                        dateTextFieldState =
                            textFieldStateManager.localDateField(
                                DateTimeFormatter.ofPattern(stringProvider.dateWeekdayDayMonthYear)
                            ),
                        timeTextFieldState =
                            textFieldStateManager.localTimeField(
                                formatter = DateTimeFormatter.ofPattern("hh:mm a")
                            ),
                        is24H = false,
                        unit = ShortDistanceUnit.Centimeter,
                        isEdit = false,
                    )
                    .toLoadable(),
            onAction = {},
        )
    }
}
