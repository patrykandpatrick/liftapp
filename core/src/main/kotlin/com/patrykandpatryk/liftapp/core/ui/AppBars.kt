package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
            colors = AppBars.colors(),
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
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
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
                modifier = modifier.background(colorScheme.background),
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

    val noBackgroundColors: TopAppBarColors
        @Composable get() = colors(containerColor = Color.Transparent)

    @Composable
    fun colors(
        containerColor: Color = colorScheme.background,
        contentColor: Color = colorScheme.onBackground,
    ): TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor,
            subtitleContentColor = contentColor,
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
