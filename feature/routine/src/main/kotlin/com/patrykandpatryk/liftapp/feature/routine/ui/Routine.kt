package com.patrykandpatryk.liftapp.feature.routine.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import com.patrykandpatryk.liftapp.core.R
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.ui.TopAppBar

fun NavGraphBuilder.addRoutine(
    navigateBack: () -> Unit,
    navigate: (String) -> Unit,
) {
    composable(
        route = Routes.Routine,
    ) {

        Routine(
            navigateBack = navigateBack,
            navigate = navigate,
        )
    }
}

@Suppress("UnusedPrivateMember")
@Composable
fun Routine(
    navigateBack: () -> Unit,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = stringResource(id = R.string.route_routine),
                onBackClick = navigateBack,
                scrollBehavior = topAppBarScrollBehavior,
            )
        },
    ) { paddingValues ->

        LazyColumn(
            contentPadding = paddingValues,
        ) {}
    }
}
