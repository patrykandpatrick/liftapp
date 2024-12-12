package com.patrykandpatryk.liftapp.core.ui.wheel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import kotlin.time.Duration
import kotlinx.coroutines.launch

@Composable
fun WheelPickerDurationInputField(
    text: String,
    duration: Duration,
    onTimeChange: (Duration) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val isFocused = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val onDismissRequest = { focusManager.clearFocus() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        value = text,
        onValueChange = {},
        readOnly = true,
        label = { Text(text = label) },
        modifier = modifier.onFocusEvent { focusState -> isFocused.value = focusState.hasFocus },
    )

    if (isFocused.value) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
            dragHandle = null,
            modifier = modifier,
        ) {
            TimeBottomSheetContent(
                duration = duration,
                onTimeChange = onTimeChange,
                label = label,
                onDismissRequest = {
                    coroutineScope.launch {
                        sheetState.hide()
                        onDismissRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun TimeBottomSheetContent(
    duration: Duration,
    onTimeChange: (Duration) -> Unit,
    label: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
                .padding(
                    horizontal = LocalDimens.current.padding.contentHorizontal,
                    vertical = LocalDimens.current.padding.contentVertical,
                )
                .padding(top = LocalDimens.current.padding.contentVerticalSmall),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(LocalDimens.current.verticalItemSpacing))

        duration.toComponents { _, _, minutes, seconds, _ ->
            TimePicker(
                hour = null,
                minute = minutes,
                second = seconds,
                is24h = false,
                onTimeChange = onTimeChange,
            )
        }

        Spacer(Modifier.height(LocalDimens.current.verticalItemSpacing))

        Button(onClick = onDismissRequest, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.action_done))
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun TimeBottomSheetPreview() {
    LiftAppTheme {
        TimeBottomSheetContent(
            duration = Duration.ZERO,
            onTimeChange = {},
            label = "Duration",
            onDismissRequest = {},
        )
    }
}
