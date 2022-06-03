package com.patrykandpatryk.liftapp.feature.settings.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.rememberTopAppBarScrollBehavior

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Settings(
    parentNavController: NavController,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = rememberTopAppBarScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_settings),
                scrollBehavior = topAppBarScrollBehavior,
                onBackClick = parentNavController::popBackStack,
            )
        },
    ) { paddingValues ->

        LazyColumn(contentPadding = paddingValues) { }
    }
}
