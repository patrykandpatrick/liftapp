package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R

@Composable
fun CompactTopAppBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        CenterAlignedTopAppBar(title = title, navigationIcon = navigationIcon)
        HorizontalDivider()
    }
}

object CompactTopAppBarDefaults {
    @Composable
    fun Title(title: String, modifier: Modifier = Modifier) {
        Text(text = title, modifier = modifier)
    }

    @Composable
    fun BackIcon(onClick: () -> Unit) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(id = R.string.action_go_back),
            )
        }
    }
}
