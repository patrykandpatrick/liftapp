package com.patrykandpatrick.liftapp.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons

@Composable
fun CompactTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    colors: TopAppBarColors = AppBars.colors(),
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    divider: Boolean = colors.scrolledContainerColor != Color.Transparent,
    alwaysShowChrome: Boolean = false,
    content: @Composable (ColumnScope.() -> Unit)? = null,
) {
    // M3 swaps the app bar's own background for `scrolledContainerColor` once content is scrolled
    // under it. The same signal drives the background behind the content slot and the divider, so
    // the bar takes on all of its chrome at once. Sticky composite headers can keep that chrome on
    // so the header and its content read as one visual surface with a persistent lower edge. The
    // divider keeps its space either way, so an ordinary bar does not change height as it appears.
    val showChrome = alwaysShowChrome || AppBars.isContentScrolledUnder(scrollBehavior)
    val containerColor by
        animateColorAsState(
            if (showChrome) colors.scrolledContainerColor else colors.containerColor
        )
    val dividerAlpha by animateFloatAsState(if (showChrome) 1f else 0f)

    Column(modifier.drawBehind { drawRect(containerColor) }) {
        CenterAlignedTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            // The containing Column owns the animated background so it can cover both the app bar
            // and the optional sticky content below it. Leaving the Material app bar opaque would
            // cover that background, notably when `alwaysShowChrome` is used without a scroll
            // behavior.
            colors =
                colors.copy(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                ),
            scrollBehavior = scrollBehavior,
            windowInsets = windowInsets,
        )
        content?.invoke(this)
        if (divider) LiftAppHorizontalDivider(Modifier.graphicsLayer { alpha = dividerAlpha })
    }
}

object CompactTopAppBarDefaults {
    @Composable
    fun Title(title: String, modifier: Modifier = Modifier) {
        Text(text = title, modifier = modifier)
    }

    @Composable
    fun IconButton(painter: Painter, contentDescription: String? = null, onClick: () -> Unit) {
        LiftAppIconButton(onClick = onClick) {
            Icon(painter = painter, contentDescription = contentDescription)
        }
    }

    @Composable
    fun IconButton(
        imageVector: ImageVector,
        contentDescription: String? = null,
        onClick: () -> Unit,
    ) {
        LiftAppIconButton(onClick = onClick) {
            Icon(imageVector = imageVector, contentDescription = contentDescription)
        }
    }

    @Composable
    fun BackIcon(onClick: () -> Unit) {
        LiftAppIconButton(onClick = onClick) {
            Icon(
                imageVector = LiftAppIcons.ArrowBack,
                contentDescription = stringResource(id = R.string.action_go_back),
            )
        }
    }
}
