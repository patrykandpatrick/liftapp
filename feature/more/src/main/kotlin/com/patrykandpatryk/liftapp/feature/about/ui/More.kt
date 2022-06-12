package com.patrykandpatryk.liftapp.feature.about.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.ui.TopAppBar
import com.patrykandpatryk.liftapp.core.ui.topAppBarScrollBehavior

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun More(
    modifier: Modifier,
    navigate: (String) -> Unit,
) {
    val topAppBarScrollBehavior = topAppBarScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_more),
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues)) {
            Button(onClick = { navigate(Routes.Settings.value) }) {
                Text(text = stringResource(id = R.string.route_settings))
            }
            Button(onClick = { navigate(Routes.About.value) }) {
                Text(text = stringResource(id = R.string.route_about))
            }
            Button(onClick = { navigate(Routes.OneRepMax.value) }) {
                Text(text = stringResource(id = R.string.route_one_rep_max))
            }
        }
    }
}
