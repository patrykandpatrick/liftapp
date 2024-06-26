package com.patrykandpatryk.liftapp.feature.more.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.feature.more.navigation.MoreNavigator
import com.patrykandpatryk.liftapp.feature.more.navigation.destinations

@Composable
fun MoreScreen(
    navigator: MoreNavigator,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .padding(padding)
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_more),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->

        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(destinations) { destination ->
                ListItem(
                    title = stringResource(id = destination.titleResourceId),
                    iconPainter = painterResource(id = destination.iconResourceId),
                ) { destination.navigate(navigator) }
            }
        }
    }
}
