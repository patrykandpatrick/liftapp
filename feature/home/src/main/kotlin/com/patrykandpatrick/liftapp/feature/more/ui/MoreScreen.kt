package com.patrykandpatrick.liftapp.feature.more.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.patrykandpatrick.liftapp.core.R
import com.patrykandpatrick.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatrick.liftapp.core.ui.CompactTopAppBar
import com.patrykandpatrick.liftapp.feature.more.model.Action
import com.patrykandpatrick.liftapp.feature.more.navigation.destinations
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItem
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppListItemPosition
import com.patrykandpatrick.liftapp.ui.component.LiftAppScaffold
import com.patrykandpatrick.liftapp.ui.dimens.LocalDimens
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
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding =
                PaddingValues(
                    start = LocalDimens.current.screen.padding,
                    top = LocalDimens.current.screen.padding,
                    end = LocalDimens.current.screen.padding,
                    bottom = LocalDimens.current.screen.padding,
                ),
        ) {
            itemsIndexed(destinations) { index, destination ->
                LiftAppListItem(
                    onClick = { onAction(Action.NavigateTo(destination)) },
                    position =
                        LiftAppListItemPosition(
                            index = index,
                            count = destinations.size,
                        ),
                    modifier = Modifier.fillMaxWidth(),
                    icon = {
                        LiftAppListItemDefaults.Icon {
                            Icon(imageVector = destination.imageVector, contentDescription = null)
                        }
                    },
                    title = {
                        LiftAppListItemDefaults.Title(
                            text = stringResource(id = destination.titleResourceId)
                        )
                    },
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
