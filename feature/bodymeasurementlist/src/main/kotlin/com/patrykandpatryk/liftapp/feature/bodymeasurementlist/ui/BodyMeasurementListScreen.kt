package com.patrykandpatryk.liftapp.feature.bodymeasurementlist.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.ListItem
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.resource.iconRes

@Composable
fun BodyMeasurementListScreen(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val viewModel: BodyMeasurementListViewModel = hiltViewModel()
    val items by viewModel.items.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_body),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->

        LazyColumn(modifier = Modifier.padding(paddingValues)) {

            items(
                items = items,
                key = { item -> item.bodyMeasurementID },
            ) { item ->

                ListItem(
                    title = item.headline,
                    description = item.supportingText,
                    iconPainter = painterResource(id = item.bodyMeasurementType.iconRes),
                    onClick = { navigate(Routes.BodyMeasurementDetails.create(item.bodyMeasurementID)) },
                )
            }
        }
    }
}
