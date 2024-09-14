package com.patrykandpatryk.liftapp.feature.main.ui

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.patrykandpatryk.liftapp.core.logging.CollectSnackbarMessages
import com.patrykandpatryk.liftapp.core.navigation.NavItemRoute
import com.patrykandpatryk.liftapp.core.preview.MultiDevicePreview
import com.patrykandpatryk.liftapp.core.ui.anim.EXIT_ANIM_DURATION
import com.patrykandpatryk.liftapp.core.ui.anim.slideAndFadeIn
import com.patrykandpatryk.liftapp.core.ui.theme.LiftAppTheme
import com.patrykandpatryk.liftapp.feature.bodymeasurementlist.ui.BodyMeasurementListScreen
import com.patrykandpatryk.liftapp.feature.dashboard.ui.DashboardScreen
import com.patrykandpatryk.liftapp.feature.exercises.ui.ExerciseListScreen
import com.patrykandpatryk.liftapp.feature.main.HomeViewModel
import com.patrykandpatryk.liftapp.feature.main.navigation.Home
import com.patrykandpatryk.liftapp.feature.main.navigation.HomeRoute
import com.patrykandpatryk.liftapp.feature.main.navigation.navBarRoutes
import com.patrykandpatryk.liftapp.feature.main.navigation.rememberHomeNavigator
import com.patrykandpatryk.liftapp.feature.more.ui.MoreScreen
import com.patrykandpatryk.liftapp.feature.routines.RoutineListScreen

@Composable
fun Home(
    mainNavController: NavController,
    modifier: Modifier = Modifier,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val snackbarHostState = remember { SnackbarHostState() }
    CollectSnackbarMessages(messages = viewModel.messages, snackbarHostState = snackbarHostState)

    HomeScaffold(
        mainNavController = mainNavController,
        snackbarHostState = snackbarHostState,
        modifier = modifier,
    )
}

@Composable
private fun HomeScaffold(
    mainNavController: NavController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
    val homeNavigator = rememberHomeNavigator(navController = mainNavController)

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
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        NavHost(
            route = Home::class,
            navController = navController,
            startDestination = navBarRoutes.first().route,
            enterTransition = { slideAndFadeIn() },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = EXIT_ANIM_DURATION))
            },
        ) {
            navBarRoutes.forEach { routeItem ->
                when (routeItem.route) {
                    HomeRoute.BodyMeasurements ->
                        composable<HomeRoute.BodyMeasurements> {
                            BodyMeasurementListScreen(navigator = homeNavigator, padding = paddingValues)
                        }

                    HomeRoute.Dashboard ->
                        composable<HomeRoute.Dashboard> {
                            DashboardScreen(navigator = homeNavigator, padding = paddingValues)
                        }

                    HomeRoute.Exercises ->
                        composable<HomeRoute.Exercises> {
                            ExerciseListScreen(
                                navigator = homeNavigator,
                                pickingMode = false,
                                disabledExerciseIDs = emptyList(),
                                padding = paddingValues,
                            )
                        }

                    HomeRoute.More ->
                        composable<HomeRoute.More> {
                            MoreScreen(navigator = homeNavigator, padding = paddingValues)
                        }

                    HomeRoute.Routines ->
                        composable<HomeRoute.Routines> {
                            RoutineListScreen(navigator = homeNavigator, padding = paddingValues)
                        }
                }
            }
        }
    }
}

@Composable
private fun NavigationBarWithPadding(
    navController: NavController,
    navItemRoutes: Collection<NavItemRoute<HomeRoute>>,
    modifier: Modifier = Modifier,
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination by remember { derivedStateOf { currentBackStackEntry?.destination } }

    Column {
        HorizontalDivider()

        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = modifier,
        ) {
            navItemRoutes.forEach { menuRoute ->
                val selected by derivedStateOf {
                    currentDestination?.hierarchy?.any { it.route == menuRoute.route::class.qualifiedName } ?: false
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
}

@MultiDevicePreview
@Composable
fun HomePreview() {
    LiftAppTheme {
        HomeScaffold(
            snackbarHostState = remember { SnackbarHostState() },
            mainNavController = NavController(LocalContext.current),
        )
    }
}
