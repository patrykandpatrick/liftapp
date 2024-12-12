package com.patrykandpatryk.liftapp.core.ui.input

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.time.text
import com.patrykandpatryk.liftapp.core.ui.SegmentedButton
import com.patrykandpatryk.liftapp.core.ui.SupportingText
import com.patrykandpatryk.liftapp.core.ui.VerticalSegmentedButtonContainer
import com.patrykandpatryk.liftapp.core.ui.dialog.DialogButtons
import com.patrykandpatryk.liftapp.core.ui.dimens.DialogDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.extension.parseToIntOrNull
import com.patrykandpatryk.liftapp.domain.extension.toIntOrZero
import com.patrykandpatryk.liftapp.domain.time.TimeOfDay
import java.text.DecimalFormat

@Composable
fun TimePicker(
    state: TimePickerState,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onTimePicked: (hour: Int, minute: Int) -> Unit,
) {
    CompositionLocalProvider(LocalDimens provides DialogDimens) {
        if (state.isShowing) {
            Dialog(onDismissRequest = { state.isShowing = false }, properties = properties) {
                Surface(
                    modifier =
                        modifier
                            .widthIn(
                                min = LocalDimens.current.dialog.minWidth,
                                max = LocalDimens.current.dialog.maxWidth,
                            )
                            .width(IntrinsicSize.Min)
                            .padding(all = LocalDimens.current.dialog.paddingLarge),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = LocalDimens.current.dialog.tonalElevation,
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    TimePickerContent(
                        modifier =
                            Modifier.padding(
                                horizontal = LocalDimens.current.padding.contentHorizontal,
                                vertical = LocalDimens.current.padding.contentVertical,
                            ),
                        state = state,
                        onNegativeButtonClick = { state.isShowing = false },
                        onPositiveButtonClick = {
                            state.isShowing = false
                            onTimePicked(state.hourInt, state.minuteInt)
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun TimePickerContent(
    state: TimePickerState,
    modifier: Modifier = Modifier,
    onPositiveButtonClick: () -> Unit,
    onNegativeButtonClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(bottom = LocalDimens.current.padding.itemVertical),
            text = stringResource(id = R.string.picker_time_title),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleSmall,
        )

        Row(
            modifier =
                Modifier.padding(top = LocalDimens.current.dialog.paddingLarge)
                    .height(IntrinsicSize.Max),
            horizontalArrangement =
                Arrangement.spacedBy(LocalDimens.current.padding.contentHorizontalSmall),
        ) {
            val inputTextStyle =
                MaterialTheme.typography.displayMedium.copy(textAlign = TextAlign.Center)

            Column(modifier = Modifier.fillMaxHeight().weight(weight = 1f, fill = false)) {
                TextField(
                    focusedValue = state.hour,
                    unfocusedValue = state.formattedHour,
                    onValueChange = { hour ->
                        state.updateHour(hour) { focusManager.moveFocus(FocusDirection.Next) }
                    },
                    textStyle = inputTextStyle,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next,
                        ),
                )

                SupportingText(text = stringResource(id = R.string.picker_time_hour))
            }

            Text(
                modifier = Modifier.padding(top = LocalDimens.current.padding.itemVertical),
                text = stringResource(id = R.string.picker_time_colon),
                style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
            )

            Column(modifier = Modifier.fillMaxHeight().weight(weight = 1f, fill = false)) {
                TextField(
                    focusedValue = state.minute,
                    unfocusedValue = state.formattedMinute,
                    onValueChange = state::updateMinute,
                    textStyle = inputTextStyle,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done,
                        ),
                    keyboardActions = KeyboardActions(onDone = { onPositiveButtonClick() }),
                )

                SupportingText(text = stringResource(id = R.string.picker_time_minute))
            }

            if (state.is24h.not()) {
                TimeOfDayIndicator(state = state)
            }
        }

        DialogButtons(
            modifier = Modifier.padding(top = LocalDimens.current.dialog.paddingLarge),
            onPositiveButtonClick = onPositiveButtonClick,
            onNegativeButtonClick = onNegativeButtonClick,
        )
    }
}

@Composable
private fun ColumnScope.TextField(
    focusedValue: String,
    unfocusedValue: String,
    onValueChange: (String) -> Unit,
    textStyle: TextStyle,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }

    val isFocused by interactionSource.collectIsFocusedAsState()

    OutlinedTextField(
        modifier = modifier.weight(weight = 1f),
        value = if (isFocused) focusedValue else unfocusedValue,
        onValueChange = onValueChange,
        textStyle = textStyle,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
    )
}

@Composable
private fun TimeOfDayIndicator(state: TimePickerState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxHeight()) {
        VerticalSegmentedButtonContainer(
            modifier = Modifier.weight(weight = 1f),
            items = TimeOfDay.values().toList(),
            shape = MaterialTheme.shapes.extraSmall,
        ) { _, timeOfDay ->
            SegmentedButton(
                modifier = Modifier.fillMaxHeight(),
                text = timeOfDay.text,
                onClick = { state.timeOfDay = timeOfDay },
                selected = state.timeOfDay == timeOfDay,
            )
        }

        SupportingText(text = "")
    }
}

@Suppress("MagicNumber")
@Immutable
@Stable
class TimePickerState(
    isShowing: Boolean = false,
    hour: Int,
    minute: Int,
    val is24h: Boolean = true,
) {

    private val decimalFormat = DecimalFormat("00")

    private val timeRange = if (is24h) 0..24 else 0..12

    val formattedHour: String by derivedStateOf { decimalFormat.format(this.hour.toIntOrZero()) }

    val formattedMinute: String by derivedStateOf {
        decimalFormat.format(this.minute.toIntOrZero())
    }

    val hourInt: Int
        get() {
            var hour = hour.toIntOrZero()

            when {
                is24h.not() && timeOfDay == TimeOfDay.PM -> hour += 12
                is24h.not() && timeOfDay == TimeOfDay.AM && hour == 12 -> hour = 0
            }

            return hour
        }

    val minuteInt: Int
        get() = minute.toIntOrZero()

    var isShowing by mutableStateOf(isShowing)

    var hour by mutableStateOf(getDisplayHour(hour))

    var minute by mutableStateOf(minute.toString())

    var timeOfDay by mutableStateOf(if (hour > 12) TimeOfDay.PM else TimeOfDay.AM)

    fun updateHour(hour: String, onInputFilled: () -> Unit) {
        if (hour.isEmpty()) {
            this.hour = ""
        } else {
            val parsedHour = decimalFormat.parseToIntOrNull(hour) ?: return
            if (parsedHour in timeRange) {
                this.hour = hour
                if (this.hour.length == 2) onInputFilled()
            }
        }
    }

    fun updateMinute(minute: String) {
        if (minute.isEmpty()) {
            this.minute = ""
        } else {
            val parsedMinute = decimalFormat.parseToIntOrNull(minute) ?: return
            if (parsedMinute in 0..59) {
                this.minute = minute
            }
        }
    }

    private fun getDisplayHour(hour: Int): String =
        if (is24h.not() && hour > 12) {
                hour - 12
            } else {
                hour
            }
            .let(decimalFormat::format)
}

@Composable
fun rememberTimePickerState(
    isShowing: Boolean = false,
    hour: Int,
    minute: Int,
    is24h: Boolean = true,
) =
    remember(keys = arrayOf(is24h, hour, minute)) {
        TimePickerState(isShowing = isShowing, hour = hour, minute = minute, is24h = is24h)
    }

@MultiDevicePreview
@Composable
fun TimePicker12HPreview() {
    LiftAppTheme {
        TimePicker(
            modifier = Modifier.padding(16.dp),
            state =
                rememberTimePickerState(isShowing = true, is24h = false, hour = 21, minute = 37),
            onTimePicked = { _, _ -> },
        )
    }
}

@MultiDevicePreview
@Composable
fun TimePicker24HPreview() {
    LiftAppTheme {
        TimePicker(
            modifier = Modifier.padding(16.dp),
            state = rememberTimePickerState(isShowing = true, hour = 21, minute = 37),
            onTimePicked = { _, _ -> },
        )
    }
}
