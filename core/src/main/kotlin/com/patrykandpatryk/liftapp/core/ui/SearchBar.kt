package com.patrykandpatryk.liftapp.core.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.InteractiveBorderColors
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.icons.Cross
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.modifier.interactiveButtonEffect
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.PreviewResource
import com.patrykandpatryk.liftapp.core.text.StringTextFieldState

@Composable
fun SearchBar(textFieldState: StringTextFieldState, modifier: Modifier = Modifier) {
    var focused by remember { mutableStateOf(value = false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val isValueNotEmpty = textFieldState.value.isNotEmpty()

    val clearFocusAndValue =
        {
                focusManager.clearFocus()
                if (isValueNotEmpty) textFieldState.clear()
            }
            .also { BackHandler(enabled = focused, onBack = it) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .interactiveButtonEffect(
                    colors =
                        InteractiveBorderColors(
                            color = colorScheme.outline,
                            pressedColor = colorScheme.primary,
                            hoverForegroundColor = colorScheme.primary,
                            hoverBackgroundColor = colorScheme.outline,
                        ),
                    onClick = {
                        if (focused) clearFocusAndValue() else focusRequester.requestFocus()
                    },
                    enabled = true,
                    shape = CircleShape,
                )
                .dropShadow(CircleShape) {
                    radius = 8.dp.toPx()
                    spread = 1.dp.toPx()
                    color = Color.Black.copy(alpha = .06f)
                }
                .dropShadow(CircleShape) {
                    radius = 1.dp.toPx()
                    spread = 1.dp.toPx()
                    color = Color.Black.copy(alpha = .06f)
                    offset = Offset(0f, 1.dp.toPx())
                }
                .background(color = colorScheme.surfaceVariant, shape = CircleShape)
                .padding(horizontal = 4.dp)
                .height(IntrinsicSize.Min),
    ) {
        LiftAppIconButton(
            onClick = { if (focused) clearFocusAndValue() else focusRequester.requestFocus() }
        ) {
            AnimatedContent(targetState = focused) { targetState ->
                Icon(
                    imageVector =
                        if (targetState) Icons.AutoMirrored.Filled.ArrowBack
                        else Icons.Default.Search,
                    contentDescription = null,
                    tint = colorScheme.onSurface,
                )
            }
        }
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.weight(1f)) {
            BasicTextField(
                value = textFieldState.textFieldValue,
                onValueChange = textFieldState::updateText,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colorScheme.onSurface),
                modifier =
                    Modifier.fillMaxWidth()
                        .focusRequester(focusRequester = focusRequester)
                        .onFocusChanged { focusState -> focused = focusState.isFocused },
                decorationBox = { textField ->
                    textField()

                    if (!isValueNotEmpty) {
                        Text(
                            text = stringResource(id = R.string.generic_search),
                            style = MaterialTheme.typography.bodyLarge,
                            color = colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        }

        LiftAppIconButton(onClick = textFieldState::clear, enabled = isValueNotEmpty) {
            AnimatedContent(targetState = isValueNotEmpty, contentAlignment = Alignment.Center) {
                isVisible ->
                if (isVisible) {
                    Icon(
                        imageVector = LiftAppIcons.Cross,
                        contentDescription = stringResource(R.string.action_clear),
                    )
                }
            }
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun SearchBarInactivePreview() {
    LiftAppTheme {
        LiftAppBackground {
            SearchBar(
                textFieldState = PreviewResource.textFieldStateManager().stringTextField(),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun SearchBarActivePreview() {
    LiftAppTheme {
        LiftAppBackground {
            SearchBar(
                textFieldState = PreviewResource.textFieldStateManager().stringTextField(),
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }
}
