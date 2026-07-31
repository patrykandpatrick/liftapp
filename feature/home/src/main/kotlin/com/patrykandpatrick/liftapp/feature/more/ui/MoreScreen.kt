package com.patrykandpatrick.liftapp.feature.more.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.core.ui.ListItem
import com.patrykandpatrick.liftapp.feature.more.model.Action
import com.patrykandpatrick.liftapp.feature.more.navigation.destinations
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
import com.patrykandpatrick.liftapp.ui.icons.ChevronRight
import com.patrykandpatrick.liftapp.ui.icons.LiftAppIcons
import com.patrykandpatrick.liftapp.ui.theme.LiftAppTheme

@Composable
fun MoreScreen(modifier: Modifier = Modifier) {
    val viewModel: MoreViewModel = hiltViewModel()

    MoreScreen(onAction = viewModel::onAction, modifier = modifier)
}

@Composable
private fun MoreScreen(onAction: (Action) -> Unit, modifier: Modifier = Modifier) {
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LiftAppScaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            CompactTopAppBar(
                title = { Text(stringResource(id = R.string.route_more)) },
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(destinations) { destination ->
                ListItem(
                    title = stringResource(id = destination.titleResourceId),
                    imageVector = destination.imageVector,
                    paddingValues =
                        PaddingValues(
                            horizontal = LocalDimens.current.screen.horizontalPadding,
                            vertical = 16.dp,
                        ),
                    actions = {
                        Icon(imageVector = LiftAppIcons.ChevronRight, contentDescription = null)
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
