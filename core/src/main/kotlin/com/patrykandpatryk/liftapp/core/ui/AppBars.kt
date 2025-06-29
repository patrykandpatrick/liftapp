package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.tabs.LiftAppTabRow
import com.patrykandpatrick.liftapp.ui.component.tabs.LiftAppTabRowItem
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview

@Composable
fun TopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column {
        LargeTopAppBar(
            scrollBehavior = scrollBehavior,
            title = { Text(text = title) },
            actions = actions,
            colors = AppBars.colors,
            navigationIcon = {
                if (onBackClick != null) {
                    LiftAppIconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = LiftAppIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.action_close),
                        )
                    }
                }
            },
        )

        LiftAppHorizontalDivider()
    }
}

@Composable
fun TopAppBarWithTabs(
    modifier: Modifier = Modifier,
    title: String,
    selectedTabIndex: () -> Int,
    selectedTabOffset: (() -> Float)? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    tabs: @Composable () -> Unit,
) {
    CompactTopAppBar(
        title = { Text(text = title) },
        actions = actions,
        navigationIcon = {
            if (onBackClick != null) {
                AppBars.BackArrow(onClick = onBackClick)
            }
        },
        content = {
            LiftAppTabRow(
                selectedTabIndex = selectedTabIndex(),
                selectedTabOffset = selectedTabOffset?.invoke(),
                modifier = modifier.background(colorScheme.surface),
                tabs = tabs,
            )
            LiftAppHorizontalDivider()
        },
        modifier = modifier,
    )
}

@Composable
fun TopAppBarWithTabs(
    modifier: Modifier = Modifier,
    title: String,
    selectedTabIndex: () -> Int,
    selectedTabOffset: (() -> Float)? = null,
    onTabSelected: (index: Int) -> Unit,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    tabs: List<TabItem>,
) {
    TopAppBarWithTabs(
        modifier = modifier,
        title = title,
        selectedTabIndex = selectedTabIndex,
        selectedTabOffset = selectedTabOffset,
        onBackClick = onBackClick,
        actions = actions,
        tabs = {
            val tabDimens = LocalDimens.current.tab

            tabs.forEachIndexed { index, tabItem ->
                LiftAppTabRowItem(
                    selected = selectedTabIndex() == index,
                    onClick = { onTabSelected(index) },
                ) {
                    if (tabItem.icon != null) {
                        Icon(
                            modifier =
                                Modifier.align(Alignment.CenterHorizontally)
                                    .padding(
                                        bottom =
                                            if (tabItem.text != null) tabDimens.iconToTextPadding
                                            else 0.dp
                                    ),
                            painter = tabItem.icon,
                            contentDescription = null,
                        )
                    }

                    if (tabItem.text != null) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            text = tabItem.text,
                        )
                    }
                }
            }
        },
    )
}

@Immutable data class TabItem(val text: String? = null, val icon: Painter? = null)

@Composable
fun DialogTopBar(title: String, onCloseClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            modifier =
                Modifier.weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = LocalDimens.current.padding.contentHorizontal),
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )

        IconButton(
            modifier = Modifier.padding(end = LocalDimens.current.padding.contentHorizontalSmall),
            onClick = onCloseClick,
        ) {
            Icon(
                modifier = Modifier.align(Alignment.CenterVertically),
                imageVector = Icons.Outlined.Close,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = stringResource(id = R.string.action_close),
            )
        }
    }
}

object AppBars {
    @Composable
    fun BackArrow(onClick: () -> Unit, modifier: Modifier = Modifier) {
        LiftAppIconButton(onClick = onClick, modifier = modifier) {
            Icon(
                imageVector = LiftAppIcons.ArrowBack,
                contentDescription = stringResource(id = R.string.action_close),
            )
        }
    }

    val colors: TopAppBarColors
        @Composable
        get() =
            TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.surface,
                scrolledContainerColor = colorScheme.surface,
                navigationIconContentColor = colorScheme.onSurface,
                titleContentColor = colorScheme.onSurface,
                actionIconContentColor = colorScheme.onSurface,
                subtitleContentColor = colorScheme.onSurface,
            )
}

@MultiDevicePreview
@Composable
fun PreviewTopAppBarWithTextTabs() {
    LiftAppTheme {
        TopAppBarWithTabs(
            title = "Title",
            selectedTabIndex = { 0 },
            onTabSelected = {},
            tabs = listOf(TabItem(text = "First"), TabItem(text = "Second")),
        )
    }
}

@MultiDevicePreview
@Composable
fun PreviewTopAppBarWithIconTabs() {
    LiftAppTheme {
        TopAppBarWithTabs(
            title = "Title",
            selectedTabIndex = { 0 },
            onTabSelected = {},
            tabs =
                listOf(
                    TabItem(icon = painterResource(id = R.drawable.ic_time)),
                    TabItem(icon = painterResource(id = R.drawable.ic_workout)),
                ),
        )
    }
}

@MultiDevicePreview
@Composable
fun PreviewTopAppBarWithTextIconTabs() {
    LiftAppTheme {
        TopAppBarWithTabs(
            title = "Title",
            selectedTabIndex = { 0 },
            onTabSelected = {},
            tabs =
                listOf(
                    TabItem(text = "First", icon = painterResource(id = R.drawable.ic_time)),
                    TabItem(text = "Second", icon = painterResource(id = R.drawable.ic_workout)),
                ),
        )
    }
}

@MultiDevicePreview
@Composable
fun PreviewDialogTopBar() {
    Surface { DialogTopBar(title = "Title", onCloseClick = {}) }
}
