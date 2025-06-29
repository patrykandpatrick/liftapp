package com.patrykandpatrick.liftapp.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun LiftAppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = LiftAppTextFieldDefaults.shape,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        shape = shape,
        colors = LiftAppTextFieldDefaults.colors(),
    )
}

@Composable
fun LiftAppTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = LiftAppTextFieldDefaults.shape,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource ?: remember { MutableInteractionSource() },
        shape = shape,
        colors = LiftAppTextFieldDefaults.colors(),
    )
}

object LiftAppTextFieldDefaults {

    val shape: Shape
        @Composable get() = MaterialTheme.shapes.medium

    @Composable
    fun colors(
        focused: Color = colorScheme.primary,
        text: Color = colorScheme.onSurface,
        secondaryTexts: Color = colorScheme.onSurfaceVariant,
        error: Color = colorScheme.error,
    ): TextFieldColors =
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = focused,
            focusedLabelColor = focused,
            focusedTextColor = text,
            cursorColor = focused,
            unfocusedTextColor = text,
            unfocusedLeadingIconColor = secondaryTexts,
            unfocusedTrailingIconColor = secondaryTexts,
            errorLabelColor = error,
            errorBorderColor = error,
            errorCursorColor = error,
            errorSupportingTextColor = error,
            errorPlaceholderColor = error,
            selectionColors = TextSelectionColors(focused, colorScheme.primaryDisabled),
        )
}

@LightAndDarkThemePreview
@Composable
private fun LiftAppTextFieldEmptyPreview() {
    LiftAppTheme {
        LiftAppBackground {
            LiftAppTextField(
                value = "",
                onValueChange = {},
                label = { Text("Label") },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun LiftAppTextFieldWithTextPreview() {
    LiftAppTheme {
        LiftAppBackground {
            LiftAppTextField(
                value = "Text",
                onValueChange = {},
                label = { Text("Label") },
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun LiftAppTextFieldWithTextAndErrorPreview() {
    LiftAppTheme {
        LiftAppBackground {
            LiftAppTextField(
                value = "Text",
                onValueChange = {},
                label = { Text("Label") },
                isError = true,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
