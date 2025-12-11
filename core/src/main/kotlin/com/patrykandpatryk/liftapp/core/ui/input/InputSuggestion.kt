package com.patrykandpatryk.liftapp.core.ui.input

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppText
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.preview.PreviewTheme

@Composable
fun InputSuggestion(
    text: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiftAppCard(
        onClick = onClick,
        modifier = modifier,
        contentPadding =
            PaddingValues(
                horizontal = LocalDimens.current.padding.itemHorizontalMedium,
                vertical = LocalDimens.current.padding.itemVerticalSmall,
            ),
        verticalArrangement = Arrangement.Top,
        colors = LiftAppCardDefaults.tonalCardColors,
    ) {
        LiftAppText(text = text, style = MaterialTheme.typography.bodySmall)
        LiftAppText(text = label, style = MaterialTheme.typography.labelSmall)
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
private fun SuggestionsRowPreview() {
    PreviewTheme {
        LiftAppBackground {
            InputSuggestionsRow(Modifier.padding(16.dp)) {
                InputSuggestion(text = "50 kg × 8 reps", label = "Previous set", onClick = {})
                InputSuggestion(text = "45 kg × 8 reps", label = "Previous workout", onClick = {})
            }
        }
    }
}
