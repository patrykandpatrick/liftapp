package com.patrykandpatryk.liftapp.core.ui.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppTextField
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.ui.SupportingText
import com.patrykandpatryk.liftapp.core.ui.VerticalDivider
import com.patrykandpatryk.liftapp.core.ui.button.IconButton
import com.patrykandpatryk.liftapp.domain.validation.valueInRange

@Composable
fun NumberInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onPlusClick: (isLong: Boolean) -> Unit,
    onMinusClick: (isLong: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    prefix: String? = null,
    suffix: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
) {
    val hapticFeedback = LocalHapticFeedback.current

    LiftAppTextField(
        modifier = modifier.height(IntrinsicSize.Max).fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        label =
            if (hint != null) {
                { Text(text = hint) }
            } else {
                null
            },
        leadingIcon = {
            Row(modifier = Modifier.padding(end = LocalDimens.current.padding.itemVerticalSmall)) {
                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onLongClick = {
                        onMinusClick(true)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onClick = { onMinusClick(false) },
                    repeatLongClicks = true,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_minus),
                        contentDescription = stringResource(id = R.string.action_decrease),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                VerticalDivider(
                    modifier = Modifier.padding(vertical = LocalDimens.current.padding.itemVertical)
                )

                if (prefix != null) {
                    Text(
                        modifier =
                            Modifier.align(Alignment.CenterVertically)
                                .padding(start = LocalDimens.current.padding.contentHorizontal),
                        text = prefix,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        trailingIcon = {
            Row(
                modifier = Modifier.padding(start = LocalDimens.current.padding.itemHorizontalSmall)
            ) {
                if (suffix != null) {
                    Text(
                        modifier =
                            Modifier.align(Alignment.CenterVertically)
                                .padding(end = LocalDimens.current.padding.contentHorizontal),
                        text = suffix,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                VerticalDivider(
                    modifier = Modifier.padding(vertical = LocalDimens.current.padding.itemVertical)
                )

                IconButton(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    onLongClick = {
                        onPlusClick(true)
                        hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onClick = { onPlusClick(false) },
                    repeatLongClicks = true,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = stringResource(id = R.string.action_increase),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        isError = isError,
    )
}

@Composable
fun <T : Number> NumberInput(
    textFieldState: TextFieldState<T>,
    onPlusClick: (isLong: Boolean) -> Unit,
    onMinusClick: (isLong: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    prefix: String? = null,
    suffix: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    supportingText: AnnotatedString? = null,
) {
    Column(modifier = modifier) {
        NumberInput(
            value = textFieldState.textFieldValue,
            onValueChange = textFieldState::updateText,
            onPlusClick = onPlusClick,
            onMinusClick = onMinusClick,
            hint = hint,
            prefix = prefix,
            suffix = suffix,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = textFieldState.hasError,
        )

        SupportingText(
            value = textFieldState.text,
            supportingText = supportingText,
            errorText = textFieldState.errorMessage?.let(::AnnotatedString),
        )
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewNumberInput() {
    LiftAppTheme {
        Surface {
            NumberInput(
                modifier = Modifier.padding(16.dp),
                value = TextFieldValue("40"),
                hint = "Weight",
                onValueChange = {},
                onPlusClick = {},
                onMinusClick = {},
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewNumberInputWithError() {
    LiftAppTheme {
        Surface {
            NumberInput(
                modifier = Modifier.padding(16.dp),
                textFieldState =
                    PreviewResource.textFieldStateManager()
                        .doubleTextField(
                            initialValue = "40",
                            validators = { valueInRange(50.0, 100.0) },
                        ),
                hint = "Weight",
                onPlusClick = {},
                onMinusClick = {},
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun PreviewNumberInputWithPrefixAndSuffix() {
    LiftAppTheme {
        Surface {
            NumberInput(
                modifier = Modifier.padding(16.dp),
                value = TextFieldValue("40"),
                hint = "Weight",
                onValueChange = {},
                onPlusClick = {},
                onMinusClick = {},
                suffix = "kg",
                prefix = "Weight",
            )
        }
    }
}
