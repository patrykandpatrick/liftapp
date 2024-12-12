package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import com.patrykandpatryk.liftapp.core.text.TextFieldState
import com.patrykandpatryk.liftapp.core.ui.dimens.dimens
import com.patrykandpatryk.liftapp.domain.Constants.Input.TYPING_DEBOUNCE_MILLIS
import kotlinx.coroutines.delay

@Composable
fun <T : Any> OutlinedTextField(
    textFieldState: TextFieldState<T>,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = minLines,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    supportingText: AnnotatedString? = null,
) {
    OutlinedTextField(
        value = textFieldState.text,
        onValueChange = textFieldState::updateText,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
        supportingText = supportingText,
        errorText = textFieldState.errorMessage?.let(::AnnotatedString),
    )
}

@Composable
fun OutlinedTextField(
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
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = minLines,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    supportingText: AnnotatedString? = null,
    errorText: AnnotatedString? = null,
) {
    Column(modifier = modifier) {
        androidx.compose.material3.OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            readOnly = readOnly,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            supportingText = null,
            isError = errorText != null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors,
        )

        SupportingText(value = value, supportingText = supportingText, errorText = errorText)
    }
}

@Composable
fun SupportingText(
    modifier: Modifier = Modifier,
    value: String? = null,
    supportingText: AnnotatedString? = null,
    errorText: AnnotatedString? = null,
) {
    val (errorVisible, setErrorVisible) = remember { mutableStateOf(false) }
    val (cachedErrorText, setCachedErrorText) = remember { mutableStateOf(errorText) }

    LaunchedEffect(key1 = errorText == null, key2 = value) {
        setErrorVisible(false)
        if (errorText != null) {
            delay(TYPING_DEBOUNCE_MILLIS)
            setCachedErrorText(errorText)
        }
        setErrorVisible(errorText != null)
    }

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
        Column(
            modifier =
                modifier.padding(
                    horizontal = MaterialTheme.dimens.padding.supportingTextHorizontal,
                    vertical = MaterialTheme.dimens.padding.supportingTextVertical,
                )
        ) {
            AnimatedVisibility(visible = errorVisible) {
                LaunchedEffect(key1 = transition.currentState, key2 = errorText) {
                    if (transition.currentState == EnterExitState.PostExit && errorText == null) {
                        setCachedErrorText(null)
                    }
                }
                if (cachedErrorText != null) {
                    Text(text = cachedErrorText, color = MaterialTheme.colorScheme.error)
                }
            }

            AnimatedVisibility(visible = supportingText != null) {
                if (supportingText != null) {
                    Text(text = supportingText, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
