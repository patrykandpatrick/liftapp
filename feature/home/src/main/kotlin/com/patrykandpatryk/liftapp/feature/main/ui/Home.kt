package com.patrykandpatryk.liftapp.feature.main.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.navigation.NavItemRoute
import com.patrykandpatryk.liftapp.core.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.composable
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.provider.navigator
import com.patrykandpatryk.liftapp.core.ui.anim.EXIT_ANIM_DURATION
import com.patrykandpatryk.liftapp.core.ui.anim.slideAndFadeIn
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.feature.main.HomeViewModel
import com.patrykandpatryk.liftapp.feature.main.navigation.navBarRoutes

fun NavGraphBuilder.addHome() {
    composable(route = Routes.Home) {
        Home()
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    HomeScaffold(
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun HomeScaffold(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            NavigationBarWithPadding(
                navController = navController,
                navItemRoutes = navBarRoutes,
            )
        },
    ) { paddingValues ->

        val navigator = navigator

        NavHost(
            route = Routes.Home.value,
            navController = navController,
            startDestination = navBarRoutes.first().route,
            enterTransition = { slideAndFadeIn() },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = EXIT_ANIM_DURATION))
            },
        ) {
            navBarRoutes.forEach { routeItem ->
                composable(
                    route = routeItem.route,
                ) { backStackEntry ->
                    routeItem.content(backStackEntry, Modifier, paddingValues, navigator::navigate)
                }
            }
        }
    }
}

@Composable
private fun NavigationBarWithPadding(
    navController: NavController,
    navItemRoutes: Collection<NavItemRoute>,
    modifier: Modifier = Modifier,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination by remember {
        derivedStateOf { currentBackStackEntry?.destination }
    }

    NavigationBar(modifier = modifier) {
        navItemRoutes.forEach { menuRoute ->
            val selected by derivedStateOf {
                currentDestination?.hierarchy?.any { it.route == menuRoute.route } ?: false
            }
            NavigationBarItem(
                selected = selected,
                onClick = { navController.navigate(menuRoute.route) },
                icon = {
                    Icon(
                        painter = painterResource(
                            id = if (selected) menuRoute.selectedIconRes else menuRoute.deselectedIconRes,
                        ),
                        contentDescription = stringResource(id = menuRoute.titleRes),
                        tint = LocalContentColor.current,
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = menuRoute.titleRes),
                        textAlign = TextAlign.Center,
                    )
                },
                alwaysShowLabel = false,
            )
        }
    }
}

@MultiDevicePreview
@Composable
fun HomePreview() {
    LiftAppTheme {
        HomeScaffold(
            snackbarHostState = remember { SnackbarHostState() },
        )
    }
}
