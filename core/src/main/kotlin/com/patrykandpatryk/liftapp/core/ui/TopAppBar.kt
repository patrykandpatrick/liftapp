package com.patrykandpatryk.liftapp.core.ui

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun TopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val topAppBarColors = TopAppBarDefaults.largeTopAppBarColors()
    val scrollFraction = scrollBehavior.scrollFraction
    val containerColor by topAppBarColors.containerColor(scrollFraction)

    Column {

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = containerColor)
                .statusBarsPadding(),
        )

        LargeTopAppBar(
            scrollBehavior = scrollBehavior,
            title = { Text(text = title) },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {

                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = null,
                        )
                    }
                }
            },
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun topAppBarScrollBehavior(): TopAppBarScrollBehavior {

    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    return TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec = decayAnimationSpec,
        canScroll = { true },
        state = rememberTopAppBarScrollState(),
    )
}
