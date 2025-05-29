package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatryk.liftapp.core.R

@Composable
fun CompactTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
) {
    Column(modifier) {
        CenterAlignedTopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = AppBars.colors,
        )
        LiftAppHorizontalDivider()
    }
}

object CompactTopAppBarDefaults {
    @Composable
    fun Title(title: String, modifier: Modifier = Modifier) {
        Text(text = title, modifier = modifier)
    }

    @Composable
    fun IconButton(painter: Painter, contentDescription: String? = null, onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(painter = painter, contentDescription = contentDescription)
        }
    }

    @Composable
    fun BackIcon(onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = stringResource(id = R.string.action_go_back),
            )
        }
    }
}
