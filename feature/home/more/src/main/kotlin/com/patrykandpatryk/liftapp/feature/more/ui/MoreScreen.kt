package com.patrykandpatryk.liftapp.feature.more.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.icons.ChevronForward
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.feature.more.model.Action
import com.patrykandpatryk.liftapp.feature.more.navigation.destinations

@Composable
fun MoreScreen(modifier: Modifier = Modifier) {
    val viewModel: MoreViewModel = hiltViewModel()

    MoreScreen(onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun MoreScreen(onAction: (Action) -> Unit, modifier: Modifier = Modifier) {
    LiftAppScaffold(
        modifier = modifier,
        topBar = { CompactTopAppBar(title = { Text(stringResource(id = R.string.route_more)) }) },
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(destinations) { destination ->
                ListItem(
                    title = stringResource(id = destination.titleResourceId),
                    iconPainter = painterResource(id = destination.iconResourceId),
                    actions = {
                        Icon(imageVector = LiftAppIcons.ChevronForward, contentDescription = null)
                    },
                    onClick = { onAction(Action.NavigateTo(destination)) },
                )
            }
        }
    }
}

@MultiDevicePreview
@Composable
private fun MoreScreenPreview() {
    LiftAppTheme { MoreScreen(onAction = {}) }
}
