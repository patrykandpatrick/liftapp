package com.patrykandpatryk.liftapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.navOptions
import com.patrykandpatrick.liftapp.navigation.Routes
import com.patrykandpatrick.liftapp.ui.component.LiftAppCard
import com.patrykandpatrick.liftapp.ui.component.LiftAppCardDefaults
import com.patrykandpatrick.liftapp.ui.component.LiftAppHorizontalDivider
import com.patrykandpatrick.liftapp.ui.component.animateContainerColorsAsState
import com.patrykandpatrick.liftapp.ui.theme.colorScheme
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

    Column(modifier) {
        LiftAppHorizontalDivider()

        Row(
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier =
                Modifier.height(IntrinsicSize.Min)
                    .background(colorScheme.surface)
                    .padding(vertical = 6.dp, horizontal = 6.dp)
                    .navigationBarsPadding(),
        ) {
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
                    icon = menuRoute.icon,
                    label = stringResource(id = menuRoute.titleRes),
                )
            }
        }
    }
}

@Composable
fun RowScope.NavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
) {
    LiftAppCard(
        onClick = onClick,
        colors =
            animateContainerColorsAsState(
                    if (selected) LiftAppCardDefaults.tonalCardColors
                    else LiftAppCardDefaults.deselectedColors
                )
                .value,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        role = Role.Tab,
        modifier = Modifier.weight(1f).semantics { contentDescription = label },
    ) {
        Icon(imageVector = icon, contentDescription = null)

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

private fun NavItemRoute<*>.isSelected(currentDestination: NavDestination?): Boolean =
    currentDestination?.hierarchy?.any {
        it.route?.contains(checkNotNull(route::class.qualifiedName)) == true
    } == true
