@file:OptIn(ExperimentalMaterial3Api::class)

package pl.patrykgoworowski.mintlift.feature.main.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import pl.patrykgoworowski.mintlift.core.navigation.NavItemRoute
import pl.patrykgoworowski.mintlift.core.navigation.Routes
import pl.patrykgoworowski.mintlift.feature.main.navigation.navBarRoutes
import pl.patrykgoworowski.mintlift.core.ui.anim.EXIT_ANIM_DURATION
import pl.patrykgoworowski.mintlift.core.ui.anim.slideAndFadeIn

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Main(
    parentNavController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navController = rememberAnimatedNavController()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBarWithPadding(
                navController = navController,
                navItemRoutes = navBarRoutes,
            )
        },
    ) { paddingValues ->

        val navBarRoutes = navBarRoutes

        AnimatedNavHost(
            route = Routes.Menu.value,
            navController = navController,
            startDestination = navBarRoutes.first().route,
            enterTransition = { slideAndFadeIn<Int>() },
            exitTransition = {
                fadeOut(animationSpec = tween(durationMillis = EXIT_ANIM_DURATION))
            },
        ) {
            navBarRoutes.forEach { routeItem ->
                composable(
                    route = routeItem.route,
                ) { backStackEntry ->
                    routeItem.content(
                        entry = backStackEntry,
                        modifier = Modifier,
                        padding = paddingValues,
                        navigate = parentNavController::navigate,
                    )
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
    Surface(
        color = Color.Red,
        modifier = modifier
            .background(color = Color.Red)
            .navigationBarsPadding(),
    ) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()

        val currentDestination by derivedStateOf { currentBackStackEntry?.destination }

        NavigationBar {
            navItemRoutes.forEach { menuRoute ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any {
                        it.route == menuRoute.route
                    } ?: false,
                    onClick = { navController.navigate(menuRoute.route) },
                    icon = {
                        Icon(
                            painter = painterResource(id = menuRoute.iconRes),
                            contentDescription = stringResource(id = menuRoute.titleRes),
                        )
                    },
                    label = {
                        Text(text = stringResource(id = menuRoute.titleRes))
                    },
                )
            }
        }
    }
}
