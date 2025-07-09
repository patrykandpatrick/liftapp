package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.LiftAppIconButton
import com.patrykandpatrick.liftapp.ui.icons.ArrowBack
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatryk.liftapp.core.R

@Composable
fun CompactTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)? = { LiftAppHorizontalDivider() },
) {
    Column(modifier) {
        CenterAlignedTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = AppBars.colors,
            scrollBehavior = scrollBehavior,
        )
        content?.invoke(this)
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
