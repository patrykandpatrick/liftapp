package com.patrykandpatryk.liftapp.core.ui.input

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.nonBlankOrNull
import com.patrykandpatryk.liftapp.core.time.now
import com.patrykandpatryk.liftapp.core.ui.SupportingText
import com.patrykandpatryk.liftapp.core.ui.dialog.DialogButtons
import com.patrykandpatryk.liftapp.core.ui.dimens.DialogDimens
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.domain.date.day
import com.patrykandpatryk.liftapp.domain.date.getMillisFor
import com.patrykandpatryk.liftapp.domain.date.isValid
import com.patrykandpatryk.liftapp.domain.date.month
import com.patrykandpatryk.liftapp.domain.date.safeParseToCalendar
import com.patrykandpatryk.liftapp.domain.date.year
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    state: DatePickerState,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    onTimePicked: (year: Int, month: Int, day: Int) -> Unit,
) {

    CompositionLocalProvider(LocalDimens provides DialogDimens) {

        if (state.isShowing) {

            Dialog(
                onDismissRequest = { state.isShowing = false },
                properties = properties,
            ) {

                Surface(
                    modifier = modifier
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

                    DatePickerContent(
                        modifier = Modifier
                            .padding(
                                horizontal = LocalDimens.current.padding.contentHorizontal,
                                vertical = LocalDimens.current.padding.contentVertical,
                            ),
                        state = state,
                        onPositiveButtonClick = onClick@{
                            val calendar = state.resolvedDate ?: return@onClick
                            onTimePicked(calendar.year, calendar.month, calendar.day)
                            state.hide()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun DatePickerContent(
    modifier: Modifier = Modifier,
    state: DatePickerState,
    onPositiveButtonClick: () -> Unit,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(LocalDimens.current.verticalItemSpacing),
    ) {

        Text(
            modifier = Modifier.padding(bottom = LocalDimens.current.padding.itemVertical),
            text = stringResource(id = R.string.picker_date_title),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleSmall,
        )

        Text(
            text = state.resolvedFormattedDate,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineMedium,
        )

        OutlinedTextField(
            value = state.input,
            onValueChange = state::updateInput,
            label = {
                Text(text = stringResource(id = R.string.picker_date_input_title))
            },
            placeholder = {
                Text(text = stringResource(id = R.string.picker_date_example))
            },
            isError = state.hasError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
        )

        SupportingText(
            text = stringResource(
                id = R.string.picker_date_input_error,
                stringResource(id = R.string.picker_date_example),
                state.sampleDate,
            ),
            isError = state.hasError,
            visible = state.hasError,
        )

        DialogButtons(
            modifier = Modifier.padding(top = LocalDimens.current.dialog.paddingMedium),
            onNegativeButtonClick = state::hide,
            onPositiveButtonClick = onPositiveButtonClick,
            isPositiveButtonEnabled = state.isInputValid,
        )
    }
}

@Stable
class DatePickerState(
    millis: Long? = null,
    isShowing: Boolean = false,
    inputDatePattern: String,
    outputDatePattern: String,
    exampleDatePattern: String,
    unresolvedDateText: String,
    private val scope: CoroutineScope,
    private val errorDelay: Long,
) {

    private val inputDateFormat = SimpleDateFormat(inputDatePattern, Locale.getDefault())

    private val outputDateFormat = SimpleDateFormat(outputDatePattern, Locale.getDefault())

    private val exampleDateFormat = SimpleDateFormat(exampleDatePattern, Locale.getDefault())

    private var _hasError = mutableStateOf(false)

    private var delayJob: Job? = null

    val hasError: Boolean by _hasError

    val isInputValid: Boolean by derivedStateOf { inputDateFormat.isValid(input) }

    val sampleDate: String = exampleDateFormat.format(now)

    val resolvedDate: Calendar? by derivedStateOf {
        input.nonBlankOrNull?.let(inputDateFormat::safeParseToCalendar)
    }

    val resolvedFormattedDate: String by derivedStateOf {
        val parsedDate = resolvedDate?.timeInMillis

        if (parsedDate != null) {
            outputDateFormat.format(parsedDate)
        } else {
            unresolvedDateText
        }
    }

    var isShowing: Boolean by mutableStateOf(isShowing)

    var input: String by mutableStateOf(millis?.let(exampleDateFormat::format) ?: "")

    fun show() {
        isShowing = true
    }

    fun hide() {
        isShowing = false
    }

    fun updateInput(value: String) {
        input = value
        _hasError.value = false
        delayJob?.cancel()

        val hasError = input.isNotBlank() && inputDateFormat.isValid(input).not()

        if (hasError) {
            delayJob = scope.launch {
                delay(errorDelay)
                _hasError.value = true
            }
        }
    }
}

@Composable
fun rememberDatePickerState(
    millis: Long? = null,
    isShowing: Boolean = false,
): DatePickerState {

    val inputDatePattern = stringResource(id = R.string.picker_date_input_date_format)
    val outputDatePattern = stringResource(id = R.string.picker_date_output_date_format)
    val exampleDatePattern = stringResource(id = R.string.picker_date_example_date_format)
    val unresolvedDateText = stringResource(id = R.string.picker_date_action_title)

    val scope = rememberCoroutineScope()

    val errorDelay = integerResource(id = R.integer.input_error_delay).toLong()

    return remember(key1 = millis) {
        DatePickerState(
            millis = millis,
            isShowing = isShowing,
            inputDatePattern = inputDatePattern,
            outputDatePattern = outputDatePattern,
            exampleDatePattern = exampleDatePattern,
            unresolvedDateText = unresolvedDateText,
            scope = scope,
            errorDelay = errorDelay,
        )
    }
}

@Preview(backgroundColor = 0xFFFFFFFF)
@Preview(backgroundColor = 0xFF212121, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DatePickerPreview() {
    LiftAppTheme {
        DatePicker(
            state = rememberDatePickerState(
                millis = getMillisFor(year = 2022, month = 8, day = 13),
                isShowing = true,
            ),
            onTimePicked = { _, _, _ -> },
        )
    }
}
