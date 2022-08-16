package com.patrykandpatryk.liftapp.feature.body.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
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
import com.patrykandpatryk.liftapp.core.ui.topAppBarScrollBehavior

@Composable
fun Body(
    modifier: Modifier = Modifier,
    navigate: (String) -> Unit,
) {

    val topAppBarScrollBehavior = topAppBarScrollBehavior()

    val viewModel: BodyViewModel = hiltViewModel()
    val items by viewModel.bodyItems.collectAsState()

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
                key = { item -> item.id },
            ) { item ->

                ListItem(
                    title = item.title,
                    iconPainter = painterResource(id = item.iconRes),
                    onClick = { navigate(Routes.BodyDetails.create(item.id)) },
                )
            }
        }
    }
}
