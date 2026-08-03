package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.patrykandpatrick.liftapp.core.text.TextFieldState
import com.patrykandpatrick.liftapp.domain.Constants.Input.TYPING_DEBOUNCE_MILLIS
import com.patrykandpatrick.liftapp.ui.component.LiftAppTextField
import com.patrykandpatrick.liftapp.ui.component.LiftAppTextFieldDefaults
import com.patrykandpatrick.liftapp.ui.dimens.dimens
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlinx.coroutines.delay

@Composable
fun <T : Any> LiftAppTextFieldWithSupportingText(
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
    shape: Shape = LiftAppTextFieldDefaults.shape,
    supportingText: AnnotatedString? = null,
) {
    LiftAppTextFieldWithSupportingText(
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
        supportingText = supportingText,
        errorText = textFieldState.errorMessage?.let(::AnnotatedString),
    )
}

@Composable
fun LiftAppTextFieldWithSupportingText(
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
    shape: Shape = LiftAppTextFieldDefaults.shape,
    supportingText: AnnotatedString? = null,
    errorText: AnnotatedString? = null,
) {
    Column(modifier = modifier) {
        LiftAppTextField(
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
    val (cachedSupportingText, setCachedSupportingText) =
        remember {
            mutableStateOf(supportingText)
        }

    LaunchedEffect(key1 = errorText == null, key2 = value) {
        setErrorVisible(false)
        if (errorText != null) {
            delay(TYPING_DEBOUNCE_MILLIS)
            setCachedErrorText(errorText)
        }
        setErrorVisible(errorText != null)
    }

    LaunchedEffect(supportingText) {
        if (supportingText != null) setCachedSupportingText(supportingText)
    }

    val errorVisibility = remember { MutableTransitionState(errorVisible) }
    errorVisibility.targetState = errorVisible
    val supportingTextVisibility = remember { MutableTransitionState(supportingText != null) }
    supportingTextVisibility.targetState = supportingText != null

    LaunchedEffect(errorVisibility.isIdle, errorVisibility.currentState, errorText) {
        if (errorVisibility.isIdle && !errorVisibility.currentState && errorText == null) {
            setCachedErrorText(null)
        }
    }
    LaunchedEffect(
        supportingTextVisibility.isIdle,
        supportingTextVisibility.currentState,
        supportingText,
    ) {
        if (
            supportingTextVisibility.isIdle &&
                !supportingTextVisibility.currentState &&
                supportingText == null
        ) {
            setCachedSupportingText(null)
        }
    }

    // Keep the composable around while removed text finishes its exit animation, but do not reserve
    // the supporting-text padding when this field has never had anything to show.
    if (
        supportingText == null &&
            cachedSupportingText == null &&
            errorText == null &&
            cachedErrorText == null
    ) {
        return
    }

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
        Column(
            modifier =
                modifier.padding(
                    horizontal = dimens.supportingText.horizontalPadding,
                    vertical = dimens.supportingText.verticalPadding,
                )
        ) {
            AnimatedVisibility(visibleState = errorVisibility) {
                if (cachedErrorText != null) {
                    Text(text = cachedErrorText, color = colorScheme.error)
                }
            }

            AnimatedVisibility(visibleState = supportingTextVisibility) {
                if (cachedSupportingText != null) {
                    Text(text = cachedSupportingText, color = colorScheme.foregroundVariant)
                }
            }
        }
    }
}
