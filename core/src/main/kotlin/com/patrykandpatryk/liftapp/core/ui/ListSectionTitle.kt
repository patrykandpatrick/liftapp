package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.patrykandpatryk.liftapp.core.preview.LightAndDarkThemePreview
import com.patrykandpatryk.liftapp.core.ui.dimens.LocalDimens
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme

@Composable
fun ListSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier
            .padding(
                vertical = LocalDimens.current.padding.itemVertical,
                horizontal = LocalDimens.current.padding.contentHorizontal,
            ),
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@LightAndDarkThemePreview
@Composable
fun ListSectionTitlePreview() {
    LiftAppTheme {
        Surface {
            ListSectionTitle(
                title = "Title",
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
