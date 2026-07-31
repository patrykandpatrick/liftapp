package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppBackground
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun ListSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues =
        PaddingValues(
            vertical = 16.dp,
            horizontal = LocalDimens.current.screen.horizontalPadding,
        ),
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(paddingValues),
    ) {
        CompositionLocalProvider(LocalContentColor provides colorScheme.onBackground) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )

            // The heading's own height sets the row's. A trailing control is usually a plain
            // button, whose padding is invisible until it is pressed, and letting that padding
            // stretch the row would push the section's content further down than a section
            // without one. It is centered on the heading and allowed to overflow instead.
            if (trailingIcon != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.height(0.dp).wrapContentHeight(unbounded = true),
                ) {
                    trailingIcon()
                }
            }
        }
    }
}

object ListSectionTitleDefaults {
    val betweenSectionsSpacing = 32.dp
    val withinSectionSpacing = 12.dp
    val bottomPadding = 4.dp

    fun topPadding(isFirstSection: Boolean): Dp =
        if (isFirstSection) {
            0.dp
        } else {
            betweenSectionsSpacing - withinSectionSpacing
        }
}

@LightAndDarkThemePreview
@Composable
fun ListSectionTitlePreview() {
    LiftAppTheme {
        LiftAppBackground { ListSectionTitle(title = "Title", modifier = Modifier.fillMaxWidth()) }
    }
}
