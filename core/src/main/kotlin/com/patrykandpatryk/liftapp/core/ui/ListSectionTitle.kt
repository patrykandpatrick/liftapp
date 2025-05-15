package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview

@Composable
fun ListSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues =
        PaddingValues(
            vertical = LocalDimens.current.padding.itemVertical,
            horizontal = LocalDimens.current.padding.contentHorizontal,
        ),
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(paddingValues),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.primary) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )

            trailingIcon?.invoke()
        }
    }
}

@LightAndDarkThemePreview
@Composable
fun ListSectionTitlePreview() {
    LiftAppTheme {
        Surface { ListSectionTitle(title = "Title", modifier = Modifier.fillMaxWidth()) }
    }
}
