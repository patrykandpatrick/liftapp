package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.foundation.layout.Box
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
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemDefaults
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.preview.LightAndDarkThemePreview
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme

@Composable
fun ListSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
    inset: ListSectionTitleDefaults.Inset = ListSectionTitleDefaults.Inset.ListItemContent,
    spacing: ListSectionTitleDefaults.Spacing = ListSectionTitleDefaults.Spacing.Standard,
    endPadding: Dp? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val screenHorizontalPadding = LocalDimens.current.screen.padding
    val startPadding =
        when (inset) {
            ListSectionTitleDefaults.Inset.ListItemContent ->
                LiftAppListItemDefaults.sectionHeadingStartPadding
            ListSectionTitleDefaults.Inset.Screen -> screenHorizontalPadding
        }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier.padding(
                start = startPadding,
                top = spacing.top,
                end = endPadding ?: screenHorizontalPadding,
                bottom = spacing.bottom,
            ),
    ) {
        CompositionLocalProvider(LocalContentColor provides colorScheme.foreground) {
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
    /** The two horizontal alignments used by section headings throughout the app. */
    enum class Inset {
        /** Aligns with list-item content, including the inset inside the screen margin. */
        ListItemContent,

        /** Aligns directly with the screen margin. */
        Screen,
    }

    /** Complete vertical gaps owned by the heading rather than assembled by its neighbors. */
    enum class Spacing(val top: Dp, val bottom: Dp) {
        Standard(top = 16.dp, bottom = 16.dp),
        AfterDivider(top = 20.dp, bottom = 16.dp),
        Section(top = 32.dp, bottom = 16.dp),
    }
}

@LightAndDarkThemePreview
@Composable
fun ListSectionTitlePreview() {
    LiftAppTheme {
        LiftAppBackground { ListSectionTitle(title = "Title", modifier = Modifier.fillMaxWidth()) }
    }
}
