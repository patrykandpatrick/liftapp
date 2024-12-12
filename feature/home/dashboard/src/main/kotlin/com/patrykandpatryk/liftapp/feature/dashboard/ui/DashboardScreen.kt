package com.patrykandpatryk.liftapp.feature.dashboard.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.feature.dashboard.navigation.DashboardNavigator

@Composable
fun DashboardScreen(
    navigator: DashboardNavigator,
    padding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier =
            modifier.padding(padding).nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_dashboard),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) {}
}
