package com.patrykandpatryk.liftapp.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisEnterTransition
import com.patrykandpatryk.liftapp.core.ui.animation.sharedXAxisExitTransition

@Composable
fun BottomAppBarNavigationHost(
    navController: NavHostController,
    navigator: BottomAppBarNavigator,
    navigationBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isBottomAppBarNavigator =
        navController.currentBackStackEntryAsState().value?.destination?.navigatorName ==
            BottomAppBarNavigator.NAME

    val isNavigationForward = remember { mutableStateOf(true) }

    LaunchedEffect(navController.currentBackStackEntryFlow) {
        var previousBackStackEntry: NavBackStackEntry? = null
        navController.currentBackStackEntryFlow.collect { entry ->
            isNavigationForward.value = entry != previousBackStackEntry
            previousBackStackEntry = navController.previousBackStackEntry
        }
    }

    BackHandler(navController.previousBackStackEntry != null) { navController.popBackStack() }

    AnimatedContent(
        targetState = isBottomAppBarNavigator,
        transitionSpec = {
            sharedXAxisEnterTransition(forward = isNavigationForward.value) togetherWith
                sharedXAxisExitTransition(forward = isNavigationForward.value)
        },
        modifier = modifier.fillMaxSize(),
    ) { isBottomAppBarNavigator ->
        if (isBottomAppBarNavigator) {
            navigator.Content(navigationBar = navigationBar)
        } else {
            content()
        }
    }
}
