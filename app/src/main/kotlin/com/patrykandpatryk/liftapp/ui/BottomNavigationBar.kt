package com.patrykandpatryk.liftapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.navOptions
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatryk.liftapp.core.navigation.NavItemRoute
import com.patrykandpatryk.liftapp.navigation.BottomAppBarNavigator

@Composable
internal fun BottomNavigationBar(
    navController: NavController,
    navigator: BottomAppBarNavigator,
    navItemRoutes: Collection<NavItemRoute<Any>>,
    modifier: Modifier = Modifier,
) {
    val currentBackStackEntry by navigator.currentDestination.collectAsStateWithLifecycle(null)
    val currentDestination by remember { derivedStateOf { currentBackStackEntry?.destination } }

    Column(modifier.shadow(8.dp)) {
        HorizontalDivider()

        NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
            navItemRoutes.forEach { menuRoute ->
                val selected by derivedStateOf { menuRoute.isSelected(currentDestination) }
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (selected) return@NavigationBarItem
                        navController.navigate(
                            menuRoute.route,
                            navOptions { popUpTo<Routes.Home>() },
                        )
                    },
                    icon = {
                        Icon(
                            painter =
                                painterResource(
                                    id =
                                        if (selected) menuRoute.selectedIconRes
                                        else menuRoute.deselectedIconRes
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

private fun NavItemRoute<*>.isSelected(currentDestination: NavDestination?): Boolean =
    currentDestination?.hierarchy?.any {
        it.route?.contains(checkNotNull(route::class.qualifiedName)) == true
    } == true
