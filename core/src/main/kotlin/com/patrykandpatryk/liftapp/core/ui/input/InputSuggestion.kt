package com.patrykandpatryk.liftapp.core.ui.input

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R

@Composable
fun InputSuggestion(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    contentDescription: String? = null,
) {
    OutlinedButton(onClick = onClick, modifier = modifier) {
        if (icon != null) {
            Icon(icon, contentDescription, Modifier.padding(end = 6.dp))
        }
        Text(text)
    }
}

@Composable
fun InputSuggestionsRow(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement =
            Arrangement.spacedBy(LocalDimens.current.padding.itemHorizontalSmall, Alignment.End),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@LightAndDarkThemePreview
@Composable
private fun InputSuggestionPreview() {
    LiftAppTheme {
        Surface {
            InputSuggestion(
                text = "50 kg",
                onClick = {},
                icon = painterResource(R.drawable.ic_previous_set),
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@LightAndDarkThemePreview
@Composable
private fun SuggestionsRowPreview() {
    LiftAppTheme {
        Surface {
            InputSuggestionsRow(Modifier.padding(16.dp)) {
                InputSuggestion(
                    text = "50 kg",
                    onClick = {},
                    icon = painterResource(R.drawable.ic_previous_set),
                )
                InputSuggestion(
                    text = "45 kg",
                    onClick = {},
                    icon = painterResource(R.drawable.ic_history),
                )
            }
        }
    }
}
