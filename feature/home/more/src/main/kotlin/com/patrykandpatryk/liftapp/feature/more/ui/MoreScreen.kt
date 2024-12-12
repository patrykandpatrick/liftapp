package com.patrykandpatryk.liftapp.feature.more.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.extension.interfaceStub
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.feature.more.navigation.MoreNavigator
import com.patrykandpatryk.liftapp.feature.more.navigation.destinations

@Composable
fun MoreScreen(navigator: MoreNavigator, padding: PaddingValues, modifier: Modifier = Modifier) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier =
            modifier.padding(padding).nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(id = R.string.route_more)) },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(destinations) { destination ->
                ListItem(
                    title = stringResource(id = destination.titleResourceId),
                    iconPainter = painterResource(id = destination.iconResourceId),
                    actions = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_chevron_right),
                            contentDescription = null,
                        )
                    },
                ) {
                    destination.navigate(navigator)
                }
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun MoreScreenPreview() {
    LiftAppTheme { MoreScreen(navigator = interfaceStub(), padding = PaddingValues()) }
}
