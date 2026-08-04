package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.lerp
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.component.tabs.LiftAppTabRow
import com.patrykandpatrick.liftapp.ui.component.tabs.LiftAppTabRowItem
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.Clock
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.icons.Settings
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
import kotlin.math.roundToInt

@Composable
fun TopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    // Expanded, the bar is part of the content behind it. As it collapses, its background and
    // divider fade in so the content scrolling underneath ends at a visible edge.
    val chromeAlpha = { scrollBehavior?.state?.collapsedFraction ?: 1f }
    val backgroundColor = colorScheme.background

    Column(Modifier.drawBehind { drawRect(color = backgroundColor, alpha = chromeAlpha()) }) {
        Box {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                actions = actions,
                colors = AppBars.noBackgroundColors,
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

            CollapsingTitle(
                title = title,
                collapsedFraction = { scrollBehavior?.state?.collapsedFraction ?: 0f },
                hasNavigationIcon = onBackClick != null,
                modifier =
                    Modifier.matchParentSize().windowInsetsPadding(TopAppBarDefaults.windowInsets),
            )
        }

        LiftAppHorizontalDivider(Modifier.graphicsLayer { alpha = chromeAlpha() })
    }
}

@Composable
private fun CollapsingTitle(
    title: String,
    collapsedFraction: () -> Float,
    hasNavigationIcon: Boolean,
    modifier: Modifier = Modifier,
) {
    val fraction = collapsedFraction().coerceIn(0f, 1f)
    val textStyle =
        lerp(MaterialTheme.typography.headlineMedium, MaterialTheme.typography.titleLarge, fraction)
            .copy(textMotion = TextMotion.Animated)

    Layout(
        content = {
            Text(
                text = title,
                color = AppBars.noBackgroundColors.titleContentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = textStyle,
            )
        },
        modifier = modifier,
    ) { measurables, constraints ->
        val expandedStart = 16.dp.roundToPx()
        val collapsedSideClearance = if (hasNavigationIcon) 56.dp.roundToPx() else expandedStart
        val sideClearance =
            (expandedStart + (collapsedSideClearance - expandedStart) * fraction).roundToInt()
        val titlePlaceable =
            measurables
                .single()
                .measure(
                    constraints.copy(
                        minWidth = 0,
                        minHeight = 0,
                        maxWidth = (constraints.maxWidth - sideClearance * 2).coerceAtLeast(0),
                    )
                )
        val titleBaseline =
            titlePlaceable[FirstBaseline].takeIf { it != AlignmentLine.Unspecified } ?: 0
        val expandedX = expandedStart
        val collapsedX = (constraints.maxWidth - titlePlaceable.width) / 2
        val titleX = (expandedX + (collapsedX - expandedX) * fraction).roundToInt()

        // LargeTopAppBar puts the expanded baseline 28dp above its 152dp lower edge. Its compact
        // title is vertically centered in the same 64dp row used by CenterAlignedTopAppBar.
        val expandedBaseline = (TopAppBarDefaults.LargeAppBarExpandedHeight - 28.dp).roundToPx()
        val collapsedTitleY =
            (TopAppBarDefaults.LargeAppBarCollapsedHeight.roundToPx() - titlePlaceable.height) / 2
        val collapsedBaseline = collapsedTitleY + titleBaseline
        val titleY =
            (expandedBaseline + (collapsedBaseline - expandedBaseline) * fraction).roundToInt() -
                titleBaseline

        layout(constraints.maxWidth, constraints.maxHeight) {
            titlePlaceable.placeRelative(x = titleX, y = titleY)
        }
    }
}

@Composable
fun TopAppBarWithTabs(
    modifier: Modifier = Modifier,
    title: String,
    selectedTabIndex: () -> Int,
    selectedTabOffset: (() -> Float)? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    tabs: @Composable () -> Unit,
) {
    val dividerColor = colorScheme.outline
    val dividerThickness = LocalDimens.current.divider.thickness

    CompactTopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        scrollBehavior = scrollBehavior,
        alwaysShowChrome = true,
        divider = false,
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
                modifier =
                    Modifier.drawBehind {
                        val thickness = dividerThickness.toPx()
                        drawRect(
                            color = dividerColor,
                            topLeft = Offset(0f, size.height - thickness),
                            size = Size(size.width, thickness),
                        )
                    },
                tabs = tabs,
            )
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
    scrollBehavior: TopAppBarScrollBehavior? = null,
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
        scrollBehavior = scrollBehavior,
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
                                            if (tabItem.text != null) {
                                                tabDimens.iconToTextPadding
                                            } else {
                                                0.dp
                                            }
                                    ),
                            imageVector = tabItem.icon,
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

@Immutable data class TabItem(val text: String? = null, val icon: ImageVector? = null)

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
        @Composable
        get() =
            colors(containerColor = Color.Transparent, scrolledContainerColor = Color.Transparent)

    /**
     * Bars are part of the content until something scrolls under them, at which point they take on
     * [scrolledContainerColor] and a divider to mark the edge the content disappears behind.
     *
     * The invisible state is a transparent [scrolledContainerColor] rather than
     * [Color.Transparent], which is transparent *black*: crossfading from it drags the bar through
     * a dark tint on the first frames of a scroll. Keeping both ends the same hue leaves alpha as
     * the only difference.
     */
    @Composable
    fun colors(
        scrolledContainerColor: Color = colorScheme.background,
        containerColor: Color = scrolledContainerColor.copy(alpha = 0f),
        contentColor: Color = colorScheme.foreground,
    ): TopAppBarColors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = scrolledContainerColor,
            navigationIconContentColor = contentColor,
            titleContentColor = contentColor,
            actionIconContentColor = contentColor,
            subtitleContentColor = contentColor,
        )

    /**
     * Whether content is currently scrolled under the bar, using the same reading of
     * [TopAppBarState.overlappedFraction] M3 itself uses to pick the container color. A bar without
     * a [scrollBehavior] has no way of knowing, and so is never considered scrolled under.
     */
    @Composable
    internal fun isContentScrolledUnder(scrollBehavior: TopAppBarScrollBehavior?): Boolean {
        val scrolledUnder =
            remember(scrollBehavior) {
                derivedStateOf { scrollBehavior?.state?.overlappedFraction ?: 0f > 0.01f }
            }
        return scrolledUnder.value
    }
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
                listOf(TabItem(icon = LiftAppIcons.Clock), TabItem(icon = LiftAppIcons.Settings)),
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
                    TabItem(text = "First", icon = LiftAppIcons.Clock),
                    TabItem(text = "Second", icon = LiftAppIcons.Settings),
                ),
        )
    }
}
